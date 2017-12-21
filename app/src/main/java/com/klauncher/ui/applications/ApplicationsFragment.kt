package com.klauncher.ui.applications

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.klauncher.R
import com.klauncher.extensions.bind
import com.klauncher.extensions.hide
import com.klauncher.extensions.show
import com.klauncher.model.Application
import com.klauncher.ui.applications.sortinggrid.SortingGrid
import io.reactivex.subjects.PublishSubject

class ApplicationsFragment : Fragment(), ApplicationsContract.View {

    private val presenter: ApplicationsContract.Presenter = ApplicationsPresenter(this)
    private val applicationsList = ArrayList<Application>()
    val closePublisher: PublishSubject<Any> = PublishSubject.create()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return LayoutInflater.from(context).inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadItems()
        with (grid) {
            adapter = applicationsAdapter
            setOnItemClickListener { _, _, position, _ -> handleOnClick(position) }
            setOnItemLongClickListener { _, _, _, _ ->
                showSorting()
                true
            }
        }

        sortingGrid.setOnItemClickListener { _, _, position, _ -> handleLetterClick(position) }
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
        presenter.openApplication(application)
        closePublisher.onNext(0)
        refreshAdapter(applicationsList)
    }

    override fun setItems(items: List<Application>) {
        applicationsList.addAll(items)
        refreshAdapter(applicationsList)
    }

    private fun refreshAdapter(items: List<Application>) {
        with(applicationsAdapter) {
            clear()
            addAll(items)
            notifyDataSetChanged()
        }
    }

    val applicationsAdapter: ApplicationsAdapter by lazy {
        ApplicationsAdapter(context)
    }

    val grid: GridView by bind(R.id.grid_view)
    val sortingGrid: SortingGrid by bind(R.id.sorting_view)
}