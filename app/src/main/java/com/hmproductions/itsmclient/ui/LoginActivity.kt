package com.hmproductions.itsmclient.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.ContextModule
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.LoginDetails
import com.hmproductions.itsmclient.utils.Constants
import com.hmproductions.itsmclient.utils.Miscellaneous.extractErrorMessage
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var client: ITSMClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        DaggerITSMApplicationComponent.builder().contextModule(ContextModule(this)).build().inject(this)

        loginButton.setOnClickListener { setupLoginButton() }
        signUpTextView.setOnClickListener { setupSignUpText() }
    }

    private fun setupLoginButton() {
        if (emailEditText.text.toString().isEmpty() || emailEditText.text.toString() == "" ||
            !Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()
        ) {
            toast(R.string.valid_email_error_text)
            return
        }

        if (passwordEditText.text.isEmpty() || passwordEditText.text.toString() == "" || passwordEditText.text.toString().length < 8) {
            toast(R.string.valid_password_error_text)
            return
        }

        doAsync {
            val loginResponse = client
                .login(LoginDetails(emailEditText.text.toString(), passwordEditText.text.toString())).execute()

            uiThread {
                if (loginResponse.isSuccessful) {
                    Constants.USER_TOKEN = loginResponse.body()?.token
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    toast(extractErrorMessage(loginResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun setupSignUpText() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }
}
