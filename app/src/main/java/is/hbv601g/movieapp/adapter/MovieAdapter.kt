package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.MovieItem

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var movies: List<MovieItem> = emptyList()

    fun setMovies(movies: List<MovieItem>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieName: TextView = itemView.findViewById(R.id.movieName)
        private val movieYear: TextView = itemView.findViewById(R.id.movieYear)

        fun bind(movie: MovieItem) {
            movieName.text = movie.name
            movieYear.text = movie.year.toString()
        }
    }
}
