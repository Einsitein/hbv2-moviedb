package `is`.hbv601g.movieapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import `is`.hbv601g.movieapp.model.ReviewItem
import `is`.hbv601g.movieapp.model.UserItem
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException


class MovieDetailsActivity : AppCompatActivity() {

    private suspend fun getUserId(context: Context): Long{
        val token = DBHelper(context).getLatestToken()
        token?.let {
            val response = RetrofitInstance.userApiService.getMe(token)
            val user = response.body()
            user?.let {
                return user.id
            }
        }
        return -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val moviePoster: ImageView = findViewById(R.id.moviePoster)
        val movieName: TextView = findViewById(R.id.movieName)
        val movieYear: TextView = findViewById(R.id.movieYear)
        val movieRating: TextView = findViewById(R.id.movieRating)
        val userRatingInput: EditText = findViewById(R.id.userRatingInput)
        val submitRatingButton: Button = findViewById(R.id.submitRatingButton)

        // Retrieve the passed movie ID
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        //val userId = intent.getIntExtra("USER_ID", -1)

        var userId : Long = -1
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userId = getUserId(applicationContext)

            } catch (e: Exception) {
                Log.e("SearchActivity", "Exception in findByUserId: ${e.message}")
            }
        }



        if (movieId != -1) {
            lifecycleScope.launch {
                try {
                    val movie = RetrofitInstance.movieApiService.getMovieById(movieId)
                    val averageRating = RetrofitInstance.reviewApiService.getMovieRatingById(movieId)


                    if (movie.isSuccessful && averageRating.isSuccessful) {
                        movieName.text = movie.body()?.name ?: "Unknown"
                        movieYear.text = movie.body()?.year?.toString() ?: "Unknown"
                        movieRating.text = "Rating: " + averageRating.body().toString()

                        // Load movie poster from URL using Glide
                        Glide.with(this@MovieDetailsActivity)
                            .load(movie.body()?.images)
                            .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                            .into(moviePoster)
                    }
                } catch (e: CancellationException) {
                    Log.e("MovieApp", "⚠️ Coroutine was canceled: ${e.message}")
                    throw e  // Re-throw if needed

                } catch (e: Exception) {
                    Log.e("MovieApp", "❌ Error fetching movie details: ${e.message}")
                }

            }

            submitRatingButton.setOnClickListener {
                val userRating = userRatingInput.text.toString().toDoubleOrNull()

                if (userRating == null) {
                    Toast.makeText(this, "Please enter a valid rating!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val review = ReviewItem(
                    id = "${userId}-$movieId",
                    userId = userId.toLong(),
                    movieId = movieId.toLong(),
                    rating = userRating,
                    movieReview = ""
                )

                Log.d("DEBUGG!!", review.toString())
                lifecycleScope.launch {
                    Log.d("MovieApp", userRating.toString())
                    try {
                        val response = withContext(Dispatchers.IO) {
                            RetrofitInstance.reviewApiService.submitReview(review)
                        }

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@MovieDetailsActivity, "Your rating: $userRating", Toast.LENGTH_SHORT).show()
                                val updatedRatingResponse = withContext(Dispatchers.IO) {
                                    RetrofitInstance.reviewApiService.getMovieRatingById(movieId)
                                }

                                // Update the UI with the new rating
                                withContext(Dispatchers.Main) {
                                    if (updatedRatingResponse.isSuccessful) {
                                        val newAverageRating = updatedRatingResponse.body()
                                        movieRating.text = "Rating: ${newAverageRating?.toString() ?: "N/A"}"
                                    } else {
                                        Toast.makeText(
                                            this@MovieDetailsActivity,
                                            "Failed to fetch updated rating!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this@MovieDetailsActivity, "Failed to submit rating!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MovieDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


        }
    }
}
