package com.klauncher.ui.applications

import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.extensions.hide
import com.klauncher.extensions.show
import com.klauncher.model.Application
import com.klauncher.ui.applications.sortinggrid.SortingGrid
import io.reactivex.subjects.PublishSubject

class ApplicationsFragment : Fragment(), ApplicationsContract.View {

    private val timeKeeperApps by lazy { listOf("olx", "price observer") }

    val applicationsReceiver =  object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED -> presenter.loadItems()
            }
        }
    }

    private val presenter: ApplicationsContract.Presenter = ApplicationsPresenter(this)
    private val applicationsList = ArrayList<Application>()
    val closePublisher: PublishSubject<Any> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context).inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadItems()
        registerApplicationsBroadcast()
        with (grid) {
            adapter = applicationsAdapter
            setOnItemClickListener { _, _, position, _ -> handleOnClick(position) }
            setOnItemLongClickListener { _, _, position, _ ->
                val application = applicationsAdapter.getItem(position)
                uninstall(application)
                //showSorting()
                true
            }
        }

        sortingGrid.setOnItemClickListener { _, _, position, _ -> handleLetterClick(position) }
    }

    fun uninstall(application: Application) {
        startActivity(
                Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.fromParts("package", application.packageName, null))
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context.unregisterReceiver(applicationsReceiver)
    }

    override fun setDistinctLetters(letters: HashSet<Char>) {
        sortingGrid.fillUsedLetters(letters)
    }

    private fun handleLetterClick(position: Int) {
        val letter = sortingGrid.charByPosition(position)
        refreshAdapter(applicationsList.filter { it().equals(letter) })
        sortingGrid.hide()
    }

    private fun showSorting() {
        sortingGrid.show()
    }

    override fun hideSorting() {
        sortingGrid.hide()
        refreshAdapter(applicationsList)
    }

    private fun handleOnClick(position: Int) {
        val application = applicationsAdapter.getItem(position)

        if (timeKeeperApps.any { application.name.toLowerCase().startsWith(it.toLowerCase()) }) {
            if (!timeKeeper.canOpen()) {
                Toast.makeText(context, R.string.time_keeper_warning, Toast.LENGTH_SHORT).show()
                return
            }
        }

        presenter.openApplication(application)
        closePublisher.onNext(0)
        refreshAdapter(applicationsList)
    }

    override fun setItems(items: List<Application>) {
        applicationsList.clear()
        applicationsList.addAll(items)
        refreshAdapter(applicationsList)
    }

    private fun refreshAdapter(items: List<Application>) {
        with(applicationsAdapter) {
            clear()
            addAll(items)
            notifyDataSetChanged()
        }
        grid.post({ grid.setSelection(0) })
    }

    private fun registerApplicationsBroadcast() {
        context.registerReceiver(applicationsReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        })
    }

    val applicationsAdapter: ApplicationsAdapter by lazy {
        ApplicationsAdapter(context)
    }

    val grid: GridView by bind(R.id.grid_view)
    val sortingGrid: SortingGrid by bind(R.id.sorting_view)
    val timeKeeper by lazy { TimeKeeper(context) }
}
