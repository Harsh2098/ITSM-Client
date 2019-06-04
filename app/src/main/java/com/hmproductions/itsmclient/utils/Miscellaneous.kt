package com.hmproductions.itsmclient.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.hmproductions.itsmclient.data.ErrorMessage
import com.hmproductions.itsmclient.data.LoginResponse

object Miscellaneous {

    fun extractErrorMessage(jsonString: String?): String {
        if (jsonString == null || jsonString.isBlank() || jsonString.isEmpty()) return "Internal server error"
        return Gson().fromJson(jsonString, ErrorMessage::class.java).message
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
