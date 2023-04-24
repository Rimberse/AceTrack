package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
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
import java.util.*
import kotlin.collections.*

// Used as a backup service
const val RESULTS_JSON_FILE = "results.json"
val resultsGsonBuilder: Gson = GsonBuilder()
    .setPrettyPrinting().create()
val resultsListType: Type = object : TypeToken<ArrayList<ResultModel>>() {}.type

fun generateRandomResultId(): Long {
    return Random().nextLong()
}

class ResultJSONStore(private val context: Context) : ResultStore {
    private var results = mutableListOf<ResultModel>()
    private lateinit var database: DatabaseReference
    private val dbHelper = DBHelper(context)

    init {
        database = Firebase.database(context.getString(R.string.firebase_database_url)).reference
        deserialize()
    }

    override fun findById(id: Long): ResultModel? {
        var result = results.find {
            it.id == id
        }

        return result
    }

    override fun findAll(): MutableList<ResultModel> {
        logAll()
        return results

        Log.i("SQLite persisted Results table", dbHelper.getAllResults().joinToString("\n" ))
    }

    override fun create(result: ResultModel) {
        result.id = generateRandomResultId()
        results.add(result)
        database.child(context.getString(R.string.firebase_database_results_reference)).child(result.id.toString()).setValue(result)
        serialize()

        dbHelper.insertResult(result)
    }

    override fun update(result: ResultModel) {
        val currentResult: ResultModel? = results.find { it.id == result.id }

        if (currentResult != null) {
            results[results.indexOf(currentResult)] = result
            database.child(context.getString(R.string.firebase_database_results_reference)).child(result.id.toString()).setValue(result)
        }

        serialize()

        dbHelper.insertResult(result)
    }

    override fun delete(result: ResultModel) {
        results.remove(result)
        database.child(context.getString(R.string.firebase_database_results_reference)).child(result.id.toString()).removeValue()
        serialize()
    }

    private fun serialize() {
        val jsonString = resultsGsonBuilder.toJson(results, resultsListType)
        write(context, RESULTS_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        // In case if Firebase database went down
//        val jsonString = read(context, RESULTS_JSON_FILE)
//        results = resultsGsonBuilder.fromJson(jsonString, resultsListType)

        database.child(context.getString(R.string.firebase_database_results_reference)).get().addOnSuccessListener {
            val resultsMap = HashMap<String, ResultModel>()

            for (resultSnapshot in it.children) {
                val result: ResultModel? = resultSnapshot.getValue(ResultModel::class.java)
                resultsMap[resultSnapshot.key!!] = result!!
            }

            Log.i("Firebase", "Got value $resultsMap")
            results = ArrayList<ResultModel>(resultsMap.values)

        }.addOnFailureListener{
            Log.e("Firebase", "Error getting data", it)
        }
    }

    private fun logAll() {
        results.forEach { Timber.i("$it") }
    }
}
