package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.UserItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Header

interface UserApiService {
    @GET("/user/me")
    suspend fun getMe(@Header("Authorization") accessToken: String): Response<UserItem>
}