package com.hmproductions.itsmclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.FieldRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.ConfigurationRequest
import com.hmproductions.itsmclient.utils.Constants.USER_TOKEN
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        fieldAdapter = FieldRecyclerAdapter(context, null)
        fieldsRecyclerView.layoutManager = LinearLayoutManager(context)
        fieldsRecyclerView.adapter = fieldAdapter
        fieldsRecyclerView.setHasFixedSize(true)

        with(tierPicker) {
            maxValue = resources.getStringArray(R.array.designations).size
            minValue = 1
            wrapSelectorWheel = false

            value = resources.getStringArray(R.array.designations).size / 2
        }

        getAllConfigurationsAsync()

        setConfigButton.setOnClickListener { sendNewConfiguration() }
    }

    private fun getAllConfigurationsAsync() {
        doAsync {
            val fieldResponse = client.getAvailableFields(USER_TOKEN).execute()

            uiThread {
                if (fieldResponse.isSuccessful) {
                    var fieldList = fieldResponse.body()?.configuration ?: mutableListOf()
                    if (fieldList.isNotEmpty()) {
                        fieldList = fieldList.sortedWith(compareBy({it.field}))
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

        doAsync {
            val createConfigResponse =
                client.setConfiguration(USER_TOKEN, ConfigurationRequest(tierPicker.value, customList)).execute()

            uiThread {
                if (createConfigResponse.isSuccessful) {
                    findNavController().navigateUp()
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(createConfigResponse.errorBody()?.string()))
                }
            }
        }
    }
}