package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
import java.util.*

fun generateRandomPlayerId(): Long {
    return Random().nextLong()
}

class PlayerJSONStore(private val context: Context) : PlayerStore {
    private var players = mutableListOf<PlayerModel>()

    override fun findAll(): MutableList<PlayerModel> {
        return players
    }

    override fun create(player: PlayerModel) {
        player.id = generateRandomPlayerId()
        players.add(player)
    }


    override fun update(player: PlayerModel) {
        val currentPlayer: PlayerModel? = players.find { it.id == player.id }

        if (currentPlayer != null) {
            players[players.indexOf(currentPlayer)] = player
        }
    }

    override fun delete(player: PlayerModel) {
        players.remove(player)
    }
}
