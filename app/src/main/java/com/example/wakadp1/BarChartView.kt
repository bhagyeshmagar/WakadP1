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

    private val barPaint = Paint()
    private val textPaint = Paint()

    init {
        // Resolve colors dynamically from themes
        // Default to Navy Blue / Black if resource not found (safety)
        val barColor = try {
            context.getColor(R.color.police_navy_blue)
        } catch (e: Exception) {
            Color.parseColor("#002147")
        }
        
        val textColor = try {
            context.getColor(R.color.text_main)
        } catch (e: Exception) {
            Color.BLACK
        }

        barPaint.color = barColor
        
        textPaint.color = textColor
        textPaint.textSize = 30f
        textPaint.textAlign = Paint.Align.CENTER
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
