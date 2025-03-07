package `is`.hbv601g.movieapp.model

data class MovieItem(
    val id: Int,
    val name: String,
    val year: Int,
    val cast: List<String>,
    val images: String,
    val type: String,
    val runtime: Int,
    val director: String,
    val rating: Double
)