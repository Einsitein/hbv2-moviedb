package `is`.hbv601g.movieapp.model

data class ReviewItem(
    val id: String,
    val userId: Long,
    val movieId: Long,
    val movieReview: String,
    val rating: Double
)