package com.siabucketup.ecotaxi.model

data class RectangleArea(
    var leftUpX: Double,
    var leftUpY: Double,
    var rightDownX: Double,
    var rightDownY: Double,
    )
{
    fun paste(rectangleArea: RectangleArea) {
        leftUpX = rectangleArea.leftUpX
        leftUpY = rectangleArea.leftUpY
        rightDownY = rectangleArea.rightDownY
        rightDownX = rectangleArea.rightDownX
    }

    fun width(): Double = (rightDownX - leftUpX)

    fun height(): Double = (rightDownY - leftUpY)

    fun widthDivOn2(): Double = width() / 2

    fun heightDivOn2(): Double = height() / 2

    fun centerX(): Double = widthDivOn2() + leftUpX

    fun centerY(): Double = heightDivOn2() + leftUpY

    override fun toString(): String {
        return "Rectangle[$leftUpX, $leftUpY, $rightDownX, $rightDownY]"
    }
}