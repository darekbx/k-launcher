package com.klauncher.ui.applications

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import com.klauncher.R

open class IconLoader(val context: Context) {

    companion object {
        val DEFAULT_ICON = R.mipmap.ic_launcher
        val CACHE_ENABLED = false
        val cache = hashMapOf<String, Drawable>()
    }

    fun loadIcon(resolveInfo: ResolveInfo): Drawable {
        val packageName = getPackageName(resolveInfo)
        return when {
            CACHE_ENABLED && cache.containsKey(packageName) -> cache.getValue(packageName)
            else -> loadAndStore(resolveInfo, packageName)
        }
    }

    internal open fun loadAndStore(resolveInfo: ResolveInfo, packageName: String): Drawable {
        with(loadApplicationIcon(resolveInfo)) {
            cache.put(packageName, this)
            return this
        }
    }

    internal open fun loadApplicationIcon(resolveInfo: ResolveInfo): Drawable {
        return try {
            packageManager.getApplicationIcon(applicationInfo(resolveInfo))
        } catch (e: Throwable) {
            context.resources.getDrawable(DEFAULT_ICON, context.theme)
        }
    }

    internal open fun getPackageName(resolveInfo: ResolveInfo) = applicationInfo(resolveInfo).packageName
    private fun applicationInfo(resolveInfo: ResolveInfo) = resolveInfo.activityInfo.applicationInfo

    private val packageManager: PackageManager by lazy {
        context.packageManager
    }
}