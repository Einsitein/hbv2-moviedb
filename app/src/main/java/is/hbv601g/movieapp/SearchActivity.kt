package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601g.movieapp.adapter.SearchResultsAdapter
import `is`.hbv601g.movieapp.model.MovieItem
import `is`.hbv601g.movieapp.model.TvShowItem
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity that handles searching for movies and TV shows.
 * Implements live search so the search is triggered after a short pause as the user types.
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var adapter: SearchResultsAdapter
    private lateinit var radioGroup: RadioGroup

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.searchResultsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SearchResultsAdapter().apply {
            onItemClick = { selectedItem ->
                when (selectedItem) {
                    is MovieItem -> {
                        //start MovieDetailsActivity with the movie ID.
                        val intent = Intent(this@SearchActivity, MovieDetailsActivity::class.java)
                        intent.putExtra("MOVIE_ID", selectedItem.id)
                        startActivity(intent)
                    }
                    is TvShowItem -> {
                        // Do nothing for TV shows, no TV show detail activity yet.
                    }
                }
            }
        }
        recyclerView.adapter = adapter

        radioGroup = findViewById(R.id.radioGroupSearchType)

        // Listen for radio selection changes to update the search results.
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Get the current query from the search view.
            val query = searchView.query.toString()
            if (query.isNotBlank()) {
                performSearch(query, checkedId)
            }
        }

        // Listen for search query input changes.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it, radioGroup.checkedRadioButtonId) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500) // Debounce delay.
                    newText?.let {
                        if (it.isNotBlank()) {
                            performSearch(it, radioGroup.checkedRadioButtonId)
                        } else {
                            adapter.submitList(emptyList())
                        }
                    }
                }
                return true
            }
        })
    }

    /**
     * Determines which search method to use based on the selected toggle.
     *
     * @param query The search query string.
     * @param checkedId The resource ID of the selected radio button.
     */
    private fun performSearch(query: String, checkedId: Int) {
        when (checkedId) {
            R.id.radioAll -> searchAll(query)
            R.id.radioMovies -> searchMovies(query)
            R.id.radioTvShows -> searchTvShows(query)
            else -> searchAll(query)
        }
    }

    /**
     * Searches both movies and TV shows and combines the results.
     */
    private fun searchAll(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val moviesDeferred = async { RetrofitInstance.movieApiService.searchMovies(query) }
                val tvShowsDeferred = async { RetrofitInstance.tvShowApiService.searchTvShows(query) }

                val moviesResponse = moviesDeferred.await()
                val tvShowsResponse = tvShowsDeferred.await()

                if (moviesResponse.isSuccessful && tvShowsResponse.isSuccessful) {
                    val movies = moviesResponse.body() ?: emptyList()
                    val tvShows = tvShowsResponse.body() ?: emptyList()

                    // Combines results into a single list.
                    val combinedResults = mutableListOf<Any>().apply {
                        addAll(movies)
                        addAll(tvShows)
                    }

                    withContext(Dispatchers.Main) {
                        adapter.submitList(combinedResults)
                    }
                } else {
                    Log.e("SearchActivity", "Search failed: movies code ${moviesResponse.code()}, tv shows code ${tvShowsResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchActivity", "Exception in searchAll: ${e.message}")
            }
        }
    }

    /**
     * Searches for movies only.
     */
    private fun searchMovies(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.movieApiService.searchMovies(query)
                if (response.isSuccessful) {
                    val movies = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.submitList(movies)
                    }
                } else {
                    Log.e("SearchActivity", "Movies search failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchActivity", "Exception in searchMovies: ${e.message}")
            }
        }
    }

    /**
     * Searches for TV shows only.
     */
    private fun searchTvShows(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.tvShowApiService.searchTvShows(query)
                if (response.isSuccessful) {
                    val tvShows = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.submitList(tvShows)
                    }
                } else {
                    Log.e("SearchActivity", "TV shows search failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchActivity", "Exception in searchTvShows: ${e.message}")
            }
        }
    }
}