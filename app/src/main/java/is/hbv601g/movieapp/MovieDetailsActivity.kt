package `is`.hbv601g.movieapp

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
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


class MovieDetailsActivity : AppCompatActivity() {

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
        val userId = intent.getIntExtra("USER_ID", -1)


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
                val userRating = userRatingInput.text.toString()

                if (userRating.isNotEmpty()) {
                    val review = ReviewItem(
                        userId = userId.toLong(),  // Replace with actual user ID
                        movieId = movieId.toLong(), // Pass the correct movie ID from your details
                        rating = userRating.toDouble(),
                        movieReview = "" // Assuming this is required
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val call = async { RetrofitInstance.reviewApiService.submitReview(review) }
                            val response = call.await()

                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@MovieDetailsActivity,
                                    "You rated ${movieName.text} as $userRating",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(this@MovieDetailsActivity, "Failed to submit rating!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@MovieDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@MovieDetailsActivity, "Please enter a rating!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
