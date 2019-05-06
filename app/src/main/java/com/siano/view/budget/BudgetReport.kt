package com.siano.view.budget

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.siano.api.model.Transaction
import com.siano.api.model.TransactionShare
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlin.random.Random

class BudgetReport {
    private val transactions: List<Transaction>

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    constructor(transactionsList: List<Transaction>) {
        this.transactions = transactionsList
        val categories = getCategories(transactions)

        createFile(categories)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCategories(transactions: List<Transaction>): MutableMap<Int, Pair<Color, Int>> {
        val categories: MutableMap<Int, Pair<Color, Int>> = mutableMapOf()

        transactions.forEach {
            if (it.category_id != null) {
                val shares: List<TransactionShare> = it.shares

                var sum: Int = 0;
                shares.forEach {
                    if (it.amount > 0) {
                        sum += it.amount.toInt()
                    }
                }

                if (!categories.containsKey(it.category_id)) {
                    val pair: Pair<Color, Int> = Pair(
                        Color.valueOf(
                            Random.nextInt(0, 255).toFloat(),
                            Random.nextInt(0, 255).toFloat(),
                            Random.nextInt(0, 255).toFloat()
                        ), sum
                    )
                    categories.put(it.category_id, pair)
                } else {
                    val category = categories[it.category_id]
                    var currentSum = category!!.component2()
                    currentSum += sum
                    val currentColor = category!!.component1()

                    categories.set(it.category_id, Pair(currentColor, currentSum))
                }
            }
        }

        return categories
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun createFile(categories: MutableMap<Int, Pair<Color, Int>>) {
        val categoriesKeys = categories.keys.toIntArray() //id of categories for chart
        val categoriesStrings: List<String> = arrayListOf("Other", "Company", "Food at home", "Food out", "Internet", "Phone", "TV", "Rent", "Energy", "Gas", "Water", "Flat maintenance", "Shoes", "Clothes", "Fees", "Attractions", "Public transport", "Accommodation", "Accessories", "Hobby", "Alcohol", "Drugs", "Games", "Theater", "Cinema", "Concerts", "Books", "Subscription", "Car maintenance", "Fuel", "Parking", "Car insurance", "Health care", "Hygiene", "Sport", "Cosmetics", "Medicine", "Charity", "Gift")
        val categoriesNames: MutableMap<Int, String> = mutableMapOf() //categories id with its' names
        categoriesKeys.forEach { categoriesNames.put(it, categoriesStrings[it])} //generating this ^
        val categoriesValues = categories.values //categories colors with values for generating chart

        val categoriesSums: MutableList<Int> = mutableListOf() //sums of shares in categories
        val categoriesColors: MutableList<Color> = mutableListOf() // colors for categories to generate legend

        categoriesValues.toList().forEach {
            categoriesColors.add(it.first)
            categoriesSums.add(it.second)
        }

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 852, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas

        val paint = Paint()
        paint.color = Color.parseColor("#ffffff")
        canvas.drawPaint(paint)
        paint.color = Color.parseColor("#000000")
        canvas.drawText("Budget report", 30f, 30f, paint)

        val categoriesSumsArray = categoriesSums.toIntArray()
        val categoriesColorsArray = categoriesColors.toTypedArray()

        val bitmap = drawPieChart(categoriesSumsArray, categoriesColorsArray)

        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap, 30f, 60f, null)

        //create legend under the pie chart
        val offset = 250f
        var offset2 = 0f
        for (i in 0..categories.size-1) {
            paint.color = categoriesColors[i].toArgb()
            canvas.drawText( "|" , 30f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 31f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 32f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 33f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 34f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 35f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 36f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 37f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 38f, offset + i*12.toFloat() + 0f, paint)
            canvas.drawText( "|" , 39f, offset + i*12.toFloat() + 0f, paint)

            paint.color = Color.parseColor("#000000")
            canvas.drawText((categoriesStrings[i] + ": ") , 45f, offset + i*12.toFloat(), paint)
            canvas.drawText(categoriesSums[i].toString() , 150f, offset + i*12.toFloat(), paint)
            offset2 = offset + i*10.toFloat() + 0f
        }

        var fullSum = 0
        categoriesSums.forEach{
            fullSum += it
        }

        canvas.drawText(("Sum: ") , 100f, offset2 + 20f, paint)
        canvas.drawText(fullSum.toString() , 150f, offset2 + 20f, paint)

        document.finishPage(page)

        val date = LocalDate.now()
        val filename =  "report_" + date.year + "_" + date.month + "_" + Random.nextInt(1000) + ".pdf"
        val mypath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        document.writeTo(FileOutputStream(mypath))

        // close the document
        document.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun drawPieChart(slices: IntArray, colors: Array<Color>): Bitmap {
        val bmp: Bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565)
        bmp.eraseColor(Color.WHITE)

        //canvas to draw on it
        val canvas = Canvas(bmp)
        val box = RectF(2f, 2f, (bmp.width - 2).toFloat(), (bmp.height - 2).toFloat())

        //get value for 100%
        var sum = 0
        for (slice in slices) {
            sum += slice
        }
        //initalize painter
        val paint = Paint()
        paint.isAntiAlias = true

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.style = Paint.Style.FILL_AND_STROKE
        var start = 0f
        //draw slices
        for (i in slices.indices) {
            paint.color = colors[i].toArgb()
            val angle: Float
            angle = 360.0f / sum * slices[i]
            canvas.drawArc(box, start, angle, true, paint)
            start += angle
        }
        return bmp
    }

}