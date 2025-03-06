package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Temporary button for search Activity
        // move to nav bar later..
        val searchButton = findViewById<Button>(R.id.btnSearch)
        searchButton.setOnClickListener {
            // Navigate to SearchActivity
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)

            val moviesButton = findViewById<Button>(R.id.btnMovies)
            moviesButton.setOnClickListener {
                val intent = Intent(this, MovieActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
