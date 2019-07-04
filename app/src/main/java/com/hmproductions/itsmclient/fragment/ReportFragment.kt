package com.hmproductions.itsmclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.ReportRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.CoreData
import com.hmproductions.itsmclient.data.ITSMViewModel
import com.hmproductions.itsmclient.data.ReportEntry
import com.hmproductions.itsmclient.utils.Constants
import kotlinx.android.synthetic.main.fragment_report.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class ReportFragment : Fragment(), ReportRecyclerAdapter.OnReportItemClickListener {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var coreData: ArrayList<CoreData>
    private lateinit var reportRecyclerAdapter: ReportRecyclerAdapter
    private lateinit var model: ITSMViewModel

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        model = activity?.run { ViewModelProviders.of(this).get(ITSMViewModel::class.java) }
            ?: throw Exception("Invalid activity")

        loadingDialog =  AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)).create()

        coreData = arguments?.getParcelableArrayList(Constants.CORE_DATA_KEY)
            ?: mutableListOf<CoreData>() as ArrayList<CoreData>

        reportRecyclerAdapter = ReportRecyclerAdapter(null, context, this)
        reportRecyclerView.layoutManager = LinearLayoutManager(context)
        reportRecyclerView.setHasFixedSize(false)
        reportRecyclerView.adapter = reportRecyclerAdapter

        setupReport()
    }

    private fun setupReport() {
        loadingDialog.show()

        doAsync {
            val reportResponse = client.getReport(model.token).execute()

            uiThread {
                loadingDialog.dismiss()
                reportRecyclerAdapter.swapData(extractReportFromJson(reportResponse.body()?.string() ?: ""))
            }
        }
    }

    private fun extractReportFromJson(jsonString: String): ArrayList<ReportEntry> {

        val answer = mutableListOf<ReportEntry>()
        try {
            val root = JSONObject(jsonString)
            val iterator = root.keys()

            while (iterator.hasNext()) {
                val key = iterator.next()
                val reportEntryValue = root.getString(key)
                answer.add(ReportEntry(addSpaces(key), reportEntryValue))
            }

        } catch (e: JSONException) {
            context?.toast("Error parsing some fields")
        }
        return answer as ArrayList<ReportEntry>
    }

    private fun addSpaces(old: String): String {
        val builder = StringBuilder("")
        for(ch in old) {
            if(ch.isUpperCase()) {
                builder.append(" ")
            }
            builder.append(ch)
        }
        return builder.toString().capitalize()
    }

    override fun onReportEntryClick(position: Int) {

    }
}