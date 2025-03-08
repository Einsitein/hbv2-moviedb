package `is`.hbv601g.movieapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.hbv601g.movieapp.adapter.MovieAdapter
import `is`.hbv601g.movieapp.adapter.ReviewAdapter

import `is`.hbv601g.movieapp.network.RetrofitInstance

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyRatingsActivity : AppCompatActivity(){

    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ratings)
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewReviews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter()
        recyclerView.adapter = adapter

        fetchReviews()
    }


    /**
     * gets reviews for the currently logged in user.
     */
    private fun fetchReviews() {
        val query:Long = 1// ToDo: finna actual userId
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reviewResponse = RetrofitInstance.reviewApiService.findByUserId(query)
                if (reviewResponse.isSuccessful) {
                    val reviews = reviewResponse.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        adapter.setReviews(reviews)
                    }
                } else {
                    Log.e("SearchActivity", "Reviews search failed with code: ${reviewResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e("SearchActivity", "Exception in findByUserId: ${e.message}")
            }
        }
    }
}