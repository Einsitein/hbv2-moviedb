package `is`.hbv601g.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.MovieItem
import `is`.hbv601g.movieapp.model.TvShowItem

/**
 * Adapter to show search results (movies and TV shows).
 */
class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder>() {

    private var items: List<Any> = emptyList()

    // Click listener for when an item is clicked.
    var onItemClick: ((Any) -> Unit)? = null

    /**
     * Updates the list of items and refreshes the view.
     */
    fun submitList(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * Creates a new ViewHolder using a simple list item layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return SearchResultsViewHolder(view)
    }

    /**
     * Binds a MovieItem or TvShowItem to the ViewHolder.
     */
    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is MovieItem -> {
                holder.title.text = item.name
                holder.subtitle.text = holder.itemView.context.getString(R.string.movie_subtitle, item.year)
            }
            is TvShowItem -> {
                holder.title.text = item.name
                holder.subtitle.text = holder.itemView.context.getString(
                    R.string.tv_show_subtitle,
                    item.genre ?: "Unknown"
                )
            }
            else -> {
                holder.title.text = holder.itemView.context.getString(R.string.unknown)
                holder.subtitle.text = ""
            }
        }
        // Set the click listener for this item.
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size

    /**
     * ViewHolder for search result items.
     */
    class SearchResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)
        val subtitle: TextView = itemView.findViewById(android.R.id.text2)
    }
}
