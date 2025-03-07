package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.TvShowItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TvShowApiService {
    @GET("tvshows")
    suspend fun getTvShows(): Response<List<TvShowItem>>

    @GET("tvshows/search/{searchString}")
    suspend fun searchTvShows(@Path("searchString") searchString: String): Response<List<TvShowItem>>
}