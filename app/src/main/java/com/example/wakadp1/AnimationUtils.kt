package com.example.wakadp1

import android.animation.ValueAnimator
import android.widget.TextView

object AnimationUtils {
    fun animateCount(textView: TextView, initialValue: Int, finalValue: Int) {
        val animator = ValueAnimator.ofInt(initialValue, finalValue)
        animator.duration = 1500 // 1.5 seconds
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
    }
}
