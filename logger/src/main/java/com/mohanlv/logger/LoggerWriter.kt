package com.mohanlv.logger

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Queue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * 异步日志写入器，后台单线程负责写出到文件
 */
class LoggerWriter private constructor() {
    
    private val running = AtomicBoolean(false)
    private val queue: BlockingQueue<LogEntry> = LinkedBlockingQueue(LoggerConfig.queueCapacity)
    
    // 当前写入的文件
    private var currentDate: String = ""
    private var currentFile: File? = null
    private var currentFileIndex: Int = 0
    private var currentFileSize: AtomicLong = AtomicLong(0)
    private var writer: OutputStreamWriter? = null
    
    private var thread: Thread? = null
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    
    companion object {
        val instance: LoggerWriter by lazy { LoggerWriter() }
    }
    
    fun start() {
        if (running.compareAndSet(false, true)) {
            ensureLogDir()
            thread = Thread({ runLoop() }, "LoggerWriter").apply { start() }
        }
    }
    
    fun stop() {
        if (running.compareAndSet(true, false)) {
            // 剩余日志全部写出
            flushQueue()
            closeFile()
            thread?.join(3000)
            thread = null
        }
    }
    
    fun enqueue(entry: LogEntry): Boolean {
        if (!LoggerConfig.enable) return false
        val offer = queue.offer(entry)
        // 积压超过阈值则强制刷盘
        if (queue.size >= LoggerConfig.flushThreshold) {
            wakeUp()
        }
        return offer
    }
    
    fun enqueueBlocking(entry: LogEntry) {
        if (!LoggerConfig.enable) return
        try {
            queue.put(entry)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
    
    private fun wakeUp() {
        thread?.interrupt()
    }
    
    private fun runLoop() {
        while (running.get()) {
            try {
                val entry = queue.poll(1, java.util.concurrent.TimeUnit.SECONDS)
                if (entry != null) {
                    writeEntry(entry)
                }
            } catch (e: InterruptedException) {
                // 被 interrupt，继续循环检查 running 状态
            }
        }
        // 退出前把队列里剩余的都写出
        flushQueue()
    }
    
    private fun flushQueue() {
        while (true) {
            val entry = queue.poll() ?: break
            writeEntry(entry)
        }
    }
    
    private fun writeEntry(entry: LogEntry) {
        // 检查日期是否变化，变化则换文件
        val dateStr = dateFormat.format(Date(entry.time))
        if (dateStr != currentDate) {
            closeFile()
            currentDate = dateStr
            currentFileIndex = 0
        }
        
        ensureFile()
        
        val line = formatLine(entry)
        try {
            writer?.write(line)
            currentFileSize.addAndGet(line.length.toLong())
            
            // 超过单文件上限则关闭当前文件，后续请求会创建新文件
            if (currentFileSize.get() >= LoggerConfig.maxFileSize) {
                closeFile()
                currentFileIndex++
            }
            
            // 每次写完刷新一下，降低丢数据风险
            writer?.flush()
        } catch (e: IOException) {
            Log.e("LoggerWriter", "write failed", e)
        }
    }
    
    private fun formatLine(entry: LogEntry): String {
        val timeStr = timeFormat.format(Date(entry.time))
        val levelChar = entry.level.label
        return "$timeStr $levelChar/${entry.tag}: ${entry.message} [${entry.thread}]\n"
    }
    
    private fun ensureLogDir() {
        if (!LoggerConfig.logDir.exists()) {
            LoggerConfig.logDir.mkdirs()
        }
    }
    
    private fun ensureFile() {
        if (currentFile != null && writer != null) return
        
        val fileName = if (currentFileIndex == 0) {
            "${LoggerConfig.filePrefix}_$currentDate.log"
        } else {
            "${LoggerConfig.filePrefix}_${currentDate}_$currentFileIndex.log"
        }
        
        currentFile = File(LoggerConfig.logDir, fileName)
        
        try {
            val fileSize = currentFile?.length() ?: 0L
            currentFileSize.set(fileSize)
            writer = OutputStreamWriter(
                FileOutputStream(currentFile, true),
                StandardCharsets.UTF_8
            )
        } catch (e: IOException) {
            Log.e("LoggerWriter", "open file failed", e)
        }
    }
    
    private fun closeFile() {
        try {
            writer?.flush()
            writer?.close()
        } catch (e: IOException) {
            Log.e("LoggerWriter", "close file failed", e)
        } finally {
            writer = null
            currentFile = null
            currentFileSize.set(0)
        }
    }
    
    /**
     * 清理过期日志文件
     */
    fun cleanExpiredFiles() {
        val files = LoggerConfig.logDir.listFiles() ?: return
        val cutoffTime = System.currentTimeMillis() - LoggerConfig.retainDays * 24 * 60 * 60 * 1000L
        
        files.filter { it.isFile && it.name.startsWith(LoggerConfig.filePrefix) }
            .filter { it.lastModified() < cutoffTime }
            .forEach { file ->
                if (file.delete()) {
                    Log.i("LoggerWriter", "deleted expired log: ${file.name}")
                }
            }
    }
}
