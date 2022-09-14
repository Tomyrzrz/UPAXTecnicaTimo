package com.softim.upaxtecnica.domain.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softim.upaxtecnica.R
import com.softim.upaxtecnica.domain.data.models.Movie
import kotlinx.android.synthetic.main.item_movie.view.*

class AdapterMovies (private val movies : List<Movie>) :
    RecyclerView.Adapter<AdapterMovies.MovieViewHolder>(){

    class MovieViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val IMAGE_BASE = "https://image.tmdb.org/t/p/w500/"
        fun bindMovie(movie : Movie){
            itemView.txt_name_movie.text = movie.title
            itemView.txt_overview_movie.text = movie.overview
            itemView.txt_rating_movie.text = "Rating: ${movie.vote_average}"
            Glide.with(itemView).load(IMAGE_BASE + movie.poster).into(itemView.ivMovie)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        )
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindMovie(movies.get(position))
    }
}