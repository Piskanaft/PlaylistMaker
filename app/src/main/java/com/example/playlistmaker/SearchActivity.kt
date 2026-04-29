package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.model.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

val tracks = arrayListOf(
    Track(
        trackName = "Smells Like Teen Spirit",
        artistName = "Nirvana",
        trackTime = "5:01",
        artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
    ),
    Track(
        trackName = "Billie Jean",
        artistName = "Michael Jackson",
        trackTime = "4:35",
        artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
    ),
    Track(
        trackName = "Stayin' Alive",
        artistName = "Bee Gees",
        trackTime = "4:10",
        artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
    ),
    Track(
        trackName = "Whole Lotta Love",
        artistName = "Led Zeppelin",
        trackTime = "5:33",
        artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
    ),
    Track(
        trackName = "Sweet Child O'Mine",
        artistName = "Guns N' Roses",
        trackTime = "5:03",
        artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
    )
)

class SearchActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    private val searchInputLayout: TextInputLayout by lazy { findViewById(R.id.search_input_layout) }
    private val searchInput: TextInputEditText by lazy { findViewById(R.id.search_input) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }
    private var searchInputText = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBar.top)
            insets
        }
        setTextWatcher()
        setListeners()
        recyclerView.adapter = TrackAdapter(tracks)
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchInputLayout.isEndIconVisible = !s.isNullOrEmpty()
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
            searchInput.clearFocus()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchInput.windowToken, 0)
        }

    }
}

