package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import `is`.hbv601g.movieapp.adapter.SeasonAdapter
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity that displays details of a TV show and its seasons.
 * Tapping on a season launches an activity to show episodes.
 */
class TvShowDetailsActivity : AppCompatActivity() {

    private lateinit var tvShowPoster: ImageView
    private lateinit var tvShowName: TextView
    private lateinit var tvShowDescription: TextView
    private lateinit var tvShowGenre: TextView
    private lateinit var tvShowCreators: TextView
    private lateinit var tvShowRating: TextView
    private lateinit var seasonsRecyclerView: RecyclerView
    private lateinit var seasonAdapter: SeasonAdapter

    // TV show ID passed via Intent extras.
    private var tvShowId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show_detail)

        tvShowPoster = findViewById(R.id.tvShowPoster)
        tvShowName = findViewById(R.id.tvShowName)
        tvShowDescription = findViewById(R.id.tvShowDescription)
        tvShowGenre = findViewById(R.id.tvShowGenre)
        tvShowCreators = findViewById(R.id.tvShowCreators)
        tvShowRating = findViewById(R.id.tvShowRating)
        seasonsRecyclerView = findViewById(R.id.recyclerViewSeasons)

        // Retrieve TV show ID from intent extras.
        tvShowId = intent.getIntExtra("TVSHOW_ID", -1)
        if (tvShowId != -1) {
            fetchTvShowDetails(tvShowId)
            fetchSeasons(tvShowId)
        } else {
            Log.e("TvShowDetails", "Invalid TV Show ID")
        }

        // Setup the seasons adapter with a click listener.
        seasonAdapter = SeasonAdapter { season ->
            // Launch TvShowEpisodesActivity when a season is clicked.
            val intent = Intent(this, TvShowEpisodesActivity::class.java)
            intent.putExtra("TVSHOW_ID", tvShowId)
            intent.putExtra("SEASON_ID", season.id)
            startActivity(intent)
        }
        seasonsRecyclerView.layoutManager = LinearLayoutManager(this)
        seasonsRecyclerView.adapter = seasonAdapter
    }

    /**
     * Fetches TV show details from the backend.
     */
    private fun fetchTvShowDetails(tvShowId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.tvShowApiService.getTvShowById(tvShowId)
                if (response.isSuccessful) {
                    val tvShow = response.body()
                    withContext(Dispatchers.Main) {
                        tvShow?.let {
                            tvShowName.text = it.name
                            tvShowDescription.text = it.description
                            tvShowGenre.text = "Genre: ${it.genre}"
                            tvShowCreators.text = "Creators: ${it.creators}"
                            tvShowRating.text = "Rating: ${it.rating}"
                            Glide.with(this@TvShowDetailsActivity)
                                .load(it.images)
                                .into(tvShowPoster)
                        }
                    }
                } else {
                    Log.e("TvShowDetails", "Failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TvShowDetails", "Error: ${e.message}")
            }
        }
    }

    /**
     * Fetches seasons for the TV show and updates the adapter.
     */
    private fun fetchSeasons(tvShowId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.tvShowApiService.getTvShowSeasons(tvShowId)
                if (response.isSuccessful) {
                    val seasons = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        seasonAdapter.setSeasons(seasons)
                    }
                } else {
                    Log.e("TvShowDetails", "Failed to fetch seasons: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TvShowDetails", "Error fetching seasons: ${e.message}")
            }
        }
    }
}
