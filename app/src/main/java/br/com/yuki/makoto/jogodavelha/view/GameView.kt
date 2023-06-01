package br.com.yuki.makoto.jogodavelha.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import br.com.yuki.makoto.jogodavelha.R
import br.com.yuki.makoto.jogodavelha.util.GameController
import br.com.yuki.makoto.jogodavelha.view.controller.GameAreaController
import br.com.yuki.makoto.jogodavelha.view.controller.GameHolderController
import br.com.yuki.makoto.jogodavelha.view.point.SignedPoint

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context, attrs, defStyleAttr, defStyleRes
) {
    private val selectedPoints: MutableMap<Int, SignedPoint> = mutableMapOf()

    private val areaController: GameAreaController = GameAreaController()
    private val holderController: GameHolderController = GameHolderController(context = context) {
        invalidate()
    }

    private val devices: List<InputDevice> = GameController.getGamesControllers()
    private val controller: GameController = GameController { controller, deviceId, point, isPressed ->
        if (isPressed) {
            if (selectedPoints.containsKey(deviceId))
                return@GameController
            val signedPoint = holderController.get(point) ?: return@GameController
            selectedPoints[deviceId] = signedPoint
        } else {
            val signedPoint = selectedPoints[deviceId] ?: return@GameController
            val pointDevice = controller.pointToDevice(deviceId) ?: return@GameController

            val dropIn = areaController.dropIn(pointDevice, signedPoint)
            if (!dropIn)
                holderController.reset(signedPoint)
            selectedPoints.remove(deviceId)
        }

        if (areaController.isFinished) {
            onGameFinished?.invoke(areaController.currentWinner)
        }
    }

    private val cursor: Bitmap =
        ContextCompat.getDrawable(context, R.drawable.circle_complete)!!.toBitmap(100, 100)
    private val paints = paints()

    private var onGameFinished: ((SignedPoint?) -> Unit)? = null

    init {
        isClickable = true
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (controller.onGenericMotionEvent(event))
            invalidate()
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (controller.onKeyDown(keyCode, event))
            invalidate()
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (controller.onKeyUp(keyCode, event))
            invalidate()
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed)
            resizeArea()
    }

    private fun resizeArea() {
        areaController.onSizeChange(height, width)
        val area = areaController.getArea()

        holderController.onSizeChange(
            RectF(0f, area.bottom, width.toFloat(), height.toFloat())
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        areaController.draw(canvas)
        holderController.draw(canvas)

        devices.forEachIndexed { index, device ->
            val pointer = controller.pointToDevice(device.id) ?: return@forEachIndexed
            selectedPoints[device.id]?.cursor(canvas, pointer, paints[index]) ?: canvas.drawBitmap(cursor, pointer.x, pointer.y, paints[index])
        }
    }

    fun reset() {
        areaController.reset()
        holderController.reset()
    }

    fun setOnGameFinishedListener(listener: (SignedPoint?) -> Unit) {
        this.onGameFinished = listener
    }

    companion object {
        @JvmStatic
        fun colors() : List<Int> = listOf(Color.GREEN, Color.RED)
        fun paints(): List<Paint> = colors().map { color ->
            Paint().apply {
                colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
    }
}