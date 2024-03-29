package net.efrei.hudayberdiyevkerim.acetrack.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultModel(
    var id: Long = 0,
    var playerOne: String = "",
    var playerTwo: String = "",
    var playerOneScore: Int = 0,
    var playerTwoScore: Int = 0,
    var date: Long = 0,
    var location : LocationModel? = null
) : Parcelable
