package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.ReviewItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReviewApiService {
    @GET("/review/findByUserId/{userId}")
    suspend fun findByUserId(@Path("userId") userId: String): Response<List<ReviewItem>>
}