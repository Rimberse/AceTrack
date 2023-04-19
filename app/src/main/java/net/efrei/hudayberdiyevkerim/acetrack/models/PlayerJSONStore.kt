package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import net.efrei.hudayberdiyevkerim.acetrack.helpers.exists
import net.efrei.hudayberdiyevkerim.acetrack.helpers.read
import net.efrei.hudayberdiyevkerim.acetrack.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val PLAYERS_JSON_FILE = "players.json"
val playersGsonBuilder: Gson = GsonBuilder().setPrettyPrinting().registerTypeAdapter(Uri::class.java, PlayerUriParser()).create()
val playersListType: Type = object : TypeToken<ArrayList<PlayerModel>>() {}.type

class PlayerUriParser : JsonDeserializer<Uri>, JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

fun generateRandomPlayerId(): Long {
    return Random().nextLong()
}

class PlayerJSONStore(private val context: Context) : PlayerStore {
    private var players = mutableListOf<PlayerModel>()

    init {
        if (exists(context, PLAYERS_JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<PlayerModel> {
        logAll()
        return players
    }

    override fun create(player: PlayerModel) {
        player.id = generateRandomPlayerId()
        players.add(player)
        serialize()
    }

    override fun update(player: PlayerModel) {
        val currentPlayer: PlayerModel? = players.find { it.id == player.id }

        if (currentPlayer != null) {
            players[players.indexOf(currentPlayer)] = player
        }

        serialize()
    }

    override fun delete(player: PlayerModel) {
        players.remove(player)
        serialize()
    }
    private fun serialize() {
        val jsonString = playersGsonBuilder.toJson(players, playersListType)
        write(context, PLAYERS_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, PLAYERS_JSON_FILE)
        players = playersGsonBuilder.fromJson(jsonString, playersListType)
    }

    private fun logAll() {
        players.forEach { Timber.i("$it") }
    }
}
