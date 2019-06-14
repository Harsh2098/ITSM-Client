package com.hmproductions.itsmclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.data.CoreData
import com.hmproductions.itsmclient.utils.Constants

class ReportFragment : Fragment() {

    private lateinit var coreData: ArrayList<CoreData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coreData = arguments?.getParcelableArrayList(Constants.CORE_DATA_KEY)
            ?: mutableListOf<CoreData>() as ArrayList<CoreData>

    }
}