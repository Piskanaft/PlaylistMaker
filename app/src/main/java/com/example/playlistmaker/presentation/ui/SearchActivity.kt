package com.example.playlistmaker.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.Creator
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@SuppressLint("NotifyDataSetChanged")
class SearchActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.toolbar) }
    private val searchInputLayout: TextInputLayout by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.search_input_layout
        )
    }
    private val searchInput: TextInputEditText by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.search_input
        )
    }
    private val resultsRecyclerView: RecyclerView by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.resultsRecyclerView
        )
    }
    private val searchHistoryRecyclerView: RecyclerView by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.searchHistoryRecyclerView
        )
    }

    private val searchStatusBlock: LinearLayout by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.searchStatusBlock
        )
    }
    private val statusImage: ImageView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.statusImage) }
    private val statusText: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.statusText) }
    private val statusButton: Button by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.statusButton) }
    private val historyGroup: ConstraintLayout by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.searchHistoryViewGroup
        )
    }
    private val clearHistoryButton: MaterialButton by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(
            R.id.clearHistoryButton
        )
    }
    private val progressBar: ProgressBar by lazy(mode = LazyThreadSafetyMode.NONE) {
        findViewById(R.id.progressBar)
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }
    private lateinit var resultsTrackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val resultTracks = arrayListOf<Track>()
    private val historyTracks = arrayListOf<Track>()

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor by lazy {
        Creator.provideSearchHistoryInteractor(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.updatePadding(top = statusBar.top, bottom = maxOf(navBar.bottom, ime.bottom))
            insets
        }
        setTextWatcher()
        setListeners()

        resultsTrackAdapter = TrackAdapter(resultTracks) { clickedTrack ->
            searchHistoryInteractor.addTrack(clickedTrack)
            historyTracks.clear()
            historyTracks.addAll(searchHistoryInteractor.getHistory())
            historyAdapter.notifyDataSetChanged()
            if (clickDebounce()) {
                handler.removeCallbacks(searchRunnable)
                startActivity(TrackActivity.intentFactory(this, clickedTrack))
            }
        }
        resultsRecyclerView.adapter = resultsTrackAdapter

        historyTracks.addAll(searchHistoryInteractor.getHistory())
        historyAdapter = TrackAdapter(historyTracks) { clickedTrack ->
            searchHistoryInteractor.addTrack(clickedTrack)
            historyTracks.clear()
            historyTracks.addAll(searchHistoryInteractor.getHistory())
            historyAdapter.notifyDataSetChanged()

            if (clickDebounce()) {
                handler.removeCallbacks(searchRunnable)
                startActivity(TrackActivity.intentFactory(this, clickedTrack))
            }
        }
        searchHistoryRecyclerView.adapter = historyAdapter

        historyAdapter.notifyDataSetChanged()
        updateHistoryState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT_TEXT, searchInput.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val gotText = savedInstanceState.getString(SEARCH_INPUT_TEXT, TEXT_DEF)
        searchInput.setText(gotText)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun setTextWatcher() {
        searchInputLayout.isEndIconVisible = false
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInputLayout.isEndIconVisible = !s.isNullOrEmpty()
                searchStatusBlock.visibility = View.GONE
                searchDebounce()
                if (s.isNullOrEmpty()) {
                    resultTracks.clear()
                    resultsTrackAdapter.notifyDataSetChanged()
                    resultsRecyclerView.visibility = View.GONE
                }
                updateHistoryState()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchInput.addTextChangedListener(simpleTextWatcher)
    }

    private fun setListeners() {

        toolbar.setNavigationOnClickListener { finish() }
        searchInputLayout.setEndIconOnClickListener {
            searchInput.text?.clear()
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                searchRequest()
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
            }
            true
        }
        searchInput.setOnFocusChangeListener { _, _ ->
            updateHistoryState()
        }
        statusButton.setOnClickListener {
            searchStatusBlock.visibility = View.GONE
            searchRequest()
        }
        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyTracks.clear()
            historyAdapter.notifyDataSetChanged()
            historyGroup.visibility = View.GONE
        }

    }

    private fun searchRequest() {
        val query = searchInput.text?.toString()?.trim()
        if (!query.isNullOrEmpty()) {
            searchStatusBlock.visibility = View.GONE
            resultsRecyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE


            tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {

                    handler.post {
                        progressBar.visibility = View.GONE
                        resultTracks.clear()
                        if (foundTracks == null) {
                            showStatus(
                                R.drawable.search_network_issue,
                                R.string.search_network_issue
                            )
                        } else
                            if (foundTracks.isNotEmpty()) {
                                resultTracks.addAll(foundTracks)
                                resultsTrackAdapter.notifyDataSetChanged()
                                resultsRecyclerView.visibility = View.VISIBLE
                                searchStatusBlock.visibility = View.GONE
                            } else {
                                showStatus(
                                    R.drawable.search_nothing_found,
                                    R.string.search_nothing_found
                                )
                            }
                    }
                }
            })
        }
    }


    private fun showStatus(imageResId: Int, messageId: Int) {

        resultTracks.clear()
        resultsTrackAdapter.notifyDataSetChanged()
        resultsRecyclerView.visibility = View.GONE
        statusImage.setImageResource(imageResId)
        statusText.text = getString(messageId)
        if (imageResId == R.drawable.search_network_issue) {
            statusButton.visibility = View.VISIBLE
        } else {
            statusButton.visibility = View.GONE
        }
        searchStatusBlock.visibility = View.VISIBLE
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (searchInput.text?.isNotEmpty() == true) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun updateHistoryState() {
        val hasFocus = searchInput.hasFocus()
        val inputIsEmpty = searchInput.text.isNullOrEmpty()
        if (hasFocus && inputIsEmpty && historyTracks.isNotEmpty()) {
            historyGroup.visibility = View.VISIBLE
            searchStatusBlock.visibility = View.GONE
        } else {
            historyGroup.visibility = View.GONE
        }
    }

    companion object {
        private const val SEARCH_INPUT_TEXT = "SEARCH_INPUT_TEXT"
        private const val TEXT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}

