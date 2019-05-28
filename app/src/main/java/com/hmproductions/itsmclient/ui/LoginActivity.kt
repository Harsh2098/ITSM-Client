package com.hmproductions.itsmclient.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.ContextModule
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.LoginDetails
import com.hmproductions.itsmclient.utils.Constants
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

        setupLoginButton()
    }

    private fun setupLoginButton() {
        loginButton.setOnClickListener {
            if(emailEditText.text.toString().isEmpty() || emailEditText.text.toString() == "" ||
                !Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()) {
                toast(R.string.valid_email_error_text)
                return@setOnClickListener
            }

            if(passwordEditText.text.isEmpty() || passwordEditText.text.toString() == "" || passwordEditText.text.toString().length < 8) {
                toast(R.string.valid_password_error_text)
                return@setOnClickListener
            }

            doAsync {
                val loginResponse = client
                    .login(LoginDetails(emailEditText.text.toString(), passwordEditText.text.toString()))
                    .execute()
                    .body()

                uiThread {
                    if (loginResponse != null) {
                        with(loginResponse) {
                            if (statusCode != 200 && statusCode != 201) {
                                toast(message)
                            } else if (token == "") {

                            } else {
                                Constants.USER_TOKEN = token
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}
