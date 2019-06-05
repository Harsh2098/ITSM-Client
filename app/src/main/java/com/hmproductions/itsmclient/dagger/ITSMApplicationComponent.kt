package com.hmproductions.itsmclient.dagger

import com.hmproductions.itsmclient.fragment.*
import com.hmproductions.itsmclient.ui.MainActivity
import dagger.Component

@ITSMApplicationScope
@Component(modules = [ContextModule::class, ClientModule::class])
interface ITSMApplicationComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(loginFragment: LoginFragment)
    fun inject(signUpFragment: SignUpFragment)
    fun inject(adminFragment: AdminFragment)
    fun inject(configurationFragment: ConfigurationFragment)
    fun inject(homeFragment: HomeFragment)
}
