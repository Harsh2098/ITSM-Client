package com.hmproductions.itsmclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.GraphRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.CoreData
import com.hmproductions.itsmclient.data.ITSMViewModel
import com.hmproductions.itsmclient.utils.Constants
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var graphRecyclerAdapter: GraphRecyclerAdapter
    private lateinit var model: ITSMViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        model = activity?.run { ViewModelProviders.of(this).get(ITSMViewModel::class.java) }
            ?: throw Exception("Invalid activity")

        graphRecyclerAdapter = GraphRecyclerAdapter(context, null)
        val snapHelper = PagerSnapHelper()

        graphsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        graphsRecyclerView.adapter = graphRecyclerAdapter
        graphsRecyclerView.setHasFixedSize(false)
        snapHelper.attachToRecyclerView(graphsRecyclerView)

        getCoreData()
    }

    private fun getCoreData() {
        doAsync {
            val coreDataResponse = client.getCoreData(model.token).execute()

            uiThread {
                val data = extractFieldsFromJson(coreDataResponse.body()?.string() ?: "")

                if (data.isEmpty()) {
                    flipVisibilities(false)
                } else {
                    flipVisibilities(true)
                    graphRecyclerAdapter.swapData(data)
                }

                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.CORE_DATA_KEY, data)
                intelligentReportButton.setOnClickListener { findNavController().navigate(R.id.action_report, bundle) }
            }
        }
    }

    private fun extractFieldsFromJson(jsonString: String): ArrayList<CoreData> {

        val answer = mutableListOf<CoreData>()
        try {
            val root = JSONObject(jsonString)
            val globalTickets = root.getJSONArray("globalTickets")

            for (i in 0 until globalTickets.length()) {
                val currentField = globalTickets.getJSONObject(i)
                val iterator = currentField.keys()

                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val newCoreData = CoreData(key, mutableListOf(), mutableListOf())
                    val fieldArray = currentField.getJSONArray(key)

                    for (j in 0 until fieldArray.length()) {
                        try {
                            val tempString = fieldArray.getJSONObject(j).getString("value")
                            newCoreData.intValues.add(tempString.toInt())
                        } catch (e: NumberFormatException) {
                            val tempString = fieldArray.getJSONObject(j).getString("value")
                            newCoreData.stringValues.add(tempString.toLowerCase())
                        } catch (e: JSONException) {
                            context?.toast("Error parsing some fields")
                        }
                    }

                    if(newCoreData.stringValues.size == 0 && newCoreData.intValues.size == 0)
                        continue

                    answer.add(newCoreData)
                }
            }
        } catch (e: JSONException) {
            context?.toast("Error parsing some fields")
        }
        return answer as ArrayList<CoreData>
    }

    private fun flipVisibilities(dataPresent: Boolean = false) {
        noDataTextView.visibility = if (dataPresent) View.GONE else View.VISIBLE
        graphsRecyclerView.visibility = if (dataPresent) View.VISIBLE else View.GONE
    }
}