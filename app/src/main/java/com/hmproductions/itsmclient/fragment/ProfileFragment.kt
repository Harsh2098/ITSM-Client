package com.hmproductions.itsmclient.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.dagger.DaggerITSMApplicationComponent
import com.hmproductions.itsmclient.data.ChangePasswordDetails
import com.hmproductions.itsmclient.data.ITSMViewModel
import com.hmproductions.itsmclient.utils.Constants.CONFIG_FRAGMENT_MODE
import com.hmproductions.itsmclient.utils.Constants.NORMAL_USER
import com.hmproductions.itsmclient.utils.Miscellaneous
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var client: ITSMClient

    private lateinit var model: ITSMViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerITSMApplicationComponent.builder().build().inject(this)

        model = activity?.run { ViewModelProviders.of(this).get(ITSMViewModel::class.java) }
            ?: throw Exception("Invalid activity")

        profileEmailTextView.text = model.email
        profileCompanyTextView.text = model.company
        profileDesignationTextView.text = model.designation

        setupChangePasswordTextView()
        setupRequestConfigurationTextView()
    }

    private fun setupChangePasswordTextView() {
        changePasswordTextView.setOnClickListener {
            val customChangePasswordView = LayoutInflater.from(context).inflate(R.layout.change_password_dialog, null)
            val oldPasswordEditText = customChangePasswordView.findViewById<EditText>(R.id.oldPasswordEditText)
            val newPasswordEditText = customChangePasswordView.findViewById<EditText>(R.id.newPasswordEditText)

            AlertDialog.Builder(context)
                .setPositiveButton("Change") { _, _ ->
                    run {
                        val oldPassword = oldPasswordEditText.text.toString()
                        val newPassword = newPasswordEditText.text.toString()

                        if (oldPassword.isEmpty() || oldPassword.isBlank()) {
                            context?.toast("Enter old password")
                        } else if (newPassword.isEmpty() || newPassword.isBlank()) {
                            context?.toast("Enter new password")
                        } else {
                            changePasswordAsynchronously(oldPassword, newPassword)
                        }
                    }
                }
                .setCancelable(false)
                .setTitle(R.string.change_password)
                .setNegativeButton("Cancel") { dI, _ -> dI.dismiss() }
                .setView(customChangePasswordView)
                .show()
        }
    }

    private fun setupRequestConfigurationTextView() {
        requestConfigurationTextView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(CONFIG_FRAGMENT_MODE, NORMAL_USER)
            findNavController().navigate(R.id.action_request_configuration, bundle)
        }
    }

    private fun changePasswordAsynchronously(oldPassword: String, newPassword: String) {
        val changePasswordDetails = ChangePasswordDetails(model.email, oldPassword, newPassword)

        doAsync {
            val genericResponse = client.changePassword(model.token, changePasswordDetails).execute()

            uiThread {
                if (genericResponse.isSuccessful) {
                    context?.toast(genericResponse.body()?.statusMessage ?: "")
                } else
                    context?.toast(Miscellaneous.extractErrorMessage(genericResponse.errorBody()?.string()))
            }
        }
    }
}