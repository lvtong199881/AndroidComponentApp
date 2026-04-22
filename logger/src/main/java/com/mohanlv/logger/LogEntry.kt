package com.mohanlv.logger

/**
 * 日志条目
 */
data class LogEntry(
    val level: Level,
    val tag: String,
    val message: String,
    val thread: String,
    val time: Long = System.currentTimeMillis()
) {
    
    enum class Level(val value: Int, val label: Char) {
        VERBOSE(2, 'V'),
        DEBUG(3, 'D'),
        INFO(4, 'I'),
        WARN(5, 'W'),
        ERROR(6, 'E');
        
        companion object {
            fun fromChar(char: Char): Level = entries.find { it.label == char } ?: DEBUG
        }
    }
}
