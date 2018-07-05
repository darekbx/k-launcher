package com.klauncher.ui.applications

import android.content.Context
import com.klauncher.model.Application

interface ApplicationsContract {

    interface View {

        fun setItems(items: List<Application>)
        fun setDistinctLetters(letters: HashSet<Char>)
        fun getContext(): Context
        fun hideSorting()
    }

    interface Presenter {

        fun loadItems()
        fun increaseClickCount(application: Application)
        fun openApplication(application: Application)
    }
}