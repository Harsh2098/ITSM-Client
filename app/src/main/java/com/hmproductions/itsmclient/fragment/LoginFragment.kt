package com.hmproductions.itsmclient.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.LoginDetails
import com.hmproductions.itsmclient.utils.Constants
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var client: ITSMClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        loginButton.setOnClickListener { setupLoginButton() }
        signUpTextView.setOnClickListener { setupSignUpText() }
    }

    private fun setupLoginButton() {
        if (emailEditText.text.toString().isEmpty() || emailEditText.text.toString() == "" ||
            !Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()
        ) {
            context?.toast(R.string.valid_email_error_text)
            return
        }

        if (passwordEditText.text.isEmpty() || passwordEditText.text.toString() == "" || passwordEditText.text.toString().length < 8) {
            context?.toast(R.string.valid_password_error_text)
            return
        }

        doAsync {
            val loginResponse = client
                .login(LoginDetails(emailEditText.text.toString(), passwordEditText.text.toString())).execute()

            uiThread {
                if (loginResponse.isSuccessful) {
                    Constants.USER_TOKEN = loginResponse.body()?.token
                    if (loginResponse.body()?.isAdmin == true)
                        findNavController().navigate(R.id.action_successful_admin_login)
                    else
                        findNavController().navigate(R.id.action_successful_normal_login)
                } else {
                    context?.toast(Miscellaneous.extractErrorMessage(loginResponse.errorBody()?.string()))
                }
            }
        }
    }

    private fun setupSignUpText() {
        findNavController().navigate(R.id.action_signup)
    }
}