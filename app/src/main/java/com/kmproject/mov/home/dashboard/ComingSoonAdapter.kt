package com.kmproject.mov.home.dashboard


import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kmproject.mov.R
import com.kmproject.mov.home.model.Film

class ComingSoonAdapter(private var data:List<Film>,
                        private val listener: (Film) -> Unit)
    : RecyclerView.Adapter<ComingSoonAdapter.LeagueViewHolder>() {

    lateinit var ContextAdapter: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflaterView: View = layoutInflater.inflate(R.layout.row_item_coming_soon, parent, false)
        return LeagueViewHolder(inflaterView)
    }

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)
    }

    override fun getItemCount(): Int = data.size

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView = view.findViewById(R.id.tv_kursi)
        private val tvGenre: TextView = view.findViewById(R.id.tv_genre)
        private val tvRate: TextView = view.findViewById(R.id.tv_rate)

        private val tvImage: ImageView = view.findViewById(R.id.iv_poster_image)

        fun bindItem(data: Film, listener: (Film) -> Unit, context: Context, position: Int){

        tvTitle.text = data.judul
        tvGenre.text = data.genre
        tvRate.text = data.rating

        Glide.with(context)
        .load(data.poster)
        .into(tvImage);

             itemView.setOnClickListener {
                listener(data)
            }
        }
    }
}