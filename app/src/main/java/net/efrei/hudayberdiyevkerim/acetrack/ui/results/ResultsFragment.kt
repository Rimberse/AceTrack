package net.efrei.hudayberdiyevkerim.acetrack.ui.results

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import net.efrei.hudayberdiyevkerim.acetrack.adapters.ResultsListener
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class ResultsFragment : Fragment(), ResultsListener {
    lateinit var app: MainApp
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ResultsFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onResultEditSwiped(resultPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun onResultDeleteSwiped(resultPosition: Int) {
        TODO("Not yet implemented")
    }
}
