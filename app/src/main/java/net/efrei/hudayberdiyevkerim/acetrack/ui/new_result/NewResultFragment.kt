package net.efrei.hudayberdiyevkerim.acetrack.ui.new_result

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.databinding.FragmentNewResultBinding
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.models.ResultModel
import java.text.SimpleDateFormat
import java.util.*

class NewResultFragment : Fragment() {
    lateinit var app: MainApp
    private var _fragmentBinding: FragmentNewResultBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!
    private var result = ResultModel()
    private var isEditingExistingResult = false
    private var cal: Calendar = Calendar.getInstance()
    private var resultDate: TextView? = null
    private var buttonAddDate: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentBinding = FragmentNewResultBinding.inflate(inflater, container, false)
        val root = fragmentBinding.root

        if (arguments?.get("result") !== null) {
            isEditingExistingResult = true
        }

        if (isEditingExistingResult) {
            activity?.title = getString(R.string.menu_editResult)
        } else {
            activity?.title = getString(R.string.new_result)
        }

        val allPlayers = app.players.findAll()
        var playerNames: MutableList<String> = allPlayers.map{it.firstName + " " + it.lastName} as MutableList<String>
        playerNames.add(0, getString(R.string.select_a_player))

        val playerOneSpinner = fragmentBinding.playerOneSpinner
        val playerTwoSpinner = fragmentBinding.playerTwoSpinner

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, playerNames)
        playerOneSpinner.adapter = adapter
        playerTwoSpinner.adapter = adapter

        resultDate = fragmentBinding.resultDate
        buttonAddDate = fragmentBinding.addDateButton
        resultDate!!.text = getString(R.string.date_placeholder)

        // Date picker implemented with reference to https://www.tutorialkart.com/kotlin-android/android-datepicker-kotlin-example/
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        buttonAddDate!!.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        if (isEditingExistingResult) {
            val resultId: String = arguments?.get("result") as String
            result = app.results.findById(resultId.toLong())!!

            fragmentBinding.playerOneSpinner.setSelection(playerNames.indexOf(result.playerOne))
            fragmentBinding.playerTwoSpinner.setSelection(playerNames.indexOf(result.playerTwo))
            fragmentBinding.playerOneScore.setText(result.playerOneScore.toString())
            fragmentBinding.playerTwoScore.setText(result.playerTwoScore.toString())
            resultDate!!.text = result.date
            fragmentBinding.addResultButton.setText(R.string.update_result)
        }

        playerOneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                result.playerOne = playerNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        playerTwoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                result.playerTwo = playerNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        setButtonListener(fragmentBinding)
        return root
    }

    private fun updateDateInView() {
        val format = "dd/MM/yyyy"
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        resultDate!!.text = simpleDateFormat.format(cal.time)
    }

    private fun setButtonListener(layout: FragmentNewResultBinding) {
        layout.addResultButton.setOnClickListener() {
            val playerOneScore = layout.playerOneScore.text.toString()
            val playerTwoScore = layout.playerTwoScore.text.toString()
            val resultDate = layout.resultDate.text
            val selectPlayerString = requireActivity().getString(R.string.select_a_player)

            if (result.playerOne == selectPlayerString || result.playerTwo == selectPlayerString || playerOneScore.isEmpty() || playerTwoScore.isEmpty() || resultDate.isEmpty()) {
                Snackbar
                    .make(it, R.string.fill_in_all_fields, Snackbar.LENGTH_LONG)
                    .show()
            } else if (result.playerOne == result.playerTwo) {
                Snackbar
                    .make(it, R.string.select_another_player, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                result.playerOneScore = playerOneScore.toInt()
                result.playerTwoScore = playerTwoScore.toInt()
                result.date = resultDate.toString()

                if (isEditingExistingResult) {
                    app.results.update(result.copy())
                } else {
                    app.results.create(result.copy())
                }

                findNavController().popBackStack()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NewResultFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}
