package unicen.seminario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import unicen.seminario.model.Game

class GamesAdapter : ListAdapter<Game, GamesAdapter.GameViewHolder>(GameDiffCallback()) {

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.game_name)
        val imageView: ImageView = itemView.findViewById(R.id.game_image)
        val ratingView: TextView = itemView.findViewById(R.id.game_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = getItem(position)

        holder.textView.text = game.name
        holder.ratingView.text = if (game.rating != null) "★ ${game.rating}" else "★ N/A"

        game.background_image?.let { imageUrl ->
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(300, 200)
                .into(holder.imageView)
        }
    }
}

class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
    override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean = oldItem == newItem
}

