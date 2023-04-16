package net.efrei.hudayberdiyevkerim.acetrack.ui.contact

import android.os.Bundle
import androidx.fragment.app.Fragment
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class ContactFragment : Fragment() {
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
