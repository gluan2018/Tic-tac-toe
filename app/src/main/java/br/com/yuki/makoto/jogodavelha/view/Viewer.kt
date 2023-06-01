package br.com.yuki.makoto.jogodavelha.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import br.com.yuki.makoto.jogodavelha.view.controller.GameAreaController

class Viewer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context, attrs, defStyleAttr, defStyleRes
) {
    private val areaController: GameAreaController = GameAreaController()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed)
            resizeArea()
    }

    private fun resizeArea() {
        areaController.onSizeChange(height, width, fullSize = true)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        areaController.draw(canvas)
    }
}