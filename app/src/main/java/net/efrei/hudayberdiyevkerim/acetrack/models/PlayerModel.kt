package net.efrei.hudayberdiyevkerim.acetrack.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Parcelize
data class PlayerModel(
    var id: Long = 0,
    var uuid: String = "",
    var email: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var dateOfBirth: LocalDate = LocalDate.now(),
    var experience: String = "",
    var image: Uri = Uri.EMPTY
) : Parcelable
