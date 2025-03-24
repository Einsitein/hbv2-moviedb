package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavoriteMoviesAdapter(private val favoriteMovies: List<String>) :
    RecyclerView.Adapter<FavoriteMoviesAdapter.FavoriteMovieViewHolder>() {

    private val favoriteMoviesDistinct: List<String> = favoriteMovies.distinct()

    class FavoriteMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieNameTextView: TextView = itemView.findViewById(R.id.movieNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_movie, parent, false)
        return FavoriteMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        val movieId = favoriteMoviesDistinct[position]
        // Launch a coroutine on the IO dispatcher for network call.
        CoroutineScope(Dispatchers.IO).launch {
            val movieResponse = RetrofitInstance.movieApiService.getMovieById(movieId.toInt())
            val movie = movieResponse.body()
            // Switch back to Main to update the UI.
            withContext(Dispatchers.Main) {
                holder.movieNameTextView.text = movie?.name ?: "Unknown"
            }
        }
    }


    override fun getItemCount(): Int = favoriteMoviesDistinct.size
}
