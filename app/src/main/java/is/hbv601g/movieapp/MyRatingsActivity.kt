package `is`.hbv601g.movieapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.adapter.MovieAdapter
import `is`.hbv601g.movieapp.adapter.ReviewAdapter

import `is`.hbv601g.movieapp.network.RetrofitInstance

import `is`.hbv601g.movieapp.DBHelper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyRatingsActivity : AppCompatActivity(){

    private lateinit var adapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ratings)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReviews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReviewAdapter()
        recyclerView.adapter = adapter

        fetchReviews(applicationContext)
    }


    /**
     * fetches reviews for the currently logged in user.
     */
    private fun fetchReviews(context: Context) {
        val token = DBHelper(context).getLatestToken()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = fetchUserData(token)
                sendReviewrequest(user)
            } catch (e: Exception) {
                Log.e("SearchActivity", "Exception in findByUserId: ${e.message}")
            }
        }
    }

    /**
     * Fetches User data from token of the currently logged in user
     */
    private suspend fun fetchUserData(token: String?): UserItem?{
        token?.let {
            val response = RetrofitInstance.userApiService.getMe(token)
            return response.body()
        }
        return null
    }

    /**
     * Helper function for more readable code
     */
    private suspend fun sendReviewrequest(user: UserItem?){
        user?.let {
            val reviewResponse = RetrofitInstance.reviewApiService.findByUserId(user.id)
            if (reviewResponse.isSuccessful) {
                val reviews = reviewResponse.body() ?: emptyList()
                withContext(Dispatchers.Main) {
                    adapter.setReviews(reviews)
                }
            } else {
                Log.e("SearchActivity", "Reviews search failed with code: ${reviewResponse.code()}")
            }
        }
    }
}