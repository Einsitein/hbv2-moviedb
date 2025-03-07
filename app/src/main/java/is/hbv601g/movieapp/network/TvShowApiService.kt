package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.TvShowItem
import retrofit2.Response
import retrofit2.http.GET

interface TvShowApiService {
    @GET("tvshows")
    suspend fun getTvShows(): Response<List<TvShowItem>>
}