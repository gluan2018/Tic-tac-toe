package br.com.yuki.makoto.jogodavelha.view.controller

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.util.SizeF
import br.com.yuki.makoto.jogodavelha.view.point.SignedPoint
import kotlin.math.min

class GameAreaController {
    private val lineMatrix = Matrix()
    private val linePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }
    private val linePath = Path().apply {
        moveTo(0f, 2f); lineTo(6f, 2f)
        moveTo(0f, 4f); lineTo(6f, 4f)

        moveTo(2f, 0f); lineTo(2f, 6f)
        moveTo(4f, 0f); lineTo(4f, 6f)
    }

    private val area: RectF = RectF()

    private var sizeZone: SizeF = SizeF(0f, 0f)
    private val zones = arrayOf(
        arrayOf<SignedPoint>(NonePoint, NonePoint, NonePoint),
        arrayOf<SignedPoint>(NonePoint, NonePoint, NonePoint),
        arrayOf<SignedPoint>(NonePoint, NonePoint, NonePoint),
    )

    private val imageArea: RectF = RectF()

    var currentWinner: SignedPoint? = null
        private set

    val isFinished: Boolean
        get()  {
            if (currentWinner != null)
                return true
            for (y in zones.indices) {
                for (x in zones[y].indices) {
                    if (zones[y][x] is NonePoint)
                        return false
                }
            }
            return true
        }

    fun reset() {
        currentWinner = null
        for (y in zones.indices) {
            for (x in zones[y].indices) {
                zones[y][x] = NonePoint
            }
        }
    }

    fun dropIn(pointF: PointF, pointer: SignedPoint) : Boolean {
        if (pointF.x < area.left || pointF.x > area.right || pointF.y < area.top || pointF.y > area.bottom)
            return false

        val zone = findZone(pointF)

        return when {
            isFinished -> false
            zone.first >= 3 || zone.second >= 3 || zone.first <= -1 || zone.second <= -1 -> false
            zones[zone.first][zone.second] !is NonePoint -> false
            else -> {
                zones[zone.first][zone.second] = pointer
                currentWinner = pointer.takeIf { checkIsWon(zone.first, zone.second, pointer) }
                true
            }
        }
    }

    fun onSizeChange(height: Int, width: Int, fullSize: Boolean = false) {
        val squareValue = min(height, width)
        val size = squareValue * (if (fullSize) 1.0f else 0.6f)
        val margin = squareValue * (if (fullSize) 0.0f else 0.2f)

        lineMatrix.reset()
        lineMatrix.setScale(size / 6f, size / 6f)
        lineMatrix.postTranslate(margin, margin)
        linePath.transform(lineMatrix)

        area.set(margin, margin, margin + size, margin + size)
        sizeZone = SizeF(size / 3f, size / 3f)
    }

    fun getArea(): RectF {
        return RectF(0f, 0f, area.right, area.bottom + area.top)
    }

    fun draw(canvas: Canvas) {
        canvas.drawPath(linePath, linePaint)
        for (y in zones.indices) {
            imageArea.top = y * sizeZone.height + area.top
            for (x in zones[y].indices) {
                imageArea.left = x * sizeZone.width + area.left
                imageArea.right = imageArea.left + sizeZone.width
                imageArea.bottom = imageArea.top + sizeZone.height

                zones[y][x].container(canvas, imageArea)
            }
        }
    }

    private fun findZone(pointF: PointF): Pair<Int, Int> {
        val positionX = ((pointF.x - area.left) / area.width() * 100f).toInt() / 33
        val positionY = ((pointF.y - area.top) / area.height() * 100f).toInt() / 33

        return positionY to positionX
    }

    private fun checkIsWon(x: Int, y: Int, type: SignedPoint): Boolean {
        for (line in 0 until 3) {
            if (!type.compare(zones[x][line]))
                break
            if (line == 2)
                return true
        }

        for (column in 0 until 3) {
            if (!type.compare(zones[column][y]) )
                break
            if (column == 2)
                return true
        }

        for (position in 0 until 3) {
            if (!type.compare(zones[position][position]) )
                break
            if (position == 2)
                return true
        }

        for (position in 0 until 3) {
            Log.i("Position", "Position: $position x ${2 - position}")
            if (!type.compare(zones[position][2 - position]) )
                break
            if (position == 2)
                return true
        }

        return false
    }

    companion object {
        private object NonePoint : SignedPoint() {
            override fun compare(other: SignedPoint): Boolean {
                return other is NonePoint
            }
        }
    }
}