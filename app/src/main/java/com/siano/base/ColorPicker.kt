package com.siano.base

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ColorPicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ImageView(context, attrs, defStyle) {

    private val colorSubject = BehaviorSubject.createDefault("#000000")

    fun colorChanges(): Observable<String> = colorSubject

    override fun onTouchEvent(event: MotionEvent): Boolean =
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {

            val bitmap = (this.drawable as BitmapDrawable).bitmap

            val x = event.x.toInt()
            val y = event.y.toInt()

            if (x > 0 && x < bitmap.width && y > 0 && y < bitmap.height) {
                val pixel = bitmap.getPixel(x, y)

                val hex = "#" + Integer.toHexString(pixel).substring(2)
                colorSubject.onNext(hex)
            }
            true
        } else super.onTouchEvent(event)
}