package `is`.hbv601g.movieapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.adapter.FavoriteMoviesAdapter
import `is`.hbv601g.movieapp.database.AppDatabaseHelper

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMoviesFavorited)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dbHelper = AppDatabaseHelper(this)
        val email = dbHelper.getLatestEmail()

        // Check if email is not null before fetching favorites.
        if (email != null) {
            val favoriteMovies = dbHelper.getUserFavorites(email)
            // Create and set the adapter.
            val adapter = FavoriteMoviesAdapter(favoriteMovies)
            recyclerView.adapter = adapter
        }
    }
}
