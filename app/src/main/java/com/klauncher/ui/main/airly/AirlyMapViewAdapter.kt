package com.klauncher.ui.main.airly

import android.content.Context
import android.util.AttributeSet
import android.widget.AdapterView
import com.klauncher.model.MapSensor

class AirlyMapViewAdapter(context: Context, attrs: AttributeSet) : AdapterView<AirlyViewAdapter>(context, attrs) {

    var airlyViewAdapter: AirlyViewAdapter? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val x = 6
        var y = 200

        airlyViewAdapter?.let { airlyViewAdapter ->
            (0..airlyViewAdapter.count - 1)
                    .map {
                        Pair(
                                airlyViewAdapter.getItem(it) as MapSensor,
                                airlyViewAdapter.getView(it, null, this)
                        )
                    }
                    .forEachIndexed { index, (mapSensor, view) ->
                        with(view) {
                            measure(-1, -1)
                            layout(
                                    x,
                                    y,
                                    x + measuredWidth,
                                    y + measuredHeight
                            )
                            addViewInLayout(view, index, null, true)
                        }
                        y += 38
                    }
            invalidate()
        }
    }

    fun refresh() {
        removeAllViewsInLayout()
        requestLayout()
    }

    override fun getSelectedView() = null

    override fun setSelection(selection: Int) {}

    override fun getAdapter() = airlyViewAdapter

    override fun setAdapter(adapter: AirlyViewAdapter?) {
        airlyViewAdapter = adapter
    }
}