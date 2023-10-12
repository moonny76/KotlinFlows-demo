package org.scarlet.android.movies.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.scarlet.R
import org.scarlet.android.movies.model.Movie

class MoviesListAdapter(private val interaction: Interaction?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Interaction {
        fun onClickItem(movie: Movie)
    }

    private var diffCallback: DiffUtil.ItemCallback<Movie> =
        object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Movie>) {
        differ.submitList(ArrayList(list))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_movie_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            holder.bind(differ.currentList[position] as Movie)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class MovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var movie_title = itemView.findViewById<TextView>(R.id.movie_title)
        var overview = itemView.findViewById<TextView>(R.id.description)
        var movie_image = itemView.findViewById<ImageView>(R.id.movie_image)
        var movie_release_date = itemView.findViewById<TextView>(R.id.movie_release_date)
        var movie_rating = itemView.findViewById<TextView>(R.id.movie_rating)

        fun bind(item: Movie) {
            if (interaction != null) {
                itemView.setOnClickListener {
                    interaction.onClickItem(item)
                }
            }
            movie_title.text = item.title
            Glide.with(itemView).load("https://image.tmdb.org/t/p/w500" + item.posterPath)
                .into(movie_image)

            overview.text = item.overview
            movie_release_date.text = item.releaseDate
            movie_rating.text = item.voteAverage.toString()
        }
    }
}
