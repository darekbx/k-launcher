package com.klauncher.ui.applications.sortinggrid

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView
import com.klauncher.model.SortItem

class SortingGrid(context: Context?, attrs: AttributeSet?) : GridView(context, attrs) {

    fun fillUsedLetters(letters: HashSet<Char>) {
        with(SortingAdapter(context)) {
            for (letter in 'A'..'Z') {
                val isUsed = letters.contains(letter)
                add(SortItem(letter.toString(), isUsed))
            }
            adapter = this
        }
    }

    fun charByPosition(position: Int): Char {
        val item: SortItem = adapter.getItem(position) as SortItem
        return item.letter[0]
    }
}