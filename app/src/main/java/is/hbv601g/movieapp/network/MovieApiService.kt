package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.MovieItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieApiService {
    @GET("movies")
    suspend fun getMovies(): Response<List<MovieItem>>

    @GET("movies/search/{searchString}")
    suspend fun searchMovies(@Path("searchString") searchString: String): Response<List<MovieItem>>

    @GET("movies/{id}")
    suspend fun getMovieById(@Path("id") id : Int) : Response<MovieItem>
}