package net.efrei.hudayberdiyevkerim.acetrack.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : Parcelable
