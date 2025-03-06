package `is`.hbv601g.movieapp

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

class MovieActivity : AppCompatActivity() {

    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewMovies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MovieAdapter()
        recyclerView.adapter = adapter

        fetchMovies()
    }

    private fun fetchMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.apiService.getMovies()
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
