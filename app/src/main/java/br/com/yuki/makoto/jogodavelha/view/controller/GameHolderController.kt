package br.com.yuki.makoto.jogodavelha.view.controller

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import br.com.yuki.makoto.jogodavelha.R
import br.com.yuki.makoto.jogodavelha.view.point.CirclePoint
import br.com.yuki.makoto.jogodavelha.view.point.SignedPoint
import br.com.yuki.makoto.jogodavelha.view.point.XPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameHolderController(
    context: Context,
    private val updateValues: () -> Unit
) {
    private val values: MutableList<SignedPoint>
    private val selectedValues: MutableList<SignedPoint> = mutableListOf()

    private val imageArea: RectF = RectF()

    private val holderScope = CoroutineScope(Dispatchers.Main.immediate + Job())

    init {
        val circle = ContextCompat.getDrawable(context, R.drawable.circle)!!.toBitmap(width = 300, height = 300)
        val x = ContextCompat.getDrawable(context, R.drawable.x)!!.toBitmap(width = 300, height = 300)

        values = MutableList(5) { CirclePoint(circle.copy(circle.config, true)) }
        values.addAll(List(5) { XPoint(x.copy(x.config, true)) })

        values.shuffle()

        holderScope.launch {
            while (true) {
                delay(1000 / 60)
                values.iterator()
                    .forEach { signedPoint ->
                        signedPoint.update()
                    }
                updateValues()
            }
        }
    }

    fun onSizeChange(rectF: RectF) {
        imageArea.set(rectF)
    }

    fun get(pointF: PointF): SignedPoint? {
        val index = values.indexOfFirst { signedPoint -> signedPoint.isInArea(pointF) }
        if (index != -1) {
            val signedPoint = values.removeAt(index)
            selectedValues.add(signedPoint)

            return signedPoint
        }
        return null
    }

    fun reset(signedPoint: SignedPoint) {
        selectedValues.remove(signedPoint)
        values.add(signedPoint)
        values.forEach(SignedPoint::reset)
    }

    fun reset() {
        values.addAll(selectedValues)
        selectedValues.clear()
    }

    fun draw(canvas: Canvas) {
        values.iterator()
            .forEachRemaining { signedPoint ->
                signedPoint.placeholder(canvas, imageArea)
            }
    }
}