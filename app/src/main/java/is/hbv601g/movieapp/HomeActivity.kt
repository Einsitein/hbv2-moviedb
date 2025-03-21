package `is`.hbv601g.movieapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigationButtons()
        setupThemeToggleButton()

        MainScope().launch{
            withContext(Dispatchers.Main){ setupAverageRating() }
        }
    }

    private fun setupNavigationButtons() {
        // Temporary buttons - move to nav bar or something later.
        val searchButton = findViewById<Button>(R.id.btnSearch)
        val moviesButton = findViewById<Button>(R.id.btnMovies)
        val tvShowsButton = findViewById<Button>(R.id.btnTvShows)
        val reviewButton = findViewById<Button>(R.id.btnReview)

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        moviesButton.setOnClickListener {
            startActivity(Intent(this, MovieActivity::class.java))
        }
        tvShowsButton.setOnClickListener {
            startActivity(Intent(this, TvShowActivity::class.java))
        }
        reviewButton.setOnClickListener {
            startActivity(Intent(this, MyRatingsActivity::class.java))
        }
    }

    private fun setupThemeToggleButton() {
        val toggleThemeButton = findViewById<Button>(R.id.btnToggleTheme)
        toggleThemeButton.setOnClickListener {
            // Check current mode and toggle
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            val newMode = if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
            AppCompatDelegate.setDefaultNightMode(newMode)
        }
    }

    private suspend fun setupAverageRating(){
        val averageRatingTextView = findViewById<TextView>(R.id.textViewAverageRating)
        val rating = getAverageRating(applicationContext)
        val averageRatingText = "My Average Rating Across All Movies: $rating"
        averageRatingTextView.text = averageRatingText
    }

    /*
    * Helper function
    * */
    private suspend fun getAverageRating(context: Context): Double{
        val token = DBHelper(context).getLatestToken()
        var rating = 0.0
        token?.let {
            val fetchedRating = RetrofitInstance.userApiService.getAverageRatingOfMe(token).body()
            fetchedRating?.let{ rating = fetchedRating }
        }
        return rating
    }
}
