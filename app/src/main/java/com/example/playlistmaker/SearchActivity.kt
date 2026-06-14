package com.example.playlistmaker

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.api.ITunesApi
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.model.TrackResponse
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val iTunesBaseUrl = "https://itunes.apple.com/"


class SearchActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.toolbar) }
    private val searchInputLayout: TextInputLayout by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.search_input_layout) }
    private val searchInput: TextInputEditText by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.search_input) }
    private val resultsRecyclerView: RecyclerView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.resultsRecyclerView) }
    private val searchHistoryRecyclerView: RecyclerView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.searchHistoryRecyclerView) }

    private val searchStatusBlock: LinearLayout by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.searchStatusBlock) }
    private val statusImage: ImageView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.statusImage) }
    private val statusText: TextView by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.statusText) }
    private val statusButton: Button by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.statusButton) }
    private val historyGroup: ConstraintLayout by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.searchHistoryViewGroup) }
    private val clearHistoryButton: MaterialButton by lazy(mode = LazyThreadSafetyMode.NONE) { findViewById(R.id.clearHistoryButton) }

    private var searchInputText = ""
    private var searchCall: Call<TrackResponse>? = null
    private lateinit var resultsTrackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    val resultTracks = arrayListOf<Track>()

    val sharedPreferences: SharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        getSharedPreferences(
            PLAYLISTMAKER_PREFERENCES, MODE_PRIVATE
        )
    }
    private val searchHistory by lazy(mode = LazyThreadSafetyMode.NONE) {
        SearchHistory(sharedPreferences)
    }
    private val retrofit by lazy(mode = LazyThreadSafetyMode.NONE) {
        Retrofit.Builder().baseUrl(iTunesBaseUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val iTunesService by lazy(mode = LazyThreadSafetyMode.NONE) { retrofit.create(ITunesApi::class.java) }

    val searchCallback = object : Callback<TrackResponse> {
        override fun onResponse(
            call: Call<TrackResponse>, response: Response<TrackResponse>
        ) {
            if (response.isSuccessful) {
                resultTracks.clear()
                val downloadedTracks = response.body()?.results

                if (!downloadedTracks.isNullOrEmpty()) {
                    resultTracks.addAll(downloadedTracks)
                    resultsTrackAdapter.notifyDataSetChanged()
                    resultsRecyclerView.visibility = View.VISIBLE
                    searchStatusBlock.visibility = View.GONE
                } else {
                    showStatus(
                        R.drawable.search_nothing_found, R.string.search_nothing_found
                    )
                }
            } else {
                showStatus(
                    R.drawable.search_network_issue, R.string.search_network_issue
                )
            }
        }

        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
            showStatus(
                R.drawable.search_network_issue, R.string.search_network_issue
            )
        }

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
            searchHistory.addTrack(clickedTrack)
            historyAdapter.notifyDataSetChanged()
            startActivity(TrackActivity.intentFactory(this, clickedTrack))
        }
        resultsRecyclerView.adapter = resultsTrackAdapter

        historyAdapter = TrackAdapter(searchHistory.historyTracks) { clickedTrack ->
            searchHistory.addTrack(clickedTrack)
            historyAdapter.notifyDataSetChanged()
            startActivity(TrackActivity.intentFactory(this, clickedTrack))
        }
        searchHistoryRecyclerView.adapter = historyAdapter

        historyAdapter.notifyDataSetChanged()
        updateHistoryState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT_TEXT, searchInputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val gotText = savedInstanceState.getString(SEARCH_INPUT_TEXT, TEXT_DEF)
        searchInput.setText(gotText)
    }

    companion object {
        private const val SEARCH_INPUT_TEXT = "SEARCH_INPUT_TEXT"
        private const val TEXT_DEF = ""
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
                if (s.isNullOrEmpty()) {
                    resultTracks.clear()
                    resultsTrackAdapter.notifyDataSetChanged()
                    searchStatusBlock.visibility = View.GONE
                    resultsRecyclerView.visibility = View.GONE
                }
                updateHistoryState()
            }

            override fun afterTextChanged(s: Editable?) {
                searchInputText = s.toString()
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
                val query = searchInput.text?.toString()?.trim()
                if (!query.isNullOrEmpty()) {
                    searchStatusBlock.visibility = View.GONE
                    searchCall = iTunesService.search(query)
                    searchCall?.enqueue(searchCallback)
                }
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
            searchCall = searchCall?.clone()
            searchCall?.enqueue(searchCallback)
        }
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyGroup.visibility = View.GONE
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

    private fun updateHistoryState() {
        val hasFocus = searchInput.hasFocus()
        val inputIsEmpty = searchInput.text.isNullOrEmpty()
        if (hasFocus && inputIsEmpty && searchHistory.historyTracks.isNotEmpty()) {
            historyGroup.visibility = View.VISIBLE
            searchStatusBlock.visibility = View.GONE
        } else {
            historyGroup.visibility = View.GONE
        }
    }
}

