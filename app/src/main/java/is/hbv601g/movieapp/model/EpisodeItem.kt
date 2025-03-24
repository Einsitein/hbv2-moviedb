package `is`.hbv601g.movieapp.model

/**
 * Represents a TV show episode.
 */
data class EpisodeItem(
    val id: Int,
    val episodeNumber: Int,
    val episodeName: String,
    val description: String,
    val releaseDate: String?
)
