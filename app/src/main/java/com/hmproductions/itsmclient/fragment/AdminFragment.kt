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
import com.hmproductions.itsmclient.adapter.ConfigurationRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.utils.Constants.USER_TOKEN
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_admin.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class AdminFragment : Fragment(), ConfigurationRecyclerAdapter.OnConfigurationClickListener {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var configurationAdapter: ConfigurationRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        configurationAdapter = ConfigurationRecyclerAdapter(context, null, this)
        configurationsRecyclerView.layoutManager = LinearLayoutManager(context)
        configurationsRecyclerView.adapter = configurationAdapter
        configurationsRecyclerView.setHasFixedSize(true)

        addConfigurationFab.setOnClickListener { findNavController().navigate(R.id.action_new_configuration) }
    }

    private fun getAllConfigurationsAsync() {
        doAsync {
            val configurationResponse = client.getConfigurations(USER_TOKEN).execute()

            uiThread {
                if (configurationResponse.isSuccessful) {
                    var configurationList = configurationResponse.body()?.result?.configurations ?: mutableListOf()
                    if (configurationList.isNotEmpty()) {
                        configurationList = configurationList.sortedWith(compareBy { it.tier })
                        configurationAdapter.swapData(configurationList)
                    }
                    flipVisibility(configurationList.isNotEmpty())
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(configurationResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun flipVisibility(listExists: Boolean) {

    }

    override fun onConfigurationClick(position: Int) {

    }

    override fun onResume() {
        super.onResume()
        getAllConfigurationsAsync()
    }
}