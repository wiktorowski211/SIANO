package com.siano.view.budget

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.siano.api.model.Transaction
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object BudgetReport {

    data class Category(val id: Int, val name: String, val totalAmount: Double, val color: Int)

    fun createReport(transactions: List<Transaction>) {
        val categories = getCategories(transactions)
        createFile(categories, transactions)
    }

    private fun getCategories(transactions: List<Transaction>): List<Category> {
        val categories: List<String> = listOf(
            "Other",
            "Company",
            "Food at home",
            "Food out",
            "Internet",
            "Phone",
            "TV",
            "Rent",
            "Energy",
            "Gas",
            "Water",
            "Flat maintenance",
            "Shoes",
            "Clothes",
            "Fees",
            "Attractions",
            "Public transport",
            "Accommodation",
            "Accessories",
            "Hobby",
            "Alcohol",
            "Drugs",
            "Games",
            "Theater",
            "Cinema",
            "Concerts",
            "Books",
            "Subscription",
            "Car maintenance",
            "Fuel",
            "Parking",
            "Car insurance",
            "Health care",
            "Hygiene",
            "Sport",
            "Cosmetics",
            "Medicine",
            "Charity",
            "Gift"
        )



        return transactions.groupBy { it.category_id }.map {
            Category(
                it.key,
                categories[it.key - 1],
                it.value.sumByDouble { transaction ->
                    transaction.shares.filter { share -> share.amount > 0 }.sumByDouble { share -> share.amount }
                },
                Color.argb(
                    255,
//                    Random.nextInt(50, 200).toFloat(),
//                    Random.nextInt(50, 200).toFloat(),
//                    Random.nextInt(50, 200).toFloat()
                    Random.nextInt(0, 255),
                    Random.nextInt(0, 255),
                    Random.nextInt(0, 255)
                )
            )
        }
    }

    private fun createFile(categories: List<Category>, transactions: List<Transaction>) {

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 852, 1).create()
        val page = document.startPage(pageInfo)

        val canvas = page.canvas

        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawPaint(paint)
        paint.color = Color.BLACK
        canvas.drawText("Budget report", 30f, 30f, paint)

        val bitmap = drawPieChart(categories)

        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap, 30f, 60f, null)

        //create legend under the pie chart
        var offset = 250f
        var offset2 = 0f

        paint.color = Color.BLACK
        canvas.drawText("Legend: ", 30f, offset, paint)
        offset += 15f

        categories.forEachIndexed { i, category ->
            paint.color = category.color
            canvas.drawText("|", 30f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 31f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 32f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 33f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 34f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 35f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 36f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 37f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 38f, offset + i * 12.toFloat() + 0f, paint)
            canvas.drawText("|", 39f, offset + i * 12.toFloat() + 0f, paint)

            paint.color = Color.parseColor("#000000")
            canvas.drawText((category.name + ": "), 45f, offset + i * 12.toFloat(), paint)
            canvas.drawText(category.totalAmount.toString(), 150f, offset + i * 12.toFloat(), paint)
            offset2 = offset + i * 10.toFloat() + 0f
        }

        val fullSum = categories.sumByDouble { it.totalAmount }
        val transactionCount = categories.count()

        offset2 += 30f

        paint.color = Color.BLACK
        canvas.drawText("Transactions: ", 30f, offset2, paint)
        offset2 += 15f

        var offset3 = 0f;
        transactions.forEachIndexed { i, transaction ->
            val sum = transaction.shares.filter { share -> share.amount > 0 }.sumByDouble { it.amount }

            paint.color = Color.parseColor("#000000")
            canvas.drawText((i + 1).toString() + ". " + transaction.title, 30f, offset2 + i * 12.toFloat() + 0f, paint)
            canvas.drawText("  ", 60f, offset2 + i * 12.toFloat() + 0f, paint)
            canvas.drawText("" + sum, 150f, offset2 + i * 12.toFloat() + 0f, paint)

            offset3 = offset2 + i * 12.toFloat() + 0f
        }

        canvas.drawText(("Sum: "), 100f, offset3 + 20f, paint)
        canvas.drawText(fullSum.toString(), 150f, offset3 + 20f, paint)

        canvas.drawText("Number of transactions: " + transactionCount, 30f, offset3 + 40f, paint)
        canvas.drawText("Average transaction value: " + fullSum / transactionCount, 30f, offset3 + 60f, paint)


        document.finishPage(page)

        val dateFormat = SimpleDateFormat("yyyy_MM_dd")
        val date = Date()
        dateFormat.format(date)
        val filename = "report_${dateFormat.format(date)}_${Random.nextInt(1000)}.pdf"
        val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)
        document.writeTo(FileOutputStream(path))

        // close the document
        document.close()
    }

    private fun drawPieChart(categories: List<Category>): Bitmap {
        val bmp: Bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565)
        bmp.eraseColor(Color.WHITE)

        // canvas to draw on it
        val canvas = Canvas(bmp)
        val box = RectF(2f, 2f, (bmp.width - 2).toFloat(), (bmp.height - 2).toFloat())

        // get value for 100%
        val sum = categories.sumByDouble { it.totalAmount }

        // initialize painter
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            style = Paint.Style.FILL_AND_STROKE
        }
        var offset = 0f

        // draw slices
        categories.forEach {
            paint.color = it.color
            val angle = (360.0f / sum * it.totalAmount).toFloat()
            canvas.drawArc(box, offset, angle, true, paint)
            offset += angle
        }
        return bmp
    }

}