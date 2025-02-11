package com.splitsnap.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import com.google.firebase.vertexai.type.Schema
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


fun imageSchemaProvider(): Schema {
   return Schema.obj(
        mapOf(


            "receiver" to Schema.obj(
                mapOf(
                    "name" to Schema.string(),
                    "upiId" to Schema.string()
                )
            ),
            "amount" to Schema.double(),
            "time" to Schema.string("Iso Time"), // ISO 8601 format
            "transactionId" to Schema.string(),
            "platform" to Schema.enumeration(listOf("GooglePay", "PhonePe", "Paytm", "Other"))
        )
    )
}



fun Date.updateDateFromMillis(selectedMillis: Long): Date {

    val cal = Calendar.getInstance()

    cal.time = this


    val selectedCal = Calendar.getInstance()
    selectedCal.timeInMillis = selectedMillis

    cal.set(Calendar.YEAR, selectedCal.get(Calendar.YEAR))
    cal.set(Calendar.MONTH, selectedCal.get(Calendar.MONTH))
    cal.set(Calendar.DAY_OF_MONTH, selectedCal.get(Calendar.DAY_OF_MONTH))

    // Update the date object
    return cal.time

}

@OptIn(ExperimentalMaterial3Api::class)
fun Date.updateTimeFromMillis(timePickerState: TimePickerState): Date {
    val cal = Calendar.getInstance()
    cal.time = this

    // Update the hour and minute based on TimePicker state
    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
    cal.set(Calendar.MINUTE, timePickerState.minute)

    // Optional: Reset seconds and milliseconds if not needed
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    // Update the date object
    return cal.time
}




fun formatToDate(timeString: String): Date {
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    isoFormat.timeZone = TimeZone.getTimeZone("UTC") // Handle UTC timezone
    return try {
        isoFormat.parse(timeString) ?: Date() // Fallback to current date
    } catch (e: Exception) {
        e.printStackTrace()
        Date() // Return current date in case of error
    }
}

fun formatToIsoString(date: Date): String {
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    isoFormat.timeZone = TimeZone.getTimeZone("UTC")
    return isoFormat.format(date)
}



// Function to convert Drawable to Bitmap
fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    return if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        null
    }
}