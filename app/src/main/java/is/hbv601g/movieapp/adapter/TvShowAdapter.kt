package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.TvShowItem

/**
 * Adapter for displaying TV shows in a RecyclerView.
 */
class TvShowAdapter : RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {

    private var tvShows: List<TvShowItem> = emptyList()

    /**
     * Updates the list of TV shows.
     */
    fun setTvShows(shows: List<TvShowItem>) {
        this.tvShows = shows
        notifyDataSetChanged()
    }

    /**
     * Creates a new ViewHolder for a TV show.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tv_show, parent, false)
        return TvShowViewHolder(view)
    }

    /**
     * Binds a TV show to the ViewHolder.
     */
    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        holder.bind(tvShows[position])
    }

    override fun getItemCount() = tvShows.size
    /**
     * ViewHolder for a TV show item.
     */
    class TvShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvShowName: TextView = itemView.findViewById(R.id.tvShowName)
        private val tvShowGenre: TextView = itemView.findViewById(R.id.tvShowGenre)

        /**
         * Binds TV show data to the views.
         */
        fun bind(show: TvShowItem) {
            tvShowName.text = show.name
            tvShowGenre.text = show.genre ?: "Unknown genre"
        }
    }
}
