package com.klauncher.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.View
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.ui.applications.ApplicationsContract
import com.klauncher.ui.applications.ApplicationsFragment
import com.klauncher.ui.main.MainFragment
import android.content.Intent
import android.net.Uri
import android.view.Gravity


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

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawers()
        }
    }

    fun openDialer(view: View) {
        startActivity(getPackageManager().getLaunchIntentForPackage(
                getString(R.string.dialer_package_name)))
    }

    fun openMessages(view: View) {
        val intent = Intent(Intent.ACTION_SENDTO,
                Uri.parse("smsto:" + getString(R.string.moni_number)))
        startActivity(intent)
    }

    fun openDots(view: View) {
        packageManager.getLaunchIntentForPackage(getString(R.string.dot_pad_packagename))?.let { intent ->
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(intent)
        }
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

    override fun onDrawerStateChanged(newState: Int) { }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }

    override fun onDrawerClosed(drawerView: View) {
        val fragment = fragmentManager.findFragmentById(R.id.right_drawer)
        when (fragment) {
            is ApplicationsContract.View -> fragment.hideSorting()
        }
    }

    override fun onDrawerOpened(drawerView: View) { }

    private val drawer: DrawerLayout by bind(R.id.drawer_layout)
}