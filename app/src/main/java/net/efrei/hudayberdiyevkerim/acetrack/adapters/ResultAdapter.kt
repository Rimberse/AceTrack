package net.efrei.hudayberdiyevkerim.acetrack.adapters

interface ResultsListener {
    fun onResultEditSwiped(resultPosition: Int)
    fun onResultDeleteSwiped(resultPosition: Int)
}
