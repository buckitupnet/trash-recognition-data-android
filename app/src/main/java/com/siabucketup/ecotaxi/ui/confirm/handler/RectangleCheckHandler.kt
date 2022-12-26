package com.siabucketup.ecotaxi.ui.confirm.handler

import com.siabucketup.ecotaxi.model.RectangleArea
import com.siabucketup.ecotaxi.ui.confirm.DrawingView

abstract class RectangleCheckHandler {
    abstract var next: RectangleCheckHandler?

    fun handle(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean {
        return if (handler(drawingView, rectangleArea))
            next?.handle(drawingView, rectangleArea) ?: true
        else
            false
    }

    protected abstract fun handler(drawingView: DrawingView, rectangleArea: RectangleArea): Boolean
}