package `is`.hbv601g.movieapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://hugbunadarverkefni1-moviedb.onrender.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val movieApiService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    val tvShowApiService: TvShowApiService by lazy {
        retrofit.create(TvShowApiService::class.java)
    }
    val reviewApiService: ReviewApiService by lazy {
        retrofit.create(ReviewApiService::class.java)
    }
}
