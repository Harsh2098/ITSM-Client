package com.hmproductions.itsmclient.data

import androidx.lifecycle.ViewModel

class ITSMViewModel : ViewModel() {
    var token = ""
    var designation = "Unknown"
    var company = "Unknown"
    var email = ""
    var tier = -1
    var requestId = ""
}