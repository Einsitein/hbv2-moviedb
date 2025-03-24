package `is`.hbv601g.movieapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity to display details for a specific episode.
 * Expects "TVSHOW_ID", "SEASON_ID", and "EPISODE_ID" extras in the intent.
 */
class EpisodeDetailsActivity : AppCompatActivity() {

    private lateinit var episodeDetailsTextView: TextView

    private var tvShowId: Int = -1
    private var seasonId: Int = -1
    private var episodeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode_detail)

        episodeDetailsTextView = findViewById(R.id.episodeDetailsTextView)

        // Retrieve extras.
        tvShowId = intent.getIntExtra("TVSHOW_ID", -1)
        seasonId = intent.getIntExtra("SEASON_ID", -1)
        episodeId = intent.getIntExtra("EPISODE_ID", -1)

        if (tvShowId != -1 && seasonId != -1 && episodeId != -1) {
            fetchEpisodeDetails(tvShowId, seasonId, episodeId)
        } else {
            Log.e("EpisodeDetails", "Invalid TVSHOW_ID, SEASON_ID, or EPISODE_ID")
        }
    }

    /**
     * Fetches and displays details for the given episode using the endpoint:
     * /tvshows/{tvShowId}/season/{seasonId}/episode/{episodeId}
     */
    private fun fetchEpisodeDetails(tvShowId: Int, seasonId: Int, episodeId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.tvShowApiService.getEpisodeById(tvShowId, seasonId, episodeId)
                if (response.isSuccessful) {
                    val episode = response.body()
                    withContext(Dispatchers.Main) {
                        episode?.let {
                            val details = """
                                Episode ${it.episodeNumber}: ${it.episodeName}
                                
                                ${it.description}
                            """.trimIndent()
                            episodeDetailsTextView.text = details
                        }
                    }
                } else {
                    Log.e("EpisodeDetails", "Failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("EpisodeDetails", "Error fetching episode details: ${e.message}")
            }
        }
    }
}