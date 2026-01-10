package com.example.wakadp1

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint().apply { color = Color.parseColor("#002147") } // Navy Blue
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }
    private var data: List<Pair<String, Int>> = emptyList()

    fun setData(newData: List<Pair<String, Int>>) {
        data = newData
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val barWidth = width / (data.size * 2)
        val maxCount = data.maxOfOrNull { it.second } ?: 1
        val scale = (height - 100) / maxCount

        data.forEachIndexed { index, (label, value) ->
            val left = index * (barWidth * 2) + barWidth / 2
            val right = left + barWidth
            val bottom = height - 50
            val top = bottom - (value * scale)

            // Draw bar
            canvas.drawRect(left, top, right, bottom, barPaint)

            // Draw label
            canvas.drawText(label, (left + right) / 2, height - 10, textPaint)
            
            // Draw value
            canvas.drawText(value.toString(), (left + right) / 2, top - 10, textPaint)
        }
    }
}
