package com.hmproductions.itsmclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.AccountDetails
import com.hmproductions.itsmclient.data.GenericAuthenticationDetails
import com.hmproductions.itsmclient.data.ITSMViewModel
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var model: ITSMViewModel
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        model = activity?.run { ViewModelProviders.of(this).get(ITSMViewModel::class.java) }
            ?: throw Exception("Invalid activity")

        loadingDialog =
            AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.loading_dialog, null))
                .create()

        loginButton.setOnClickListener { setupLoginButton() }
        signUpNavButton.setOnClickListener { setupSignUpText() }
        forgotPasswordTextView.setOnClickListener { setupForgotPassword() }
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

        loadingDialog.show()

        doAsync {
            val loginResponse = client
                .login(AccountDetails(emailEditText.text.toString(), passwordEditText.text.toString())).execute()

            uiThread {
                loadingDialog.dismiss()

                if (loginResponse.isSuccessful) {
                    model.token = loginResponse.body()?.token ?: ""
                    model.designation = loginResponse.body()?.designation ?: "Unknown"
                    model.company = loginResponse.body()?.company ?: "Unknown"
                    model.email = emailEditText.text.toString()
                    model.requestId = loginResponse.body()?.config_request_id ?: ""
                    model.tier =
                        context?.resources?.getStringArray(R.array.designations)?.indexOf(model.designation) ?: -1 + 1
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

    private fun setupForgotPassword() {
        if (emailEditText.text.toString().isEmpty() || emailEditText.text.toString() == "" ||
            !Patterns.EMAIL_ADDRESS.matcher(emailEditText.text.toString()).matches()
        ) {
            context?.toast(R.string.valid_email_error_text)
            return
        }

        loadingDialog.show()

        doAsync {
            val forgotPasswordResponse = client.forgotPassword(
                GenericAuthenticationDetails(
                    emailEditText.text.toString(), "", "",
                    0, ""
                )
            ).execute()

            uiThread {
                loadingDialog.dismiss()

                if (forgotPasswordResponse.isSuccessful) {
                    context?.toast(forgotPasswordResponse.body()?.statusMessage ?: "")
                } else
                    context?.toast(Miscellaneous.extractErrorMessage(forgotPasswordResponse.errorBody()?.string()))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        with(model) {
            company = "Unknown"
            designation = "Unknown"
            token = ""
            tier = 0
        }
    }
}