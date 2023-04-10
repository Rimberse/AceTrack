package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
import java.util.*

fun generateRandomUserId(): Long {
    return Random().nextLong()
}

class UserJSONStore(private val context: Context) : UserStore {
    private var users = mutableListOf<UserModel>()

    override fun findAll(): MutableList<UserModel> {
        return users
    }

    override fun create(user: UserModel) {
        user.id = generateRandomUserId()
        users.add(user)
    }


    override fun update(user: UserModel) {
        val currentUser: UserModel? = users.find { it.id == user.id }

        if (currentUser != null) {
            users[users.indexOf(currentUser)] = user
        }
    }

    override fun delete(user: UserModel) {
        users.remove(user)
    }
}
