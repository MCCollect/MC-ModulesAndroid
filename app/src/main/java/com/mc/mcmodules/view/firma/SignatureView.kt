package com.mc.mcmodules.view.firma

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mc.mcmodules.view.firma.FirmaFragment.Companion.TOUCH_TOLERANCE
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs

class SignatureView: View {
    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null

    private val pathMap: HashMap<Int, Path> by lazy { HashMap() }
    private val paintLine: Paint by lazy { Paint() }
    private val paintScreen: Paint by lazy { Paint() }
    private val previousPointMap: HashMap<Int, Point> by lazy { HashMap() }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        init()
    }

    private fun init() {
        paintLine.isAntiAlias = true
        paintLine.color = Color.BLUE
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 7f
        paintLine.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap!!)
        bitmap!!.eraseColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap!!, 0f, 0f, paintScreen)
        for (key in pathMap.keys) {
            canvas.drawPath(pathMap[key]!!, paintLine)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked //event type;
        val actionIndex = event.actionIndex //pointer (finger ,mouse..)
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
            touchStarted(
                event.getX(actionIndex),
                event.getY(actionIndex),
                event.getPointerId(actionIndex)
            )
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex))
        } else {
            touchMoved(event)
        }
        invalidate() //redraws the screen
        return true
    }

    private fun touchMoved(event: MotionEvent) {
        for (i in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(i)
            val pointerIndex = event.findPointerIndex(pointerId)
            if (pathMap.containsKey(pointerId)) {
                val newX = event.getX(pointerIndex)
                val newY = event.getY(pointerIndex)
                val path = pathMap[pointerId]
                val point = previousPointMap[pointerId]

                //Calculate how far the user moved from the last point
                val deltaX = abs(newX - point!!.x)
                val deltaY = abs(newY - point.y)

                //if the distance is significant enough to be considered as a movement , then..
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {

                    //move the path to the new location
                    path!!.quadTo(
                        point.x.toFloat(),
                        point.y.toFloat(),
                        (newX + point.x) / 2,
                        (newY + point.y) / 2
                    )

                    //store the new coordinates
                    point.x = newX.toInt()
                    point.y = newY.toInt()
                }
            }
        }
    }

    fun setDrawingColor(color: Int) { paintLine.color = color }

    fun setLineWidth(width: Int) { paintLine.strokeWidth = width.toFloat() }

    fun clear(background: Int) {
        pathMap.clear()
        previousPointMap.clear()
        bitmap!!.eraseColor(background)
        invalidate()
    }

    private fun touchEnded(pointerId: Int) {
        val path = pathMap[pointerId] //Get the corresponding path
        bitmapCanvas!!.drawPath(path!!, paintLine) //Draw to bitmapCanvas
        path.reset()
    }

    private fun touchStarted(x: Float, y: Float, pointerId: Int) {
        val path: Path?
        val point: Point?
        if (pathMap.containsKey(pointerId)) {
            path = pathMap[pointerId]
            point = previousPointMap[pointerId]
        } else {
            path = Path()
            pathMap[pointerId] = path
            point = Point()
            previousPointMap[pointerId] = point
        }
        path!!.moveTo(x, y)
        point!!.x = x.toInt()
        point.y = y.toInt()
    }

    fun saveImage(path: String): String {
        val lastIndex = path.lastIndexOf("/")
        val pathbase = path.substring(0,lastIndex)
        val file = File(path)
        File(pathbase).mkdirs()
        FileOutputStream(file).use { fos ->
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        return file.absolutePath
    }
}