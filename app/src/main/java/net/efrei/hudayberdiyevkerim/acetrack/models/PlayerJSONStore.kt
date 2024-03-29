package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.helpers.exists
import net.efrei.hudayberdiyevkerim.acetrack.helpers.read
import net.efrei.hudayberdiyevkerim.acetrack.helpers.write
import net.efrei.hudayberdiyevkerim.acetrack.persistence.DBHelper
import timber.log.Timber
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


const val PLAYERS_JSON_FILE = "players.json"

val playersGsonBuilder: Gson = GsonBuilder()
    .registerTypeAdapter(object : TypeToken<Uri?>() {}.type, PlayerUriParser())
    .registerTypeAdapter(object : TypeToken<LocalDate?>() {}.type, LocalDateParser())
    .setPrettyPrinting().create()
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

class LocalDateParser : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        return LocalDate.parse(json?.asString, formatter.withLocale(Locale.ENGLISH))
    }

    override fun serialize(
        localDate: LocalDate?,
        srcType: Type?,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(formatter.format(localDate))
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}

fun generateRandomPlayerId(): Long {
    return Random().nextLong()
}

class PlayerJSONStore(private val context: Context) : PlayerStore {
    private var players = mutableListOf<PlayerModel>()
    private lateinit var database: DatabaseReference
    private  val dbHelper = DBHelper(context)

    init {
        database = Firebase.database(context.getString(R.string.firebase_database_url)).reference
        deserialize()
    }

    override fun findAll(): MutableList<PlayerModel> {
        logAll()
        return players

        Log.i("SQLite persisted Players table", dbHelper.getAllPlayers().joinToString("\n" ))
    }

    override fun create(player: PlayerModel) {
        player.id = generateRandomPlayerId()
        players.add(player)
        database.child(context.getString(R.string.firebase_database_players_reference)).child(player.id.toString()).setValue(player)
        serialize()

        dbHelper.insertPlayer(player)
    }

    override fun update(player: PlayerModel) {
        val currentPlayer: PlayerModel? = players.find { it.id == player.id }

        if (currentPlayer != null) {
            players[players.indexOf(currentPlayer)] = player
            database.child(context.getString(R.string.firebase_database_players_reference)).child(player.id.toString()).setValue(player)
        }

        serialize()

        dbHelper.insertPlayer(player)
    }

    override fun delete(player: PlayerModel) {
        players.remove(player)
        database.child(context.getString(R.string.firebase_database_players_reference)).child(player.id.toString()).removeValue()
        serialize()
    }
    private fun serialize() {
        val jsonString = playersGsonBuilder.toJson(players, playersListType)
        write(context, PLAYERS_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        // In case if Firebase database went down
//        val jsonString = read(context, PLAYERS_JSON_FILE)
//        players = playersGsonBuilder.fromJson(jsonString, playersListType)

        database.child(context.getString(R.string.firebase_database_players_reference)).get().addOnSuccessListener {
            val playersMap = HashMap<String, PlayerModel>()

            for (playerSnapshot in it.children) {
                val player: PlayerModel? = playerSnapshot.getValue(PlayerModel::class.java)
                playersMap[playerSnapshot.key!!] = player!!
            }

            Log.i("Firebase", "Got value $playersMap")
            players = ArrayList<PlayerModel>(playersMap.values)

        }.addOnFailureListener{
            Log.e("Firebase", "Error getting data", it)
        }
    }

    private fun logAll() {
        players.forEach { Timber.i("$it") }
    }
}
