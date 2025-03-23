package `is`.hbv601g.movieapp.network

import `is`.hbv601g.movieapp.model.EpisodeItem
import `is`.hbv601g.movieapp.model.SeasonItem
import `is`.hbv601g.movieapp.model.TvShowItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TvShowApiService {
    @GET("tvshows")
    suspend fun getTvShows(): Response<List<TvShowItem>>

    @GET("tvshows/search/{searchString}")
    suspend fun searchTvShows(@Path("searchString") searchString: String): Response<List<TvShowItem>>

    @GET("tvshows/{id}")
    suspend fun getTvShowById(@Path("id") id: Int): Response<TvShowItem>

    @GET("tvshows/{id}/seasons")
    suspend fun getTvShowSeasons(@Path("id") id: Int): Response<List<SeasonItem>>

    @GET("tvshows/{tvShowId}/season/{seasonId}/episodes")
    suspend fun getEpisodes(
        @Path("tvShowId") tvShowId: Int,
        @Path("seasonId") seasonId: Int
    ): Response<List<EpisodeItem>>

    @GET("tvshows/{tvShowId}/season/{seasonId}/episode/{episodeId}")
    suspend fun getEpisodeById(
        @Path("tvShowId") tvShowId: Int,
        @Path("seasonId") seasonId: Int,
        @Path("episodeId") episodeId: Int
    ): Response<EpisodeItem>
}