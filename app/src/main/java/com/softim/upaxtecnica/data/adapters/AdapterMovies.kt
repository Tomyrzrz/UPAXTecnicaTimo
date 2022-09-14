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
    /*Adaptador para el RecyclerView de las Peliculas */

    //Clase para asignar valores a cada view con viewHolder
    class MovieViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val IMAGE_BASE = "https://image.tmdb.org/t/p/w500/"
        fun bindMovie(movie : Movie){ //Asignacion de valores a los componentes de cada view Generado
            itemView.txt_name_movie.text = movie.title
            itemView.txt_overview_movie.text = movie.overview
            itemView.txt_rating_movie.text = "Rating: ${movie.vote_average}"
            Glide.with(itemView).load(IMAGE_BASE + movie.poster).into(itemView.ivMovie)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder { //Se genera una vista del item_movie para cada pelicula
        return MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        )
    }

    override fun getItemCount(): Int = movies.size   //Retorna la cantidad de peliculas que se tienen

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) { //Se le pasa al viewHolder la pelicula que le corresponde.
        holder.bindMovie(movies.get(position))
    }
}