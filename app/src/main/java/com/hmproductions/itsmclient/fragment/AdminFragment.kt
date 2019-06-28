package com.hmproductions.itsmclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.ConfigurationRecyclerAdapter
import com.hmproductions.itsmclient.adapter.RequestRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.AlterRequest
import com.hmproductions.itsmclient.data.Configuration
import com.hmproductions.itsmclient.data.DeleteConfigurationRequest
import com.hmproductions.itsmclient.data.ITSMViewModel
import com.hmproductions.itsmclient.utils.Constants
import com.hmproductions.itsmclient.utils.Constants.ADMIN_USER
import com.hmproductions.itsmclient.utils.Constants.CONFIG_FRAGMENT_MODE
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_admin.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*
import javax.inject.Inject

class AdminFragment : Fragment(), ConfigurationRecyclerAdapter.OnConfigurationClickListener,
    RequestRecyclerAdapter.OnRequestClickListener {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var configurationAdapter: ConfigurationRecyclerAdapter
    private lateinit var requestsAdapter: RequestRecyclerAdapter
    private lateinit var model: ITSMViewModel

    private var configurationList = mutableListOf<Configuration>()
    private var requestsList = mutableListOf<AlterRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        model = activity?.run { ViewModelProviders.of(this).get(ITSMViewModel::class.java) }
            ?: throw Exception("Invalid activity")

        configurationAdapter = ConfigurationRecyclerAdapter(context, null, this)
        configurationsRecyclerView.layoutManager = LinearLayoutManager(context)
        configurationsRecyclerView.adapter = configurationAdapter
        configurationsRecyclerView.setHasFixedSize(true)

        requestsAdapter = RequestRecyclerAdapter(context, null, this)
        requestsRecyclerView.layoutManager = LinearLayoutManager(context)
        requestsRecyclerView.adapter = requestsAdapter
        requestsRecyclerView.setHasFixedSize(true)

        addConfigurationFab.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(CONFIG_FRAGMENT_MODE, ADMIN_USER)
            findNavController().navigate(R.id.action_new_configuration, bundle)
        }
    }

    private fun getAllConfigurationsAsync() {
        doAsync {
            val configurationResponse = client.getConfigurations(model.token).execute()

            uiThread {
                if (configurationResponse.isSuccessful) {
                    var configurationList = configurationResponse.body()?.result?.configurations ?: mutableListOf()
                    if (configurationList.isNotEmpty()) {
                        configurationList = configurationList.sortedWith(compareBy { it.tier })

                        this@AdminFragment.configurationList = configurationList.toMutableList()
                        configurationAdapter.swapData(configurationList)
                    }
                    flipVisibility(configurationList.isNotEmpty())
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(configurationResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun getAllRequestedConfigurationsAsync() {
        doAsync {
            val alterResponse = client.getRequestedConfigurations(model.token).execute()

            uiThread {
                if (alterResponse.isSuccessful) {
                    var requestsList = alterResponse.body()?.requests ?: mutableListOf()
                    if (requestsList.isNotEmpty()) {
                        requestsList = requestsList.sortedWith(compareBy { it.tier })

                        this@AdminFragment.requestsList = requestsList.toMutableList()
                        requestsAdapter.swapData(requestsList)
                    }
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(alterResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun flipVisibility(listExists: Boolean) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.admin_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getSelectedConfiguration(tier: Int): ArrayList<String> {
        val answer = mutableListOf<String>()
        for (config in configurationList) {
            if (config.tier == tier) {
                answer.addAll(config.fields)
            }
        }
        return answer as ArrayList<String>
    }

    private fun deleteRequestAsynchronously(requestId: String) {
        doAsync {
            val deleteRequestResponse = client.deleteConfigurationRequest(model.token, DeleteConfigurationRequest(requestId)).execute()

            uiThread {
                if (deleteRequestResponse.isSuccessful) {
                    findNavController().navigateUp()
                    getAllRequestedConfigurationsAsync()
                    context?.toast(deleteRequestResponse.body()?.statusMessage ?: "")
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(deleteRequestResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun acceptRequestAsynchronously(requestId: String) {
        doAsync {

        }
    }

    override fun onConfigurationClick(tier: Int) {
        val bundle = Bundle()
        bundle.putStringArrayList(Constants.ALREADY_SELECTED_KEY, getSelectedConfiguration(tier))
        findNavController().navigate(R.id.action_new_configuration, bundle)
    }

    override fun onRequestClick(requestId: String) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Request")
            .setMessage("Do you want to confirm this configuration request?")
            .setPositiveButton("Yes") { _, _ -> acceptRequestAsynchronously(requestId) }
            .setNegativeButton("Reject") { _, _ -> deleteRequestAsynchronously(requestId) }
            .setNeutralButton("Cancel") { dI, _ -> dI.dismiss() }
            .setCancelable(true)
            .show()
    }

    override fun onResume() {
        super.onResume()
        getAllConfigurationsAsync()
        getAllRequestedConfigurationsAsync()
    }
}