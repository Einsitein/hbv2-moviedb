package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.SeasonItem

/**
 * Adapter for displaying seasons in the TV show details screen.
 *
 * @param onSeasonClick Called when a season is clicked.
 */
class SeasonAdapter(private val onSeasonClick: (SeasonItem) -> Unit) :
    RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder>() {

    private var seasons: List<SeasonItem> = emptyList()

    /**
     * Updates the list of seasons.
     */
    fun setSeasons(seasons: List<SeasonItem>) {
        this.seasons = seasons
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_season, parent, false)
        return SeasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        val season = seasons[position]
        holder.bind(season)
        holder.itemView.setOnClickListener {
            onSeasonClick(season)
        }
    }

    override fun getItemCount(): Int = seasons.size

    /**
     * ViewHolder for a season item.
     */
    class SeasonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val seasonTextView: TextView = itemView.findViewById(R.id.seasonTextView)
        fun bind(season: SeasonItem) {
            // Display season number (and optionally its name).
            seasonTextView.text = "Season ${season.seasonNumber}" + (season.name?.let { " - $it" } ?: "")
        }
    }
}