package com.klauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DotsReceiver : BroadcastReceiver() {

    companion object {
        val DOTS_COUNT_KEY = "dotsCount"
        val DOTS_FORWARD_ACTION = "forwardDotsCount"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.run {
            if (hasExtra(DOTS_COUNT_KEY)) {
                context?.sendBroadcast(Intent().apply {
                    action = DOTS_FORWARD_ACTION
                    putExtras(intent.extras)
                })
            }
        }
    }
}