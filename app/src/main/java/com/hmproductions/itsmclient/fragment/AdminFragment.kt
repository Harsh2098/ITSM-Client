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
import com.hmproductions.itsmclient.data.*
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
    private lateinit var loadingDialog: AlertDialog

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

        loadingDialog =
            AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.loading_dialog, null))
                .create()

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
        loadingDialog.show()
        doAsync {
            val configurationResponse = client.getConfigurations(model.token).execute()

            uiThread {
                loadingDialog.dismiss()
                if (configurationResponse.isSuccessful) {
                    var configurationList = configurationResponse.body()?.result?.configurations ?: mutableListOf()
                    if (configurationList.isNotEmpty()) {
                        configurationList = configurationList.sortedWith(compareBy { it.tier })

                        this@AdminFragment.configurationList = configurationList.toMutableList()
                        configurationAdapter.swapData(configurationList)
                    } else {
                        this@AdminFragment.configurationList.clear()
                    }
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(configurationResponse.errorBody()?.string()))
                }
                flipSetConfigVisibility(configurationList.isNotEmpty())
            }
        }
    }

    private fun getAllRequestedConfigurationsAsync() {
        loadingDialog.show()
        doAsync {
            val alterResponse = client.getRequestedConfigurationsForAdmin(model.token).execute()

            uiThread {
                loadingDialog.dismiss()
                if (alterResponse.isSuccessful) {
                    var requestsList = alterResponse.body()?.requests ?: mutableListOf()
                    if (requestsList.isNotEmpty()) {
                        requestsList = requestsList.sortedWith(compareBy { it.tier })

                        this@AdminFragment.requestsList = requestsList.toMutableList()
                        requestsAdapter.swapData(requestsList)
                    } else {
                        this@AdminFragment.requestsList.clear()
                    }
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(alterResponse.errorBody()?.string()))
                }
                flipRequestConfigVisibility(requestsList.isNotEmpty())
            }
        }
    }

    private fun flipSetConfigVisibility(listExists: Boolean) {
        noConfigurationsTextView.visibility = if (listExists) View.GONE else View.VISIBLE
        configurationsRecyclerView.visibility = if (listExists) View.VISIBLE else View.INVISIBLE
    }

    private fun flipRequestConfigVisibility(listExists: Boolean) {
        noRequestsTextView.visibility = if (listExists) View.GONE else View.VISIBLE
        requestsRecyclerView.visibility = if (listExists) View.VISIBLE else View.INVISIBLE
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
        loadingDialog.show()
        doAsync {
            val deleteRequestResponse =
                client.deleteConfigurationRequest(model.token, DeleteConfigurationRequest(requestId)).execute()

            uiThread {
                loadingDialog.dismiss()
                if (deleteRequestResponse.isSuccessful) {
                    getAllRequestedConfigurationsAsync()
                    context?.toast(deleteRequestResponse.body()?.statusMessage ?: "")
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(deleteRequestResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun acceptRequestAsynchronously(requestId: String) {
        loadingDialog.show()
        doAsync {
            val currentTier = getTierFromRequestId(requestId)
            val allFields = getCombinedFields(configurationList, requestsList, requestId, currentTier)
            val setConfigurationResponse =
                client.setConfiguration(model.token, ConfigurationRequest(currentTier, allFields)).execute()
            uiThread {
                loadingDialog.dismiss()
                if (setConfigurationResponse.isSuccessful) {
                    deleteRequestAsynchronously(requestId)
                    getAllRequestedConfigurationsAsync()
                    getAllConfigurationsAsync()
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(setConfigurationResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun getCombinedFields(
        configList: List<Configuration>,
        requestList: List<AlterRequest>,
        uniqueId: String,
        tier: Int
    ): List<String> {
        val combinedFields = mutableListOf<String>()
        for (currentConfig in configList) {
            if (currentConfig.tier == tier)
                combinedFields.addAll(currentConfig.fields)
        }

        for (currentRequest in requestList) {
            if (currentRequest.id == uniqueId) {
                combinedFields.addAll(currentRequest.fields)
            }
        }

        return combinedFields.distinct()
    }

    private fun getTierFromRequestId(requestId: String): Int {
        for (currentRequest in requestsList) {
            if (currentRequest.id == requestId)
                return currentRequest.tier
        }

        return -1
    }

    override fun onConfigurationClick(tier: Int) {
        val bundle = Bundle()
        bundle.putStringArrayList(Constants.ALREADY_SELECTED_KEY, getSelectedConfiguration(tier))
        findNavController().navigate(R.id.action_new_configuration, bundle)
    }

    override fun onRequestClick(requestId: String) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Request")
            .setMessage("Requested fields will be appended to existing configuration. Do you want to accept this request?")
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