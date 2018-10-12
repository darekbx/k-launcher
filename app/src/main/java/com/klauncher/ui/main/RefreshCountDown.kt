package com.klauncher.ui.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RefreshCountDown(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        strokeWidth = 4f
    }
    private var animator: ValueAnimator? = null
    private var progressValue = 0F
    var reloadDelay: Long = 0

    fun start() {
        animator = ValueAnimator
                .ofFloat(0f, height.toFloat())
                .apply {
                    duration = reloadDelay
                    addListener(animatorListener)
                    addUpdateListener { value ->
                        progressValue = value.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
    }

    private val animatorListener = object: Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            progressValue = 0F
            invalidate()
        }
    }

    fun stop() {
        progressValue = 0F
        animator?.cancel()
        animator = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        animator?.let {
            canvas?.run {

                drawLine(0F, height.toFloat(), 0F, height.toFloat() - progressValue, paint)

            }
        }
    }
}