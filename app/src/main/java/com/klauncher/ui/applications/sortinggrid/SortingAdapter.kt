package com.klauncher.ui.applications.sortinggrid

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.klauncher.R
import com.klauncher.databinding.AdapterSortingGridBinding
import com.klauncher.model.SortItem

class SortingAdapter(context: Context?) : ArrayAdapter<SortItem>(context, R.layout.adapter_sorting_grid) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: AdapterSortingGridBinding =
                when(convertView) {
                    null -> DataBindingUtil.inflate(inflater, R.layout.adapter_sorting_grid, parent, false)
                    else -> DataBindingUtil.getBinding(convertView)!!
                }
        with (binding) {
            item = getItem(position)
            return root
        }
    }

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
}