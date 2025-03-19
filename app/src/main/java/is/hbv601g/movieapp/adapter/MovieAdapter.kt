package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.MovieItem

/**
 * Adapter for showing movies in a RecyclerView.
 */
class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // List of movies to show.
    private var movies: List<MovieItem> = emptyList()

    var onItemClick: ((MovieItem) -> Unit)? = null

    /**
     * Update the movies list.
     */
    fun setMovies(movies: List<MovieItem>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    /**
     * Create a new ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    /**
     * Bind movie data to the ViewHolder.
     */
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
        // When the item is clicked, trigger the onItemClick callback.
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(movie)
        }
    }

    /**
     * Return the number of movies.
     */
    override fun getItemCount() = movies.size

    /**
     * ViewHolder for a movie item.
     */
    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieName: TextView = itemView.findViewById(R.id.movieName)
        private val movieYear: TextView = itemView.findViewById(R.id.movieYear)

        /**
         * Bind the movie info to the views.
         */
        fun bind(movie: MovieItem) {
            movieName.text = movie.name
            movieYear.text = movie.year.toString()
        }
    }
}
