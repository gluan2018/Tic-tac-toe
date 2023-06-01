package br.com.yuki.makoto.jogodavelha.view.point

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@Suppress("MemberVisibilityCanBePrivate")
abstract class SignedPoint {
    protected val imagePaint: Paint = Paint()

    protected var directionY: Boolean = Random(System.currentTimeMillis()).nextBoolean()
    protected var directionX: Boolean = Random(System.currentTimeMillis()).nextBoolean()

    protected val progress: PointF = generateRandomPointer()

    protected val imageRectF: RectF = RectF()

    open fun placeholder(canvas: Canvas, rectF: RectF) = Unit

    open fun container(canvas: Canvas, rectF: RectF) = Unit

    open fun cursor(canvas: Canvas, pointF: PointF, paint: Paint) = Unit

    open fun update() {
        values(progress.x, directionX).apply {
            directionX = second
            progress.x = first
        }
        values(progress.y, directionY).apply {
            directionY = second
            progress.y = first
        }
    }

    open fun reset() {
        directionX = Random(System.currentTimeMillis()).nextBoolean()
        directionY = Random(System.currentTimeMillis()).nextBoolean()
        generateRandomPointer().apply {
            progress.x = this.x
            progress.y = this.y
        }
    }

    open fun isInArea(pointF: PointF): Boolean =
        (pointF.x >= imageRectF.left && pointF.x <= imageRectF.right) ||
        (pointF.y >= imageRectF.top && pointF.y <= imageRectF.bottom)

    protected fun applyRect(rectF: RectF, size: Float) {
        val left = progress.x.times(rectF.width() - size)
        val top = progress.y.times(rectF.height() - size) + rectF.top

        imageRectF.set(left, top, left + size, top + size)
    }

    private fun values(currentValue: Float, currentDirection: Boolean): Pair<Float, Boolean> {
        var point = currentValue
        if (currentDirection) {
            if (point < 1.0f) {
                point = min(1.0f, point + VELOCITY_MOVING)
                return point to true
            }
            point -= VELOCITY_MOVING
            return point to false
        }

        if (point > 0.0f) {
            point = max(0.0f, point - VELOCITY_MOVING)
            return point to false
        }
        point += VELOCITY_MOVING
        return point to true
    }

    abstract fun compare(other: SignedPoint): Boolean

    companion object {
        const val VELOCITY_MOVING = 0.05f
        @JvmStatic
        protected fun generateRandomPointer(): PointF =
            PointF(Random.nextDouble(0.0, 1.0).toFloat(), Random.nextDouble(0.0, 1.0).toFloat())
    }
}