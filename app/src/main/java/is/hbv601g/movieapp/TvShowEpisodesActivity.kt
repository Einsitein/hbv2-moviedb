package `is`.hbv601g.movieapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.adapter.EpisodeAdapter
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity to display episodes for a given season of a TV show.
 * Expects "TVSHOW_ID" and "SEASON_ID" extras in the intent.
 */
class TvShowEpisodesActivity : AppCompatActivity() {

    private lateinit var episodesRecyclerView: RecyclerView
    private lateinit var episodeAdapter: EpisodeAdapter

    private var tvShowId: Int = -1
    private var seasonId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_show_episodes)

        episodesRecyclerView = findViewById(R.id.recyclerViewEpisodes)
        episodesRecyclerView.layoutManager = LinearLayoutManager(this)
        episodeAdapter = EpisodeAdapter().apply {
            onItemClick = { episode ->
                // Launch EpisodeDetailsActivity when an episode is clicked.
                val intent = Intent(this@TvShowEpisodesActivity, EpisodeDetailsActivity::class.java)
                intent.putExtra("TVSHOW_ID", tvShowId)
                intent.putExtra("SEASON_ID", seasonId)
                intent.putExtra("EPISODE_ID", episode.id)
                startActivity(intent)
            }
        }
        episodesRecyclerView.adapter = episodeAdapter

        // Retrieve TV show ID and season ID from intent extras.
        tvShowId = intent.getIntExtra("TVSHOW_ID", -1)
        seasonId = intent.getIntExtra("SEASON_ID", -1)

        if (tvShowId != -1 && seasonId != -1) {
            fetchEpisodes(tvShowId, seasonId)
        } else {
            Log.e("TvShowEpisodes", "Invalid TVSHOW_ID or SEASON_ID")
        }
    }

    /**
     * Fetches episodes for the given TV show and season.
     */
    private fun fetchEpisodes(tvShowId: Int, seasonId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.tvShowApiService.getEpisodes(tvShowId, seasonId)
                if (response.isSuccessful) {
                    val episodes = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        episodeAdapter.setEpisodes(episodes)
                    }
                } else {
                    Log.e("TvShowEpisodes", "Failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("TvShowEpisodes", "Error fetching episodes: ${e.message}")
            }
        }
    }
}