package com.hmproductions.itsmclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.GenericAuthenticationDetails
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_signup.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class SignUpFragment : Fragment() {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        loadingDialog =
            AlertDialog.Builder(context).setView(LayoutInflater.from(context).inflate(R.layout.loading_dialog, null))
                .create()

        val spinnerAdapter =
            ArrayAdapter.createFromResource(context!!, R.array.designations, android.R.layout.simple_list_item_1)
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
            context?.toast(R.string.valid_email_error_text)
            return
        }

        if (password.isEmpty() || password.isBlank()) {
            context?.toast(R.string.valid_password_error_text)
            return
        }

        if (password != confirmPassword) {
            context?.toast(R.string.passwords_dont_match)
            return
        }

        if (company.isEmpty() || company.isBlank()) {
            context?.toast(R.string.valid_password_error_text)
            return
        }

        val designation = resources.getStringArray(R.array.designations)[designationSpinner.selectedItemPosition]

        loadingDialog.show()

        doAsync {
            val signUpResponse = client.signUp(
                GenericAuthenticationDetails(
                    email, password, company,
                    designationSpinner.selectedItemPosition + 1, designation
                )
            ).execute()

            uiThread {
                loadingDialog.show()
                if (signUpResponse.isSuccessful) {
                    context?.toast(signUpResponse.body()?.statusMessage ?: "Registration success")
                    findNavController().navigateUp()
                } else
                    context?.toast(Miscellaneous.extractErrorMessage(signUpResponse.errorBody()?.string()))
            }
        }
    }
}
