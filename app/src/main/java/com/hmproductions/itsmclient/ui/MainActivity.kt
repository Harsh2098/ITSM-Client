package com.hmproductions.itsmclient.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hmproductions.itsmclient.R
import com.hmproductions.itsmclient.utils.hideKeyboard
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.contentView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)

        setSupportActionBar(mainToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.hamburger_icon))

        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
        val host = supportFragmentManager.findFragmentById(R.id.main_host_fragment) as NavHostFragment
        navigationViewLeft.setupWithNavController(host.navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        contentView?.hideKeyboard()

        when (item.itemId) {
            android.R.id.home -> {
                mainDrawerLayout?.openDrawer(GravityCompat.START)  // OPEN DRAWER
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
