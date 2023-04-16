package net.efrei.hudayberdiyevkerim.acetrack.ui.new_result

import android.os.Bundle
import androidx.fragment.app.Fragment
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class NewResultFragment : Fragment() {
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }
}
