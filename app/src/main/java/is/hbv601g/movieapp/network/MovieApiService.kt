package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.MovieItem
import retrofit2.Response
import retrofit2.http.GET

interface MovieApiService {
    @GET("movies")
    suspend fun getMovies(): Response<List<MovieItem>>
}