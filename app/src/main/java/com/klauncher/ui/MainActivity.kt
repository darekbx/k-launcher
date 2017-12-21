package com.klauncher.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.klauncher.R
import com.klauncher.airly.AirlyController
import com.klauncher.extensions.bind
import com.klauncher.extensions.threadToAndroid
import com.klauncher.ui.applications.ApplicationsContract
import com.klauncher.ui.applications.ApplicationsFragment
import com.klauncher.ui.main.MainFragment
import org.koin.android.ext.android.inject

class MainActivity : Activity, DrawerLayout.DrawerListener {

    constructor() : super()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        addApplicationsGrid()
        addMainFragment()

        drawer.addDrawerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        drawer.removeDrawerListener(this)
    }

    private fun addApplicationsGrid() {
        val applicationsGrid = ApplicationsFragment().apply {
            closePublisher.subscribe { closeDrawer() }
        }
        fragmentManager
                .beginTransaction()
                .add(R.id.right_drawer, applicationsGrid)
                .commitAllowingStateLoss()
    }

    private fun addMainFragment() {
        val fragment = MainFragment()
        fragmentManager
                .beginTransaction()
                .add(R.id.content_frame, fragment)
                .commitAllowingStateLoss()
    }

    private fun closeDrawer() {
        drawer.closeDrawers()
    }

    private val drawer: DrawerLayout by bind(R.id.drawer_layout)

    override fun onDrawerStateChanged(newState: Int) {}
    override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {}
    override fun onDrawerOpened(drawerView: View?) {}

    override fun onDrawerClosed(drawerView: View?) {
        val fragment = fragmentManager.findFragmentById(R.id.right_drawer)
        when (fragment) {
            is ApplicationsContract.View -> fragment.hideSorting()
        }
    }
}