package com.hmproductions.itsmclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.FieldRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.ConfigurationRequest
import com.hmproductions.itsmclient.data.ITSMViewModel
import com.hmproductions.itsmclient.utils.Constants.*
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_field.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class ConfigurationFragment : Fragment() {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var fieldAdapter: FieldRecyclerAdapter
    private lateinit var model: ITSMViewModel
    private lateinit var mode: String
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        model = activity?.run { ViewModelProviders.of(this).get(ITSMViewModel::class.java) }
            ?: throw Exception("Invalid activity")

        loadingDialog =
            AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.loading_dialog, null))
                .create()

        fieldAdapter = FieldRecyclerAdapter(context, null)
        fieldsRecyclerView.layoutManager = LinearLayoutManager(context)
        fieldsRecyclerView.adapter = fieldAdapter
        fieldsRecyclerView.setHasFixedSize(true)

        mode = arguments?.getString(CONFIG_FRAGMENT_MODE) ?: ADMIN_USER

        with(tierPicker) {
            maxValue = resources.getStringArray(R.array.designations).size
            minValue = 1
            wrapSelectorWheel = false

            if (mode == ADMIN_USER)
                value = resources.getStringArray(R.array.designations).size / 2
            else {
                value = model.tier
                isEnabled = false
                setConfigButton.setText(R.string.request_config)
            }
        }

        getAllConfigurationsAsync()

        setConfigButton.setOnClickListener { sendNewConfiguration() }
    }

    private fun getAllConfigurationsAsync() {
        loadingDialog.show()
        doAsync {
            val fieldResponse = client.getAvailableFields(model.token).execute()
            val allConfigurationsResponse = client.getConfigurations(model.token).execute()

            uiThread {
                loadingDialog.dismiss()
                if (fieldResponse.isSuccessful) {
                    var fieldList = fieldResponse.body()?.configuration ?: mutableListOf()
                    if (fieldList.isNotEmpty()) {
                        fieldList = fieldList.sortedWith(compareBy { it.field })

                        var incomingList = mutableListOf<String>()
                        if (mode == ADMIN_USER)
                            incomingList =
                                arguments?.getStringArrayList(ALREADY_SELECTED_KEY) ?: mutableListOf()
                        else {
                            for (config in allConfigurationsResponse.body()?.result?.configurations
                                ?: mutableListOf()) {
                                if (config.tier == model.tier) {
                                    incomingList = config.fields as MutableList<String>
                                    break
                                }
                            }
                        }

                        for (item in incomingList) {
                            fieldList.find { it.field == item }?.checked = true
                        }

                        fieldAdapter.swapData(fieldList)
                    }
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(fieldResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun sendNewConfiguration() {
        val updatedList = fieldAdapter.updatedList
        val customList = mutableListOf<String>()

        for (item in updatedList) {
            if (item.checked) customList.add(item.field)
        }

        if (customList.size < 1) {
            context?.toast("Check at least 1 item")
            return
        }

        loadingDialog.show()

        doAsync {
            val genericResponse = if (mode == ADMIN_USER) {
                client.setConfiguration(model.token, ConfigurationRequest(tierPicker.value, customList)).execute()
            } else {
                client.alterConfiguration(model.token, ConfigurationRequest(tierPicker.value, customList)).execute()
            }

            uiThread {
                loadingDialog.dismiss()

                if (genericResponse.isSuccessful) {
                    findNavController().navigateUp()
                    context?.toast(genericResponse.body()?.statusMessage ?: "")
                    if (mode == NORMAL_USER) model.requestId = genericResponse.body()?.config_request_id ?: ""
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(genericResponse.errorBody()?.string()))
                }
            }
        }

    }
}