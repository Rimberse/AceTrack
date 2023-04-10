package net.efrei.hudayberdiyevkerim.acetrack.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var id: Long = 0,
    var uuid: String = "",
    var email: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var dateOfBirth: String = "",
    var experience: String = "",
    var image: Uri = Uri.EMPTY
) : Parcelable
