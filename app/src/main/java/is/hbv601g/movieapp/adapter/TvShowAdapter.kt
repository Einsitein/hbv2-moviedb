package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.TvShowItem

class TvShowAdapter : RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {

    private var tvShows: List<TvShowItem> = emptyList()

    fun setTvShows(shows: List<TvShowItem>) {
        this.tvShows = shows
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tv_show, parent, false)
        return TvShowViewHolder(view)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        holder.bind(tvShows[position])
    }

    override fun getItemCount() = tvShows.size

    class TvShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvShowName: TextView = itemView.findViewById(R.id.tvShowName)
        private val tvShowGenre: TextView = itemView.findViewById(R.id.tvShowGenre)

        fun bind(show: TvShowItem) {
            tvShowName.text = show.name
            tvShowGenre.text = show.genre ?: "Unknown genre"
        }
    }
}
