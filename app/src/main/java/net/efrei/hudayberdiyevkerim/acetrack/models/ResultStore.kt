package net.efrei.hudayberdiyevkerim.acetrack.models

interface ResultStore {
    fun findAll(): List<ResultModel>
    fun findById(id: Long): ResultModel?
    fun create(result: ResultModel)
    fun update(result: ResultModel)
    fun delete(result: ResultModel)
}
