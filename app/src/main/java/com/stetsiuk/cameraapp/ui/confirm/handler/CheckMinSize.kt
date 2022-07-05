package com.stetsiuk.cameraapp.ui.confirm.handler

import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.ui.confirm.DrawingView

class CheckMinSize(override var next: RectangleCheckHandler? = null) : RectangleCheckHandler() {
    override fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        if (rectangleArea.rightDownX < rectangleArea.leftUpX + DrawingView.MIN_LENGTH
            ||
            rectangleArea.rightDownY < rectangleArea.leftUpY + DrawingView.MIN_LENGTH
        ) {
            return false
        }
        return true
    }
}