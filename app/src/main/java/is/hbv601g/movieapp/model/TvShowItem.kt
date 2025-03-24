package `is`.hbv601g.movieapp.model

data class TvShowItem(
    val id: Int,
    val name: String,
    val description: String,
    val genre: String,
    val images: String,
    val type: String,
    val creators: String,
    val rating: Double,
    val cast: List<String>
)
