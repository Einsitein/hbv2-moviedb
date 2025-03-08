package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupNavigationButtons()
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
}
