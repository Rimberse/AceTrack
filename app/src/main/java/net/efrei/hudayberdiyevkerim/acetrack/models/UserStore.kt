package net.efrei.hudayberdiyevkerim.acetrack.models

interface UserStore {
    fun findAll(): List<UserModel>
    fun create(result: UserModel)
    fun update(result: UserModel)
    fun delete(result: UserModel)
}
