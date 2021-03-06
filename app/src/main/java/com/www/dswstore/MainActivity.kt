package com.www.dswstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.www.dswstore.util.MainUtil

class MainActivity : AppCompatActivity(){
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Add support for up button for fragment navigation
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, mNavController)

        // Change action Bar background color
        MainUtil().setActionBarBackgroundColor(this, supportActionBar, R.color.colorPrimary)

        // Set navigation bottom background color
        MainUtil().setBottomBarNavigationBackgroundColor(
            window = window,
            this,
            R.color.colorPrimary,
            R.color.bottom_black_color
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }
}