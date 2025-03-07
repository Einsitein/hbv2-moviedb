package `is`.hbv601g.movieapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601g.movieapp.adapter.TvShowAdapter
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvShowActivity : AppCompatActivity() {

    private lateinit var adapter: TvShowAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show)

        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewTvShows)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TvShowAdapter()
        recyclerView.adapter = adapter

        fetchTvShows()
    }

    private fun fetchTvShows() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.tvShowApiService.getTvShows()
                if (response.isSuccessful) {
                    val tvShows = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.setTvShows(tvShows)
                    }
                } else {
                    Log.e("TvShowActivity", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TvShowActivity", "Exception: ${e.message}")
            }
        }
    }
}
