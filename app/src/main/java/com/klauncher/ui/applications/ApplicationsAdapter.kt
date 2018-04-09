package com.klauncher.ui.applications

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.klauncher.R
import com.klauncher.databinding.AdapterApplicationsBinding
import com.klauncher.model.Application

class ApplicationsAdapter(context: Context) : ArrayAdapter<Application>(context, R.layout.adapter_applications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: AdapterApplicationsBinding = when (convertView) {
            null -> DataBindingUtil.inflate(inflater, R.layout.adapter_applications, parent, false)
            else -> DataBindingUtil.getBinding(convertView)!!
        }
        with(binding) {
            application = getItem(position)
            return root
        }
    }

    val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }
}