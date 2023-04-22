package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import net.efrei.hudayberdiyevkerim.acetrack.helpers.exists
import net.efrei.hudayberdiyevkerim.acetrack.helpers.read
import net.efrei.hudayberdiyevkerim.acetrack.helpers.write
import net.efrei.hudayberdiyevkerim.acetrack.persistence.DBHelper
import timber.log.Timber
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val RESULTS_JSON_FILE = "results.json"
val resultsGsonBuilder: Gson = GsonBuilder()
    .registerTypeAdapter(object : TypeToken<Uri?>() {}.type, ResultUriParser())
    .registerTypeAdapter(object : TypeToken<LocalDate?>() {}.type, LocalDateParser())
    .setPrettyPrinting().create()
val resultsListType: Type = object : TypeToken<ArrayList<ResultModel>>() {}.type

class ResultUriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
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

fun generateRandomResultId(): Long {
    return Random().nextLong()
}

class ResultJSONStore(private val context: Context) : ResultStore {
    private var results = mutableListOf<ResultModel>()
    private val dbHelper = DBHelper(context)

    init {
        if (exists(context, RESULTS_JSON_FILE)) {
            deserialize()
        }
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
        serialize()

        dbHelper.insertResult(result)
    }

    override fun update(result: ResultModel) {
        val currentResult: ResultModel? = results.find { it.id == result.id }

        if (currentResult != null) {
            results[results.indexOf(currentResult)] = result
        }

        serialize()

        dbHelper.insertResult(result)
    }

    override fun delete(result: ResultModel) {
        results.remove(result)
        serialize()
    }

    private fun serialize() {
        val jsonString = resultsGsonBuilder.toJson(results, resultsListType)
        write(context, RESULTS_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, RESULTS_JSON_FILE)
        results = resultsGsonBuilder.fromJson(jsonString, resultsListType)
    }

    private fun logAll() {
        results.forEach { Timber.i("$it") }
    }
}
