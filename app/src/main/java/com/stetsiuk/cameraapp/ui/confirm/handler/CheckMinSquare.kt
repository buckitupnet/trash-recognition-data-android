package com.stetsiuk.cameraapp.ui.confirm.handler

import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.ui.confirm.DrawingView

class CheckMinSquare(override var next: RectangleCheckHandler? = null) : RectangleCheckHandler() {
    override fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        if (rectangleArea.height() * rectangleArea.width() > DrawingView.MIN_SQUARE)
            return true
        return false
    }

}