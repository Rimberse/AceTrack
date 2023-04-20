package net.efrei.hudayberdiyevkerim.acetrack.persistence

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerModel
import net.efrei.hudayberdiyevkerim.acetrack.models.ResultModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ace_track.db"
        private const val DATABASE_VERSION = 1

        // Table names
        private const val TABLE_NAME_PLAYER = "players"
        private const val TABLE_NAME_RESULT = "results"

        // Player table columns
        private const val COLUMN_PLAYER_ID = "id"
        private const val COLUMN_PLAYER_UUID = "uuid"
        private const val COLUMN_PLAYER_EMAIL = "email"
        private const val COLUMN_PLAYER_PASSWORD = "password"
        private const val COLUMN_PLAYER_FIRST_NAME = "first_name"
        private const val COLUMN_PLAYER_LAST_NAME = "last_name"
        private const val COLUMN_PLAYER_DATE_OF_BIRTH = "date_of_birth"
        private const val COLUMN_PLAYER_EXPERIENCE = "experience"
        private const val COLUMN_PLAYER_IMAGE = "image"

        // Result table columns
        private const val COLUMN_RESULT_ID = "id"
        private const val COLUMN_RESULT_PLAYER_ONE = "player_one"
        private const val COLUMN_RESULT_PLAYER_TWO = "player_two"
        private const val COLUMN_RESULT_PLAYER_ONE_SCORE = "player_one_score"
        private const val COLUMN_RESULT_PLAYER_TWO_SCORE = "player_two_score"
        private const val COLUMN_RESULT_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Player table
        val createPlayerTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME_PLAYER (
            $COLUMN_PLAYER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_PLAYER_UUID TEXT,
            $COLUMN_PLAYER_EMAIL TEXT,
            $COLUMN_PLAYER_PASSWORD TEXT,
            $COLUMN_PLAYER_FIRST_NAME TEXT,
            $COLUMN_PLAYER_LAST_NAME TEXT,
            $COLUMN_PLAYER_DATE_OF_BIRTH TEXT,
            $COLUMN_PLAYER_EXPERIENCE TEXT,
            $COLUMN_PLAYER_IMAGE TEXT)
        """.trimIndent()
        db?.execSQL(createPlayerTableQuery)

        // Create Result table
        val createResultTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME_RESULT (
            $COLUMN_RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_RESULT_PLAYER_ONE TEXT,
            $COLUMN_RESULT_PLAYER_TWO TEXT,
            $COLUMN_RESULT_PLAYER_ONE_SCORE INTEGER,
            $COLUMN_RESULT_PLAYER_TWO_SCORE INTEGER,
            $COLUMN_RESULT_DATE TEXT)
        """.trimIndent()
        db?.execSQL(createResultTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop tables if they exist
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_PLAYER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_RESULT")

        // Recreate tables
        onCreate(db)
    }


    // Insert a player into Player table
    fun insertPlayer(player: PlayerModel): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_PLAYER_UUID, player.uuid)
            put(COLUMN_PLAYER_EMAIL, player.email)
            put(COLUMN_PLAYER_PASSWORD, player.password)
            put(COLUMN_PLAYER_FIRST_NAME, player.firstName)
            put(COLUMN_PLAYER_LAST_NAME, player.lastName)
            put(COLUMN_PLAYER_DATE_OF_BIRTH, DateTimeFormatter.ofPattern("dd/MM/yyyy").format(player.dateOfBirth))     // Convert LocalDate to String for storing in SQLite
            put(COLUMN_PLAYER_EXPERIENCE, player.experience)
            put(COLUMN_PLAYER_IMAGE, player.image.toString())   // Convert Uri to String for storing in SQLite
        }

        val id = db.insert(TABLE_NAME_PLAYER, null, contentValues)
        db.close()
        return id
    }

    // Insert a result into Result table
    fun insertResult(result: ResultModel): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_RESULT_PLAYER_ONE, result.playerOne)
            put(COLUMN_RESULT_PLAYER_TWO, result.playerTwo)
            put(COLUMN_RESULT_PLAYER_ONE_SCORE, result.playerOneScore)
            put(COLUMN_RESULT_PLAYER_TWO_SCORE, result.playerTwoScore)
            put(COLUMN_RESULT_DATE, DateTimeFormatter.ofPattern("dd/MM/yyyy").format(result.date))     // Convert LocalDate to String for storing in SQLite
        }

        val id = db.insert(TABLE_NAME_RESULT, null, contentValues)
        db.close()
        return id
    }

    // Retrieve all players from Player table
    fun getAllPlayers(): List<PlayerModel> {
        val players = mutableListOf<PlayerModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_PLAYER", null)

        if (cursor.moveToFirst()) {
            do {
                val player = PlayerModel(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_UUID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_LAST_NAME)),
                    LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_DATE_OF_BIRTH)), DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.ENGLISH)),   // Convert String to LocalDate for retrieving from SQLite
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_EXPERIENCE)),
                    Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_IMAGE)))
                ) // Convert String to Uri for retrieving from SQLite
                players.add(player)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return players
    }

    // Retrieve all results from Result table
    fun getAllResults(): List<ResultModel> {
        val results = mutableListOf<ResultModel>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_RESULT", null)
        if (cursor.moveToFirst()) {
            do {
                val result = ResultModel(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT_PLAYER_ONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT_PLAYER_TWO)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_PLAYER_ONE_SCORE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_PLAYER_TWO_SCORE)),
                    LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT_DATE)), DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.ENGLISH)) // Convert String to LocalDate for retrieving from SQLite
                )
                results.add(result)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return results
    }
}
