package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.MovieItem
import `is`.hbv601g.movieapp.model.ReviewItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApiService {

    @GET("/review/findAverageRatingByMovieId/{movieId}")
    suspend fun getMovieRatingById(@Path("movieId") movieId: Int): Response<Double>

    @POST("review/createReview")
    fun submitReview(@Body review: ReviewItem): Response<ReviewItem> // Make sure Call<ReviewItem> is used

}