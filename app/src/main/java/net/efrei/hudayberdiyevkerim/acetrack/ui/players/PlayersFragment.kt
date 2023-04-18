package net.efrei.hudayberdiyevkerim.acetrack.ui.players

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import net.efrei.hudayberdiyevkerim.acetrack.R
import net.efrei.hudayberdiyevkerim.acetrack.adapters.PlayerAdapter
import net.efrei.hudayberdiyevkerim.acetrack.adapters.PlayersListener
import net.efrei.hudayberdiyevkerim.acetrack.databinding.FragmentPlayersBinding
import net.efrei.hudayberdiyevkerim.acetrack.helpers.SwipeToDeleteCallback
import net.efrei.hudayberdiyevkerim.acetrack.helpers.SwipeToEditCallback
import net.efrei.hudayberdiyevkerim.acetrack.main.MainApp
import net.efrei.hudayberdiyevkerim.acetrack.models.PlayerModel
import net.efrei.hudayberdiyevkerim.acetrack.ui.authentication.RegisterActivity

class PlayersFragment : Fragment(), PlayersListener {
    lateinit var app: MainApp
    private var _fragmentBinding: FragmentPlayersBinding? = null
    private val fragmentBinding get() = _fragmentBinding!!
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var authentication: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
        setHasOptionsMenu(true)
        authentication = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentBinding = FragmentPlayersBinding.inflate(inflater, container, false)
        val root = fragmentBinding.root
        activity?.title = getString(R.string.menu_players)

        val layoutManager = LinearLayoutManager(context)
        fragmentBinding.recyclerView.layoutManager = layoutManager
        fragmentBinding.recyclerView.adapter = PlayerAdapter(app.players.findAll())

        setEditAndDeleteSwipeFunctions()
        loadPlayers()
        registerRefreshCallback()

        return root
    }

    private fun setEditAndDeleteSwipeFunctions() {
        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onPlayerDeleteSwiped(viewHolder.adapterPosition)
            }
        }

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onPlayerEditSwiped(viewHolder.adapterPosition)
            }
        }

        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragmentBinding.recyclerView)
        itemTouchEditHelper.attachToRecyclerView(fragmentBinding.recyclerView)
    }

    override fun onPlayerDeleteSwiped(playerPosition: Int) {
        val players = app.players.findAll()
        val targetPlayer = players[playerPosition]

        if (authentication.currentUser!!.uid != targetPlayer.uuid) {
            Toast.makeText(context,
                getString(R.string.player_delete_denied),
                Toast.LENGTH_LONG).show()
            fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
        } else {
            val builder = context?.let { AlertDialog.Builder(it) }
            builder?.setMessage(getString(R.string.confirm_player_delete))
                ?.setCancelable(false)
                ?.setPositiveButton(getString(R.string.ok_btn)) { _, _ ->
                    val adapter = fragmentBinding.recyclerView.adapter as PlayerAdapter
                    app.players.delete(targetPlayer)
                    adapter.notifyItemRemoved(playerPosition)
                    fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
                }?.setNegativeButton(getString(R.string.cancel_btn)) { dialog, _ ->
                    fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
                    dialog.dismiss()
                }

            builder?.create()?.show()
        }
    }

    override fun onPlayerEditSwiped(playerPosition: Int) {
        val players = app.players.findAll()
        val targetPlayer = players[playerPosition]

        if (authentication.currentUser!!.uid != targetPlayer.uuid) {
            Toast.makeText(context,
                getString(R.string.player_edit_denied),
                Toast.LENGTH_LONG).show()
            fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
        } else {
            val launcherIntent = Intent(context, RegisterActivity::class.java)
            launcherIntent.putExtra("player_edit", targetPlayer)
            refreshIntentLauncher.launch(launcherIntent)
        }
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { loadPlayers() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadPlayers() {
        showPlayers(app.players.findAll())
    }

    private fun showPlayers(players: List<PlayerModel>) {
        fragmentBinding.recyclerView.adapter = PlayerAdapter(players)
        fragmentBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentBinding = null
    }
}
