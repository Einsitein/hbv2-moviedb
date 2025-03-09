package `is`.hbv601g.movieapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import `is`.hbv601g.movieapp.R
import `is`.hbv601g.movieapp.model.MovieItem
import `is`.hbv601g.movieapp.model.ReviewItem
import `is`.hbv601g.movieapp.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var reviews: List<ReviewItem> = emptyList()

    fun setReviews(reviews: List<ReviewItem>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mediaTitle: TextView = itemView.findViewById(R.id.mediaTitle)
        private val rating: TextView = itemView.findViewById(R.id.rating)
        private val reviewTextView: TextView = itemView.findViewById(R.id.reviewTextbox)

        fun bind(review: ReviewItem) {


            fetchMovie(review.movieId.toInt(), mediaTitle)
            rating.text = review.rating.toString()
            reviewTextView.text = review.movieReview
        }
        private fun fetchMovie(movieId: Int, mediaTitle: TextView){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.movieApiService.getMovieById(movieId)
                    val movie = response.body()
                    if (response.isSuccessful) {
                        movie?.let {
                            mediaTitle.text = movie.name
                        }
                    } else {
                        Log.e("ReviewAdapter", "Error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("ReviewAdapter", "Exception: ${e.message}")
                }
            }
        }
    }
}