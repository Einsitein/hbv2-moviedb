package `is`.hbv601g.movieapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object to create and manage the Retrofit instance.
 */
object RetrofitInstance {

    // Base URL for the API.
    private const val BASE_URL = "https://hugbunadarverkefni1-moviedb.onrender.com/"

    // Lazily initialize the Retrofit instance.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides the service for movie API calls.
     */
    val movieApiService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    /**
     * Provides the service for TV show API calls.
     */
    val tvShowApiService: TvShowApiService by lazy {
        retrofit.create(TvShowApiService::class.java)
    }

    /**
     * Provides the service for review API calls.
     */
    val reviewApiService: ReviewApiService by lazy {
        retrofit.create(ReviewApiService::class.java)
    }

    /**
     * Provides the service for review API calls.
     */
    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}
