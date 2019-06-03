package com.hmproductions.itsmclient.dagger

import com.hmproductions.itsmclient.ui.LoginActivity
import com.hmproductions.itsmclient.ui.MainActivity
import com.hmproductions.itsmclient.ui.SignUpActivity
import dagger.Component

@ITSMApplicationScope
@Component(modules = [ContextModule::class, ClientModule::class])
interface ITSMApplicationComponent {

    fun inject(loginActivity: LoginActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(signUpActivity: SignUpActivity)
}
