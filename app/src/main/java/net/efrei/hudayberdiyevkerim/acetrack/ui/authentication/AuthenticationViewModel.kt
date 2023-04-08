package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import net.efrei.hudayberdiyevkerim.acetrack.firebase.FirebaseAuthenticationManager

class AuthenticationViewModel (application: Application) : AndroidViewModel(application) {
    var firebaseAuthenticationManager : FirebaseAuthenticationManager = FirebaseAuthenticationManager(application)
    var liveFirebaseUser : MutableLiveData<FirebaseUser> = firebaseAuthenticationManager.liveFirebaseUser

    fun login(email: String?, password: String?) {
        firebaseAuthenticationManager.login(email, password)
    }
}
