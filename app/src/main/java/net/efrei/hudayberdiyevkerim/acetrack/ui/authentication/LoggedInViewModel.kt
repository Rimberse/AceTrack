package net.efrei.hudayberdiyevkerim.acetrack.ui.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import net.efrei.hudayberdiyevkerim.acetrack.firebase.FirebaseAuthenticationManager

class LoggedInViewModel(app: Application) : AndroidViewModel(app) {
    var firebaseAuthenticationManager : FirebaseAuthenticationManager = FirebaseAuthenticationManager(app)
    var liveFirebaseUser : MutableLiveData<FirebaseUser> = firebaseAuthenticationManager.liveFirebaseUser
    var loggedOut : MutableLiveData<Boolean> = firebaseAuthenticationManager.loggedOut

    fun logOut() {
        firebaseAuthenticationManager.logOut()
    }
}
