package net.efrei.hudayberdiyevkerim.acetrack.ui.results

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.adapters.ResultAdapter
import net.efrei.hudayberdiyevkerim.acetrack.adapters.ResultsListener
import net.efrei.hudayberdiyevkerim.acetrack.databinding.FragmentResultsBinding
import net.efrei.hudayberdiyevkerim.acetrack.helpers.SwipeToDeleteCallback
import net.efrei.hudayberdiyevkerim.acetrack.helpers.SwipeToEditCallback
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.models.ResultModel

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

        val floatingActionButton: FloatingActionButton = fragmentBinding.floatingActionButton

        floatingActionButton.setOnClickListener {
            val action = ResultsFragmentDirections.actionResultsFragmentToNewResultFragment()
            findNavController().navigate(action)
        }

        setEditAndDeleteSwipeFunctions()
        loadResults()
        registerRefreshCallback()

        render(app.results.findAll())

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ResultsFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    private fun setEditAndDeleteSwipeFunctions() {
        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onResultDeleteSwiped(viewHolder.adapterPosition)
            }
        }

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onResultEditSwiped(viewHolder.adapterPosition)
            }
        }

        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragmentBinding.recyclerView)
        itemTouchEditHelper.attachToRecyclerView(fragmentBinding.recyclerView)
    }

    override fun onResultDeleteSwiped(resultPosition: Int) {
        val builder = context?.let { AlertDialog.Builder(it) }

        builder?.setMessage(R.string.swipe_delete_prompt)?.setCancelable(false)
            ?.setPositiveButton(R.string.ok_btn) { _, _ ->
                var targetResult = app.results.findAll().elementAt(resultPosition)
                val adapter = fragmentBinding.recyclerView.adapter as ResultAdapter
                app.results.delete(targetResult)
                adapter.notifyItemRemoved(resultPosition)
                fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
            }?.setNegativeButton(R.string.cancel_btn) { dialog, _ ->
                fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
                dialog.dismiss()
            }

        builder?.create()?.show()
    }

    override fun onResultEditSwiped(resultPosition: Int) {
        var targetResult = app.results.findAll().elementAt(resultPosition)
        val action = ResultsFragmentDirections.actionResultsFragmentToNewResultFragment(targetResult.id.toString())
        findNavController().navigate(action)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { loadResults() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadResults() {
        showResults(app.results.findAll())
    }

    private fun showResults (results: List<ResultModel>) {
        fragmentBinding.recyclerView.adapter = ResultAdapter(results, this)
        fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun render(resultsList: List<ResultModel>) {
        fragmentBinding.recyclerView.layoutManager = LinearLayoutManager(context)
        fragmentBinding.recyclerView.adapter = ResultAdapter(resultsList, this)

        if (resultsList.isEmpty()) {
            fragmentBinding.recyclerView.visibility = View.GONE
            fragmentBinding.resultsNotFound.visibility = View.VISIBLE
        } else {
            fragmentBinding.recyclerView.visibility = View.VISIBLE
            fragmentBinding.resultsNotFound.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}
