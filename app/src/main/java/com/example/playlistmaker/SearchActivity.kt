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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class SearchActivity : AppCompatActivity() {
    private val toolbar: MaterialToolbar by lazy { findViewById(R.id.toolbar) }
    private val searchInputLayout: TextInputLayout by lazy { findViewById(R.id.search_input_layout) }
    private val searchInput: TextInputEditText by lazy { findViewById(R.id.search_input) }
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

