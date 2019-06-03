package com.hmproductions.itsmclient.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.adapter.TicketRecyclerAdapter
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.utils.Constants
import org.jetbrains.anko.toast
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
        toast(Constants.USER_TOKEN)

        ticketsRecyclerAdapter = TicketRecyclerAdapter(this, null, this)
    }


    override fun onTicketClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
