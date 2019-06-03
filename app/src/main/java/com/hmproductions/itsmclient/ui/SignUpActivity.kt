package com.hmproductions.itsmclient.ui

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.SignUpDetails
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class SignUpActivity : AppCompatActivity() {

    @Inject
    lateinit var client: ITSMClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        title = "Sign Up"
        DaggerITSMApplicationComponent.builder().build().inject(this)

        val spinnerAdapter =
            ArrayAdapter.createFromResource(this, R.array.designations, android.R.layout.simple_spinner_item)
        designationSpinner.adapter = spinnerAdapter

        signUpButton.setOnClickListener {
            onSignUpButtonClick(
                emailEditText.text.toString(), passwordEditText.text.toString(),
                confirmPasswordEditText.text.toString(), companyEditText.text.toString()
            )
        }
    }

    private fun onSignUpButtonClick(email: String, password: String, confirmPassword: String, company: String) {
        if (email.isEmpty() || email == "" || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast(R.string.valid_email_error_text)
            return
        }

        if (password.isEmpty() || password.isBlank()) {
            toast(R.string.valid_password_error_text)
            return
        }

        if (password != confirmPassword) {
            toast(R.string.passwords_dont_match)
            return
        }

        if (company.isEmpty() || company.isBlank()) {
            toast(R.string.valid_password_error_text)
            return
        }

        val designation = resources.getStringArray(R.array.designations)[designationSpinner.selectedItemPosition]
        val isAdmin = email.contains("harsh", true)

        doAsync {
            val signUpResponse = client.signUp(
                SignUpDetails(
                    email, password, company,
                    designationSpinner.selectedItemPosition + 1, designation, isAdmin
                )
            ).execute()

            uiThread {
                if (signUpResponse.isSuccessful) {
                    finish()
                }

                toast(Miscellaneous.extractErrorMessage(signUpResponse.errorBody()?.string()))
            }
        }
    }
}
