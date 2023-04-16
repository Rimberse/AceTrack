package net.efrei.hudayberdiyevkerim.acetrack.ui.players

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import net.efrei.hudayberdiyevkerim.acetrack.adapters.PlayersListener
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class PlayersFragment : Fragment(), PlayersListener {
    lateinit var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()
    }

    override fun onPlayerEditSwiped(playerPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun onPlayerDeleteSwiped(playerPosition: Int) {
        TODO("Not yet implemented")
    }
}
