package com.hmproductions.itsmclient.dagger

import com.hmproductions.itsmclient.fragment.LoginFragment
import com.hmproductions.itsmclient.ui.MainActivity
import com.hmproductions.itsmclient.fragment.SignUpFragment
import dagger.Component

@ITSMApplicationScope
@Component(modules = [ContextModule::class, ClientModule::class])
interface ITSMApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(loginFragment: LoginFragment)
    fun inject(signUpFragment: SignUpFragment)
}
