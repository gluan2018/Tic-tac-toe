package br.com.yuki.makoto.jogodavelha.util

import android.graphics.PointF
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import kotlin.math.abs

class GameController(
    private val onPressed: (controller: GameController, deviceId: Int, point: PointF, isPressed: Boolean) -> Unit
) {
    private val controllers: MutableMap<Int, PointF> = mutableMapOf()

    var isPressed: Boolean = false
        private set

    fun pointToDevice(device: Int): PointF? = controllers[device]

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                pointToDevice(event.deviceId)?.apply {
                    onPressed(this@GameController, event.deviceId,this, true)
                    isPressed = true
                }
            }
        }
        return false
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                pointToDevice(event.deviceId)?.apply {
                    onPressed(this@GameController, event.deviceId, this, false)
                    isPressed = false
                }
            }
        }
        return false
    }

    fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
            && event.action == MotionEvent.ACTION_MOVE
        ) {
            (0 until event.historySize).forEach { position ->
                processJoystickInput(event, position)
            }
            processJoystickInput(event, -1)
            return true
        }
        return false
    }

    private fun getCenteredAxis(
        event: MotionEvent,
        device: InputDevice,
        axis: Int,
        historyPos: Int
    ): Float {
        device.getMotionRange(axis, event.source)?.apply {
            val value = if (historyPos < 0) event.getAxisValue(axis) else event.getHistoricalAxisValue(axis, historyPos)
            if (abs(value) > flat)
                return value
        }
        return 0f
    }

    private fun processJoystickInput(event: MotionEvent, historyPos: Int) {
        val inputDevice = event.device

        var x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos)
        if (x == 0f)
            x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos)
        if (x == 0f)
            x = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos)

        var y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos)
        if (y == 0f)
            y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos)
        if (y == 0f)
            y = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos)

        updateValue(inputDevice.id, x, y)
    }

    private fun updateValue(device: Int, positionX: Float, positionY: Float) {
        if (abs(positionX) < 0.05f && abs(positionY) < 0.05f)
            return
        if (!controllers.contains(device))
            controllers[device] = PointF()
        controllers.getValue(device).apply {
            set(x + positionX * VELOCITY, y + positionY * VELOCITY)
        }
    }
    companion object {
        const val VELOCITY = 20f
        @JvmStatic
        fun getGamesControllers(): List<InputDevice> {
            return InputDevice.getDeviceIds()
                .distinct()
                .mapNotNull(InputDevice::getDevice)
                .filter { inputDevice ->
                    inputDevice.sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD ||
                            inputDevice.sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
                }
        }
    }
}