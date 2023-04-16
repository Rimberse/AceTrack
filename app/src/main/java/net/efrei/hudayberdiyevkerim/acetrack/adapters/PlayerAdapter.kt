package net.efrei.hudayberdiyevkerim.acetrack.adapters

interface PlayersListener {
    fun onPlayerEditSwiped(playerPosition: Int)
    fun onPlayerDeleteSwiped(playerPosition: Int)
}
