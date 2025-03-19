package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601g.movieapp.adapter.MovieAdapter
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity that displays a list of movies.
 */
class MovieActivity : AppCompatActivity() {

    // Adapter to manage movie items in the RecyclerView.
    private lateinit var adapter: MovieAdapter

    /**
     * Called when the activity is created.
     * Sets up the RecyclerView, adapter, and starts fetching movies.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewMovies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MovieAdapter()
        recyclerView.adapter = adapter

        // Set up the click listener for movie items.
        adapter.onItemClick = { movie ->
            // Start the details activity with the movie ID.
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("MOVIE_ID", movie.id)
            startActivity(intent)
        }

        fetchMovies() // Fetch movies from the backend.
    }

    /**
     * Fetches movies from the backend and updates the adapter.
     */
    private fun fetchMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.movieApiService.getMovies()
                if (response.isSuccessful) {
                    val movies = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.setMovies(movies)
                    }
                } else {
                    Log.e("MovieActivity", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MovieActivity", "Exception: ${e.message}")
            }
        }
    }
}
