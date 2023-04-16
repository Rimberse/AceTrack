package net.efrei.hudayberdiyevkerim.acetrack.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.adapters.ResultsListener
import net.efrei.hudayberdiyevkerim.acetrack.databinding.FragmentResultsBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp

class ResultsFragment : Fragment(), ResultsListener {
    lateinit var app: MainApp
    private var _fragmentBinding: FragmentResultsBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentBinding = FragmentResultsBinding.inflate(inflater, container, false)
        val root = fragmentBinding.root
        activity?.title = getString(R.string.menu_results)

        val fab: FloatingActionButton = fragmentBinding.fab

        fab.setOnClickListener {
            val action = ResultsFragmentDirections.actionResultsFragmentToNewResultFragment()
            findNavController().navigate(action)
        }



        return root
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
