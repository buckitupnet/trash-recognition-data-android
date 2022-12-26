package com.siabucketup.ecotaxi.ui.confirm.handler

import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.ui.confirm.DrawingView

class IsUnlocked(override var next: RectangleCheckHandler? = null) : RectangleCheckHandler() {
    override fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        return drawingView.isUnlocked
    }
}