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
}
