package net.efrei.hudayberdiyevkerim.acetrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.efrei.hudayberdiyevkerim.acetrack.databinding.CardPlayerBinding
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerModel

interface PlayersListener {
    fun onPlayerEditSwiped(playerPosition: Int)
    fun onPlayerDeleteSwiped(playerPosition: Int)
}

class PlayerAdapter constructor(private var players: List<PlayerModel>) :
    RecyclerView.Adapter<PlayerAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val player = players[holder.adapterPosition]
        holder.bind(player)
    }

    override fun getItemCount(): Int = players.size

    class MainHolder(private val binding : CardPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: PlayerModel) {
            Picasso.get().load(player.image).resize(200,200).into(binding.imageIcon)

            binding.player = player
            binding.executePendingBindings()
        }
    }
}
