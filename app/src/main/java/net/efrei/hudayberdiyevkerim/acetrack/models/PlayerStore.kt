package net.efrei.hudayberdiyevkerim.acetrack.models

interface PlayerStore {
    fun findAll(): List<PlayerModel>
    fun create(result: PlayerModel)
    fun update(result: PlayerModel)
    fun delete(result: PlayerModel)
}
