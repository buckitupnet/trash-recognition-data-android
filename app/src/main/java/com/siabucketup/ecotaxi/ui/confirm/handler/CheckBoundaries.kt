package com.siabucketup.ecotaxi.ui.confirm.handler

import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.ui.confirm.DrawingView

class CheckBoundaries(override var next: RectangleCheckHandler? = null) : RectangleCheckHandler() {
    override fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        return checkBoundaries(
            leftUpX = rectangleArea.leftUpX,
            leftUpY = rectangleArea.leftUpY,
            rightDownY = rectangleArea.rightDownY,
            rightDownX = rectangleArea.rightDownX,
            height = drawingView.height.toDouble(),
            width = drawingView.width.toDouble()
        )
    }

    private fun checkBoundaries(
        leftUpX: Double,
        leftUpY: Double,
        rightDownX: Double,
        rightDownY: Double,
        height: Double,
        width: Double,
    ): Boolean {
        if (leftUpX < DrawingView.BOUNDARIES || leftUpY < DrawingView.BOUNDARIES)
            return false

        if (rightDownX > width - DrawingView.BOUNDARIES || rightDownY > height - DrawingView.BOUNDARIES)
            return false

        return true
    }
}