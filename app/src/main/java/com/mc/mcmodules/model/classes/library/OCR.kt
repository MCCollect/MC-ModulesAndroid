package com.mc.mcmodules.model.classes.library

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseArray
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.util.*
import java.util.regex.Pattern


class OCR(
    private var context: Context
) {
    private var textRecognizer = TextRecognizer.Builder(context).build()
    private var isOperational = textRecognizer.isOperational
    private var textOfImage = "No operational Device (MC collect)"


    fun loadBitmap(bitmap: Bitmap) {

        if (isOperational) {

            val frame: Frame = Frame.Builder().setBitmap(bitmap).build()
            val items: SparseArray<*> = textRecognizer.detect(frame)
            val strBuilder = StringBuilder()


            for (i in 0 until items.size()) {
                val item = items.valueAt(i) as TextBlock
                strBuilder.append(item.value)
            }


            textOfImage = strBuilder.toString().uppercase()


        } else {
            Toast.makeText(
                context,
                "Tu dispositivo no soporta reconocimiento de text",
                Toast.LENGTH_LONG
            ).show()

            textOfImage = "No operational Device (MC collect)"
        }

    }

    fun loadString(text: String) {

        textOfImage = if (isOperational) {

            text.uppercase()


        } else {
            Toast.makeText(
                context,
                "Tu dispositivo no soporta reconocimiento de text",
                Toast.LENGTH_LONG
            ).show()

            "No operational Device (MC collect)"
        }

    }

    fun prinText() {

        try {
            println("Text of image : $textOfImage")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getTextFromREGEX(pattern: Pattern, group: Int): String {

        val matcher = pattern.matcher(textOfImage)

        try {
            return if (matcher.find()) {
                matcher.group(group)!!
            } else {
                "N/F"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "N/FE"


    }

    fun getTextOfImage(): String {
        return textOfImage;
    }


}