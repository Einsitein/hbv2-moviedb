package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.ReviewItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @GET("user/users/{user_id}/movies/ratings")
    suspend fun findByUserId(@Path("user_id") userId: Long): Response<List<ReviewItem>>

    @GET("/review/findAverageRatingByMovieId/{movieId}")
    suspend fun getMovieRatingById(@Path("movieId") movieId: Int): Response<Double>

    @POST("review/createReview")
    fun submitReview(@Body review: ReviewItem): Response<ReviewItem>
}