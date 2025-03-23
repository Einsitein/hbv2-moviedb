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

    // Callback that gets invoked when a TV show is clicked.
    var onItemClick: ((TvShowItem) -> Unit)? = null

    /**
     * Updates the list of TV shows.
     */
    fun setTvShows(shows: List<TvShowItem>) {
        this.tvShows = shows
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tv_show, parent, false)
        return TvShowViewHolder(view)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val tvShow = tvShows[position]
        holder.bind(tvShow)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(tvShow)
        }
    }

    override fun getItemCount(): Int = tvShows.size

    /**
     * ViewHolder for a TV show item.
     */
    class TvShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvShowName: TextView = itemView.findViewById(R.id.tvShowName)
        // Add additional view references as needed.

        /**
         * Binds a TV show to the view.
         */
        fun bind(tvShow: TvShowItem) {
            tvShowName.text = tvShow.name
        }
    }
}
