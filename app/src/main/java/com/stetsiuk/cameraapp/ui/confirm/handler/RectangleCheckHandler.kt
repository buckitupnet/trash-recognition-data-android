package com.stetsiuk.cameraapp.ui.confirm.handler

import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.ui.confirm.DrawingView

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