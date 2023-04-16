package net.efrei.hudayberdiyevkerim.acetrack.models

import android.content.Context
import com.google.gson.*
import timber.log.Timber
import java.util.*

fun generateRandomResultId(): Long {
    return Random().nextLong()
}

class ResultJSONStore(private val context: Context) : ResultStore {
    private var results = mutableListOf<ResultModel>()

    override fun findById(id: Long): ResultModel? {
        var result = results.find {
            it.id == id
        }

        return result
    }

    override fun findAll(): MutableList<ResultModel> {
        logAll()
        return results
    }

    override fun create(result: ResultModel) {
        result.id = generateRandomResultId()
        results.add(result)
    }


    override fun update(result: ResultModel) {
        val currentResult: ResultModel? = results.find { it.id == result.id }

        if (currentResult != null) {
            results[results.indexOf(currentResult)] = result
        }
    }

    override fun delete(result: ResultModel) {
        results.remove(result)
    }

    private fun logAll() {
        results.forEach { Timber.i("$it") }
    }
}
