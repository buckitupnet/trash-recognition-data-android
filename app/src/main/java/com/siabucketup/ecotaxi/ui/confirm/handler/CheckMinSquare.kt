package com.siabucketup.ecotaxi.ui.confirm.handler

import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.ui.confirm.DrawingView

class CheckMinSquare(override var next: RectangleCheckHandler? = null) : RectangleCheckHandler() {
    override fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        if (rectangleArea.height() * rectangleArea.width() > DrawingView.MIN_SQUARE)
            return true
        return false
    }

}