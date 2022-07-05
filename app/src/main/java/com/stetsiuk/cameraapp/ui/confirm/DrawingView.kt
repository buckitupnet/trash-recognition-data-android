package com.stetsiuk.cameraapp.ui.confirm

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.doOnPreDraw
import com.stetsiuk.cameraapp.R
import com.stetsiuk.cameraapp.model.Point
import com.stetsiuk.cameraapp.model.RectangleArea
import com.stetsiuk.cameraapp.ui.confirm.handler.*

class DrawingView : View {
    private var bitmap: Bitmap? = null
    private lateinit var rect: Rect

    private var previousPoint: Point = Point(0.0, 0.0)
    private var chosenShape = Shape.NOTHING
    private var _isUnlocked = true
    val isUnlocked get() = _isUnlocked

    private lateinit var rectanglePaint: Paint
    private lateinit var circlePaint: Paint
    private lateinit var textPaint: TextPaint
    private lateinit var textBackgroundPaint: Paint
    private lateinit var checker: RectangleCheckHandler
    private val semiTransparent = ContextCompat.getColor(context, R.color.semi_transparent)
    private lateinit var rectangleArea: RectangleArea
    val getRectangleArea get() = rectangleArea

    fun lockMovingRectangle(){
        _isUnlocked = false
    }

    fun onStartPutRectangleIn(x: Double, y: Double) {
        this.doOnPreDraw {
            putRectangleIn(x, y)
        }
    }

    constructor(ct: Context) : super(ct) {
        init(ct)
    }

    constructor(ct: Context, attrs: AttributeSet?) : super(ct, attrs) {
        init(ct)
    }

    constructor(ct: Context, attrs: AttributeSet?, defStyle: Int) : super(ct, attrs, defStyle) {
        init(ct)
    }

    private fun init(ct: Context) {
        bitmap = BitmapFactory.decodeResource(ct.resources, R.drawable.ic_baseline_cancel_24)
        rectanglePaint = Paint()
        rectanglePaint.color = POLYGON_COLOR
        rectanglePaint.strokeWidth = STROKE_WIDTH
        rectanglePaint.style = Paint.Style.STROKE

        rectangleArea = RectangleArea(
            STARTING_POINT_UP,
            STARTING_POINT_UP,
            STARTING_POINT_DOWN,
            STARTING_POINT_DOWN
        )

        circlePaint = Paint()
        circlePaint.color = POLYGON_COLOR
        circlePaint.strokeWidth = STROKE_WIDTH
        circlePaint.style = Paint.Style.FILL

        textPaint = TextPaint()
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE

        textBackgroundPaint = Paint()
        textBackgroundPaint.color = semiTransparent
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint

        val isUnlocked = IsUnlocked()
        val checkBoundaries = CheckBoundaries(isUnlocked)
        val checkSize = CheckMinSize(checkBoundaries)
        val checkSquare = CheckMinSquare(checkSize)
        checker = checkSquare
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        bitmap?.let {
            canvas.drawBitmap(it, null, rect, null)
        }

        if (this::rectangleArea.isInitialized) {
            with(canvas) {
                drawRect(
                    rectangleArea.leftUpX.toFloat(),
                    rectangleArea.leftUpY.toFloat(),
                    rectangleArea.rightDownX.toFloat(),
                    rectangleArea.rightDownY.toFloat(),
                    rectanglePaint
                )

                drawCircle(
                    rectangleArea.rightDownX.toFloat(),
                    rectangleArea.rightDownY.toFloat(),
                    RADIUS.toFloat(),
                    circlePaint
                )

                if (tag.toString().isNotEmpty()) {
                    val rectangle = calculateTextRectangle(tag.toString())
                    drawRoundRect(
                        rectangle.left.toFloat(),
                        rectangle.top.toFloat(),
                        rectangle.right.toFloat(),
                        rectangle.bottom.toFloat(),
                        ROUND, ROUND, textBackgroundPaint
                    )
                    drawText(
                        tag.toString(),
                        rectangle.left.toFloat() + PADDING_START,
                        rectangle.bottom.toFloat() - PADDING_BOTTOM,
                        textPaint
                    )
                }
            }
        }
        super.onDraw(canvas)
    }

    private fun calculateTextRectangle(text: String): Rect {
        val halfTextLength = textPaint.measureText(text) + 15
        return Rect(
            rectangleArea.leftUpX.toInt(),
            (rectangleArea.rightDownY + DIST_FROM_RECTANGLE_TO_BACK).toInt(),
            (rectangleArea.leftUpX + halfTextLength).toInt(),
            (rectangleArea.rightDownY + BACK_HEIGHT + DIST_FROM_RECTANGLE_TO_BACK).toInt()
        )
    }

    private fun putRectangleIn(x: Double, y: Double) {
        val widthDivOn2 = rectangleArea.widthDivOn2()
        val heightDivOn2 = rectangleArea.heightDivOn2()
        val preRectangle = RectangleArea(
            x - widthDivOn2,
            y - heightDivOn2,
            x + widthDivOn2,
            y + heightDivOn2
        )
        val xCoordinates =
            adjustBoundaries(preRectangle.leftUpX, preRectangle.rightDownX, this.width)
        preRectangle.leftUpX = xCoordinates[0]
        preRectangle.rightDownX = xCoordinates[1]
        val yCoordinates = adjustBoundaries(preRectangle.leftUpY, preRectangle.rightDownY, this.height)
        preRectangle.leftUpY = yCoordinates[0]
        preRectangle.rightDownY = yCoordinates[1]

        rectangleArea = preRectangle
        invalidate()
    }

    private fun adjustBoundaries(left: Double, right: Double, rightLimit: Int): List<Double> {
        if (left < BOUNDARIES) {
            val dif = BOUNDARIES - left
            return listOf(left + dif, right + dif)
        } else if (right > rightLimit - BOUNDARIES) {
            val dif = right - rightLimit + BOUNDARIES
            return listOf(left - dif, right - dif)
        }
        return listOf(left, right)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val xTouch: Double
        val yTouch: Double
        var handled = false
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                xTouch = event.x.toDouble()
                yTouch = event.y.toDouble()

                chosenShape = detectShape(xTouch, yTouch)
                previousPoint = Point(xTouch, yTouch)

                invalidate()
                handled = true
            }
            MotionEvent.ACTION_MOVE -> {
                xTouch = event.x.toDouble()
                yTouch = event.y.toDouble()
                val xDif = xTouch - previousPoint.x
                val yDif = yTouch - previousPoint.y

                when (chosenShape) {
                    Shape.RECTANGLE -> move { rectangleArea.moveRectangle(xDif, yDif) }
                    Shape.RIGHT_DOWN_POINT -> move { rectangleArea.moveRightDownPoint(xDif, yDif) }
                    Shape.LEFT_UP_POINT -> move { rectangleArea.moveLeftUpPoint(xDif, yDif) }
                    Shape.LEFT_DOWN_POINT -> move { rectangleArea.moveLeftDownPoint(xDif, yDif) }
                    Shape.RIGHT_UP_POINT -> move { rectangleArea.moveRightUpPoint(xDif, yDif) }
                    Shape.NOTHING -> {}
                }
                previousPoint = Point(xTouch, yTouch)

                invalidate()
                handled = true
            }

            MotionEvent.ACTION_POINTER_UP -> {
                previousPoint = Point(0.0, 0.0)
            }
            MotionEvent.ACTION_CANCEL -> {
                handled = true
            }
        }
        return super.onTouchEvent(event) || handled
    }

    private fun move(block: () -> RectangleArea) {
        val newRectangle = block.invoke()
        val boolean = isValidRectangle(newRectangle)
        if (boolean) {
            rectangleArea.paste(newRectangle)
        }
    }

    /*
    * Checking what shape user clicked
    * */
    private fun detectShape(xTouch: Double, yTouch: Double): Shape {
        if (isTouchingPoint(xTouch, yTouch, rectangleArea.rightDownX, rectangleArea.rightDownY)) {
            return Shape.RIGHT_DOWN_POINT
        }
        if (isTouchingPoint(xTouch, yTouch, rectangleArea.leftUpX, rectangleArea.leftUpY)) {
            return Shape.LEFT_UP_POINT
        }
        if (isTouchingPoint(xTouch, yTouch, rectangleArea.leftUpX, rectangleArea.rightDownY)) {
            return Shape.LEFT_DOWN_POINT
        }
        if (isTouchingPoint(xTouch, yTouch, rectangleArea.rightDownX, rectangleArea.leftUpY)) {
            return Shape.RIGHT_UP_POINT
        }
        if (isTouchingRectangle(xTouch, yTouch))
            return Shape.RECTANGLE
        return Shape.NOTHING
    }

    private fun isValidRectangle(
        rectangleArea: RectangleArea
    ): Boolean = isValidRectangle(
        rectangleArea.leftUpX,
        rectangleArea.leftUpY,
        rectangleArea.rightDownX,
        rectangleArea.rightDownY
    )

    //enter "new" Rectangle to see is it right to change it
    private fun isValidRectangle(
        leftUpX: Double,
        leftUpY: Double,
        rightDownX: Double,
        rightDownY: Double
    ): Boolean {
        return checker.handle(this, RectangleArea(leftUpX, leftUpY, rightDownX, rightDownY))
    }

    companion object {
        const val MIN_SQUARE = 16384
        const val MIN_LENGTH = 12
        const val BOUNDARIES = 8
        private const val RADIUS = 12
        private const val RADIUS_MULTIPLAYER = 32
        private const val DIST_FROM_RECTANGLE_TO_BACK = 5F
        private const val PADDING_START = 7F
        private const val PADDING_BOTTOM = 10F
        private const val TEXT_SIZE = 40F
        private const val BACK_HEIGHT = 60F
        private const val STROKE_WIDTH = 6F
        private val POLYGON_COLOR = "#d50000".toColorInt()
        private const val TEXT_COLOR = Color.WHITE
        private const val STARTING_POINT_UP = 300.0
        private const val STARTING_POINT_DOWN = 500.0
        private const val ROUND = 15f
    }

    enum class Shape {
        LEFT_UP_POINT, LEFT_DOWN_POINT, RIGHT_UP_POINT, RIGHT_DOWN_POINT, RECTANGLE, NOTHING
    }

    private fun isTouchingRectangle(xTouch: Double, yTouch: Double): Boolean {
        return (xTouch < rectangleArea.rightDownX && xTouch > rectangleArea.leftUpX
                &&
                yTouch < rectangleArea.rightDownY && yTouch > rectangleArea.leftUpY)
    }

    private fun isTouchingPoint(
        xTouch: Double,
        yTouch: Double,
        xPoint: Double,
        yPoint: Double
    ): Boolean {
        val radius = RADIUS * RADIUS * RADIUS_MULTIPLAYER
        return ((xPoint - xTouch) * (xPoint - xTouch)
                + (yPoint - yTouch) * (yPoint - yTouch)
                <= radius)
    }

    private fun RectangleArea.moveRectangle(
        xDif: Double,
        yDif: Double
    ): RectangleArea {
        return this.copy(
            leftUpX = leftUpX + xDif,
            leftUpY = leftUpY + yDif,
            rightDownX = rightDownX + xDif,
            rightDownY = rightDownY + yDif
        )
    }

    private fun RectangleArea.moveRightDownPoint(
        xDif: Double,
        yDif: Double
    ): RectangleArea {
        return this.copy(rightDownX = rightDownX + xDif, rightDownY = rightDownY + yDif)
    }

    private fun RectangleArea.moveLeftUpPoint(
        xDif: Double,
        yDif: Double
    ): RectangleArea {
        return this.copy(leftUpX = leftUpX + xDif, leftUpY = leftUpY + yDif)
    }

    private fun RectangleArea.moveLeftDownPoint(
        xDif: Double,
        yDif: Double
    ): RectangleArea {
        return this.copy(leftUpX = leftUpX + xDif, rightDownY = rightDownY + yDif)
    }

    private fun RectangleArea.moveRightUpPoint(
        xDif: Double,
        yDif: Double
    ): RectangleArea {
        return this.copy(rightDownX = rightDownX + xDif, leftUpY = leftUpY + yDif)
    }
}