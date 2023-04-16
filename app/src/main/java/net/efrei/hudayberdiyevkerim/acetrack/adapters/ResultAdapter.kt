package net.efrei.hudayberdiyevkerim.acetrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ResultsListener {
    fun onResultEditSwiped(resultPosition: Int)
    fun onResultDeleteSwiped(resultPosition: Int)
}

