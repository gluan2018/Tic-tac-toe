package br.com.yuki.makoto.jogodavelha.view.point

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF

class CirclePoint(
    private val circle: Bitmap
) : SignedPoint() {
    private val smallCircle = Bitmap.createScaledBitmap(circle, 100, 100, false)
    init {
        imagePaint.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
    }
    override fun placeholder(canvas: Canvas, rectF: RectF) {
        super.applyRect(rectF = rectF, size = circle.height.toFloat())
        canvas.drawBitmap(circle, null, imageRectF, imagePaint)
    }
    override fun container(canvas: Canvas, rectF: RectF) {
        canvas.drawBitmap(circle, null, rectF, imagePaint)
    }
    override fun cursor(canvas: Canvas, pointF: PointF, paint: Paint) {
        canvas.drawBitmap(smallCircle, pointF.x, pointF.y, paint)
    }
    override fun compare(other: SignedPoint): Boolean {
        return other is CirclePoint
    }
}