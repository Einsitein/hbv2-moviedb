package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.EpisodeItem

/**
 * Adapter for displaying episodes.
 */
class EpisodeAdapter : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    private var episodes: List<EpisodeItem> = emptyList()

    // Click listener for an episode item.
    var onItemClick: ((EpisodeItem) -> Unit)? = null

    /**
     * Updates the list of episodes.
     */
    fun setEpisodes(episodes: List<EpisodeItem>) {
        this.episodes = episodes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.bind(episode)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(episode)
        }
    }

    override fun getItemCount(): Int = episodes.size

    /**
     * ViewHolder for an episode item.
     */
    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val episodeNumberTextView: TextView = itemView.findViewById(R.id.episodeNumberTextView)
        private val episodeNameTextView: TextView = itemView.findViewById(R.id.episodeNameTextView)
        private val episodeDescriptionTextView: TextView = itemView.findViewById(R.id.episodeDescriptionTextView)

        /**
         * Binds episode data to the views.
         */
        fun bind(episode: EpisodeItem) {
            episodeNumberTextView.text = "Episode ${episode.episodeNumber}"
            episodeNameTextView.text = episode.episodeName
            episodeDescriptionTextView.text = episode.description
        }
    }
}
