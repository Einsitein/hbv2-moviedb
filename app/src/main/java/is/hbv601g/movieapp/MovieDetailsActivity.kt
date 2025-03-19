package `is`.hbv601g.movieapp

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        if (movieId != -1) {
            lifecycleScope.launch {
                try {
                    val movieResponse = RetrofitInstance.movieApiService.getMovieById(movieId)
                    val averageRatingResponse = RetrofitInstance.reviewApiService.getMovieRatingById(movieId)

                    if (movieResponse.isSuccessful && averageRatingResponse.isSuccessful) {
                        val movie = movieResponse.body()
                        movieName.text = movie?.name ?: "Unknown"
                        movieYear.text = movie?.year?.toString() ?: "Unknown"
                        movieRating.text = "Rating: " + String.format("%.2f", averageRatingResponse.body())

                        // Capture the image URL
                        val imageUrl = movie?.images

                        // Load movie poster from URL using Glide
                        Glide.with(this@MovieDetailsActivity)
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                            .into(moviePoster)

                        // Set click listener to download the image when it's tapped
                        moviePoster.setOnClickListener {
                            if (!imageUrl.isNullOrEmpty()) {
                                val request = DownloadManager.Request(Uri.parse(imageUrl)).apply {
                                    setTitle("Downloading Image")
                                    setDescription("Downloading image...")
                                    // Show notification upon download completion
                                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    // Save the file to the Downloads directory with a custom name
                                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "movie_image.jpg")
                                }
                                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                downloadManager.enqueue(request)
                                Toast.makeText(this@MovieDetailsActivity, "Downloading image...", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MovieDetailsActivity, "No image URL available!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    Log.e("MovieApp", "⚠️ Coroutine was canceled: ${e.message}")
                    throw e
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

                lifecycleScope.launch {
                    try {
                        val token = DBHelper(applicationContext).getLatestToken()
                        val response = token?.let { RetrofitInstance.userApiService.getMe(it) }
                        val userId = response?.body()?.id

                        if (userId != null) {
                            val review = ReviewItem(
                                id = "${userId}-$movieId",
                                userId = userId.toLong(),
                                movieId = movieId.toLong(),
                                rating = userRating,
                                movieReview = ""
                            )

                            val reviewResponse = withContext(Dispatchers.IO) {
                                RetrofitInstance.reviewApiService.submitReview(review)
                            }
                            withContext(Dispatchers.Main) {
                                if (reviewResponse.isSuccessful) {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Your rating: $userRating",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val updatedRatingResponse = withContext(Dispatchers.IO) {
                                        RetrofitInstance.reviewApiService.getMovieRatingById(movieId)
                                    }

                                    // Update the UI with the new rating
                                    withContext(Dispatchers.Main) {
                                        if (updatedRatingResponse.isSuccessful) {
                                            val newAverageRating = updatedRatingResponse.body()
                                            movieRating.text = "Rating: " + String.format("%.2f", newAverageRating)
                                        } else {
                                            Toast.makeText(
                                                this@MovieDetailsActivity,
                                                "Failed to fetch updated rating!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this@MovieDetailsActivity,
                                        "Failed to submit rating!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MovieDetailsActivity,
                                "Network error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
}
