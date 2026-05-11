package com.mohanlv.shortvideo.ui.search

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mohanlv.base.base.BaseFragment
import com.mohanlv.logger.Logger
import com.mohanlv.router.RouterManager
import com.mohanlv.router.annotation.Route
import com.mohanlv.shortvideo.R
import com.mohanlv.shortvideo.api.PexelsApiClient
import com.mohanlv.shortvideo.databinding.FragmentSearchPhotoBinding
import com.mohanlv.shortvideo.databinding.PanelFilterBinding
import com.mohanlv.shortvideo.model.Photo
import com.mohanlv.shortvideo.navigateToDetail
import com.mohanlv.shortvideo.ui.photos.PhotoAdapter
import kotlinx.coroutines.launch

/**
 * 图片搜索页面
 */
@Route(path = "oneandroid://shortvideo/photos/search", description = "搜索图片")
class SearchPhotoFragment : BaseFragment<FragmentSearchPhotoBinding>() {

    private val photos = mutableListOf<Photo>()
    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(
            photos = photos,
            onItemClick = { photo ->
                navigateToDetail(photo = photo)
            }
        )
    }

    private var currentPage = 1
    private var isLoading = false
    private var hasMoreData = true
    private var currentQuery = ""

    private val chipMap = mutableMapOf<Int, Chip>()

    private var currentOrientation = ""
    private var currentSize = ""
    private var currentColor = ""

    private var filterPanelPopup: FilterPanelPopup? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchPhotoBinding {
        return FragmentSearchPhotoBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
        setupFilterChips()
        // 自动拉起键盘
        binding.editSearch.requestFocus()
        binding.editSearch.postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editSearch, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = photoAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as? StaggeredGridLayoutManager ?: return
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItems = layoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = lastVisibleItems.maxOrNull() ?: 0

                    if (lastVisibleItem >= totalItemCount - 3 && !isLoading && hasMoreData) {
                        loadMorePhotos()
                    }
                }
            })
        }
    }

    private fun setupSearchBar() {
        binding.btnBack.setOnClickListener {
            RouterManager.popBackStack()
        }

        binding.btnClear.setOnClickListener {
            binding.editSearch.setText("")
        }

        binding.editSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                binding.btnClear.visibility = if (s.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
            }
        })

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.editSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun setupFilterChips() {
        addFilterChip(R.string.filter_orientation)
        addFilterChip(R.string.filter_size)
        addFilterChip(R.string.filter_color)

        binding.btnMenu.setOnClickListener {
            showFilterPanel()
        }
    }

    private fun showFilterPanel() {
        if (filterPanelPopup?.isShowing == true) {
            filterPanelPopup?.dismiss()
        } else {
            filterPanelPopup?.dismiss()
            filterPanelPopup = FilterPanelPopup(
                context = requireContext(),
                currentOrientation = currentOrientation,
                currentSize = currentSize,
                currentColor = currentColor,
                onReset = {
                    updateChipStates()
                    if (currentQuery.isNotEmpty()) {
                        performSearch()
                    }
                },
                onConfirm = { orientation, size, color ->
                    currentOrientation = orientation
                    currentSize = size
                    currentColor = color
                    updateChipStates()
                    if (currentQuery.isNotEmpty()) {
                        performSearch()
                    }
                }
            )
            filterPanelPopup?.show(binding.btnMenu)
        }
    }

    private fun updateChipStates() {
        updateSingleChipState(R.string.filter_orientation, currentOrientation)
        updateSingleChipState(R.string.filter_size, currentSize)
        updateSingleChipState(R.string.filter_color, currentColor)
    }

    private fun updateSingleChipState(titleRes: Int, value: String) {
        val chip = chipMap[titleRes] ?: return
        if (value.isEmpty()) {
            chip.text = getString(titleRes)
            chip.setChipBackgroundColorResource(R.color.chip_background_unselected)
            chip.setTextColor(resources.getColor(R.color.text_primary, null))
            chip.isCloseIconVisible = false
        } else {
            val optionText = getFilterOptions(titleRes).find { it.second == value }?.first ?: ""
            chip.text = getString(titleRes) + ": " + optionText
            chip.setChipBackgroundColorResource(R.color.chip_background_selected)
            chip.setTextColor(resources.getColor(android.R.color.white, null))
            chip.isCloseIconVisible = true
        }
    }

    private fun addFilterChip(titleRes: Int) {
        val chip = Chip(requireContext()).apply {
            text = getString(titleRes)
            isCheckable = false
            setChipBackgroundColorResource(R.color.chip_background_unselected)
            setTextColor(resources.getColor(R.color.text_primary, null))
            isCloseIconVisible = false
            closeIconTint = ColorStateList.valueOf(Color.WHITE)
            setCloseIconResource(R.drawable.ic_chip_close)
            setOnClickListener { view ->
                showFilterPopup(view as Chip, titleRes)
            }
            setOnCloseIconClickListener {
                clearFilter(this, titleRes)
            }
        }
        chipMap[titleRes] = chip
        binding.chipGroup.addView(chip)
    }

    private fun clearFilter(chip: Chip, titleRes: Int) {
        chip.text = getString(titleRes)
        chip.setChipBackgroundColorResource(R.color.chip_background_unselected)
        chip.setTextColor(resources.getColor(R.color.text_primary, null))
        chip.isCloseIconVisible = false
        updateFilterParam(titleRes, "")
        if (currentQuery.isNotEmpty()) {
            performSearch()
        }
    }

    private fun showFilterPopup(anchorChip: Chip, titleRes: Int) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_filter, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 8f
            setBackgroundDrawable(resources.getDrawable(R.drawable.bg_popup_filter, null))
        }

        val titleText = popupView.findViewById<TextView>(R.id.textTitle)
        val container = popupView.findViewById<ViewGroup>(R.id.containerOptions)
        titleText.text = getString(titleRes)

        val options = getFilterOptions(titleRes)
        container.removeAllViews()
        for (option in options) {
            val optionView = LayoutInflater.from(requireContext()).inflate(R.layout.item_filter_option, container, false)
            val textView = optionView.findViewById<TextView>(R.id.textOption)
            textView.text = option.first
            textView.setOnClickListener {
                anchorChip.text = getString(titleRes) + ": " + option.first
                anchorChip.setChipBackgroundColorResource(R.color.chip_background_selected)
                anchorChip.setTextColor(resources.getColor(android.R.color.white, null))
                anchorChip.isCloseIconVisible = true
                updateFilterParam(titleRes, option.second)
                popupWindow.dismiss()
                if (currentQuery.isNotEmpty()) {
                    performSearch()
                }
            }
            container.addView(optionView)
        }

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popupWindow.showAsDropDown(anchorChip, 0, 0)
    }

    private fun getFilterOptions(titleRes: Int): List<Pair<String, String>> {
        return when (titleRes) {
            R.string.filter_orientation -> listOf(
                getString(R.string.filter_orientation_landscape) to "landscape",
                getString(R.string.filter_orientation_portrait) to "portrait",
                getString(R.string.filter_orientation_square) to "square"
            )
            R.string.filter_size -> listOf(
                getString(R.string.filter_size_large) to "large",
                getString(R.string.filter_size_medium) to "medium",
                getString(R.string.filter_size_small) to "small"
            )
            R.string.filter_color -> listOf(
                getString(R.string.filter_color_black) to "black",
                getString(R.string.filter_color_black_white) to "black_and_white",
                getString(R.string.filter_color_red) to "red",
                getString(R.string.filter_color_orange) to "orange",
                getString(R.string.filter_color_yellow) to "yellow",
                getString(R.string.filter_color_green) to "green",
                getString(R.string.filter_color_turquoise) to "turquoise",
                getString(R.string.filter_color_blue) to "blue",
                getString(R.string.filter_color_violet) to "violet",
                getString(R.string.filter_color_pink) to "pink",
                getString(R.string.filter_color_brown) to "brown"
            )
            else -> emptyList()
        }
    }

    private fun updateFilterParam(titleRes: Int, value: String) {
        when (titleRes) {
            R.string.filter_orientation -> currentOrientation = value
            R.string.filter_size -> currentSize = value
            R.string.filter_color -> currentColor = value
        }
    }

    private fun performSearch() {
        val query = binding.editSearch.text.toString().trim()
        if (query.isEmpty()) {
            return
        }
        currentQuery = query
        // 隐藏键盘
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editSearch.windowToken, 0)
        searchPhotos(query, currentOrientation, currentSize, currentColor)
    }

    private fun searchPhotos(query: String, orientation: String, size: String, color: String) {
        if (isLoading) return
        isLoading = true
        currentPage = 1

        showLoadingView()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = PexelsApiClient.apiService.searchPhotos(
                    query = query,
                    page = 1,
                    perPage = 20,
                    orientation = orientation.ifEmpty { null },
                    size = size.ifEmpty { null },
                    color = color.ifEmpty { null }
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.photos.isNullOrEmpty()) {
                        photos.clear()
                        photos.addAll(body.photos)
                        photoAdapter.notifyDataSetChanged()
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                        binding.layoutEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    } else {
                        photos.clear()
                        photoAdapter.notifyDataSetChanged()
                        showEmptyView()
                    }
                } else {
                    Logger.e("SearchPhotoFragment", "搜索照片失败: ${response.code()}")
                    showEmptyView()
                }
            } catch (e: Exception) {
                Logger.e("SearchPhotoFragment", "搜索照片异常", e)
                showEmptyView()
            } finally {
                isLoading = false
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadMorePhotos() {
        if (isLoading || !hasMoreData || currentQuery.isEmpty()) return
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val nextPage = currentPage + 1
                val response = PexelsApiClient.apiService.searchPhotos(
                    query = currentQuery,
                    page = nextPage,
                    perPage = 20,
                    orientation = currentOrientation.ifEmpty { null },
                    size = currentSize.ifEmpty { null },
                    color = currentColor.ifEmpty { null }
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.photos.isNullOrEmpty()) {
                        val startPosition = photos.size
                        photos.addAll(body.photos)
                        photoAdapter.notifyItemRangeInserted(startPosition, body.photos.size)
                        currentPage = nextPage
                        hasMoreData = !body.nextPage.isNullOrEmpty()
                    } else {
                        hasMoreData = false
                    }
                }
            } catch (e: Exception) {
                Logger.e("SearchPhotoFragment", "加载更多照片异常", e)
            } finally {
                isLoading = false
            }
        }
    }

    private fun showLoadingView() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.layoutEmpty.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.visibility = if (photos.isNotEmpty()) View.VISIBLE else View.GONE
    }
}