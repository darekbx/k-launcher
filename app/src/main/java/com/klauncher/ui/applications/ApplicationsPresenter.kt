package com.klauncher.ui.applications

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo

import com.klauncher.extensions.threadToAndroid
import com.klauncher.model.Application
import com.klauncher.repository.Repository
import com.klauncher.repository.tables.Entry as DBApplication

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.HashSet

open class ApplicationsPresenter(val view: ApplicationsContract.View): ApplicationsContract.Presenter {

    companion object {
        val CLICK_COUNT_ENABLED = false
    }

    override fun loadItems() {
        view?.let {
            fetchAndCount()
                    .threadToAndroid()
                    .subscribe { applications ->
                        it.setItems(applications)
                        it.setDistinctLetters(extractDistinctLetters(applications))
                    }
        }
    }

    override fun increaseClickCount(application: Application) {
        if (CLICK_COUNT_ENABLED) {
            repository().increaseCountByOne(application.packageName, application.name)
            loadItems()
        }
    }

    override fun openApplication(application: Application) {
        increaseClickCount(application)
        view?.let {
            val packageManager = it.getContext().packageManager
            val intent = packageManager.getLaunchIntentForPackage(application.packageName)
            it.getContext().startActivity(intent)
        }
    }

    internal fun fetchAndCount(): Single<List<Application>> =
            Single.zip(
                    loadItemsSynchronously(view.getContext().packageManager),
                    loadCountInfo(),
                    BiFunction<List<Application>, List<DBApplication>, List<Application>> { applications, rows ->
                        applyClickCount(applications, rows)
                        return@BiFunction sortItems(applications)
                    }
            )

    private fun sortItems(applications: List<Application>) =
            applications.sortedByDescending { application -> application.clickCount }

    internal fun extractDistinctLetters(applications: List<Application>)
            = HashSet(applications.map { it() })

    internal fun applyClickCount(applications: List<Application>, rows: List<DBApplication>) {
        applications.forEach { application ->
            application.clickCount =
                    rows.firstOrNull { row ->
                        row.packageName == application.packageName
                    }?.clickCount ?: 0
        }

        // Own space will be always at first place
        applications
                .firstOrNull { it.packageName == "com.darekbx.ownspace" }?.clickCount = Int.MAX_VALUE
    }

    internal fun loadItemsSynchronously(pm: PackageManager) =
            Single.create<List<Application>> {
                val applicationsInfo = queryActivities(pm)
                val applications = mapToApplications(pm, applicationsInfo)
                it.onSuccess(applications)
            }

    internal open fun loadCountInfo() =
            Single.create<List<DBApplication>> {
                database()?.run {
                    val rows = entryDao().listAll()
                    close()
                    it.onSuccess(rows)
                }
            }

    internal fun createLauncherIntent() =
            Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

    internal open fun mapToApplications(pm: PackageManager, applicationsInfo: MutableList<ResolveInfo>) =
            applicationsInfo.map { info ->
                Application(loadLabel(pm, info), loadPackagename(info), loadIcon(info))
            }

    internal open fun queryActivities(pm: PackageManager): MutableList<ResolveInfo> {
        val launcherIntent = createLauncherIntent()
        return pm.queryIntentActivities(launcherIntent, 0)
    }

    internal open fun repository() = Repository(view.getContext())
    internal open fun database() = repository()?.database()
    internal open fun loadLabel(pm: PackageManager, resolveInfo: ResolveInfo) = resolveInfo.loadLabel(pm).toString()
    internal open fun loadPackagename(resolveInfo: ResolveInfo) = resolveInfo.activityInfo.applicationInfo.packageName
    internal open fun loadIcon(resolveInfo: ResolveInfo) = iconLoader.loadIcon(resolveInfo)

    private val iconLoader: IconLoader by lazy {
        IconLoader(view.getContext())
    }
}