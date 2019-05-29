package com.hmproductions.itsmclient.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.TicketRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.utils.Constants.USER_TOKEN
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class MainActivity : AppCompatActivity(), TicketRecyclerAdapter.OnTicketClickListener {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var ticketsRecyclerAdapter: TicketRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.statistics)

        DaggerITSMApplicationComponent.builder().build().inject(this)

        ticketsRecyclerAdapter = TicketRecyclerAdapter(this, null, this)
        setupTicketsRecyclerView()
    }

    private fun setupTicketsRecyclerView() {

        ticketsRecyclerView.layoutManager = LinearLayoutManager(this)
        ticketsRecyclerView.adapter = ticketsRecyclerAdapter
        ticketsRecyclerView.setHasFixedSize(false)

        doAsync {
            val tickets = client.getTickets(USER_TOKEN).execute().body()?.result?.tickets?: mutableListOf()
            Log.v(":::", tickets.toString())
            uiThread {
                ticketsRecyclerAdapter.swapData(tickets)
            }
        }
    }

    override fun onTicketClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
