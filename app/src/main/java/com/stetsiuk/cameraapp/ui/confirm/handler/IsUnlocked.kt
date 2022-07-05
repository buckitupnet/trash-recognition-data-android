package com.stetsiuk.cameraapp.ui.confirm.handler

import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.ui.confirm.DrawingView

class IsUnlocked(override var next: RectangleCheckHandler? = null) : RectangleCheckHandler() {
    override fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        return drawingView.isUnlocked
    }
}