package com.splitsnap.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun formatDate(date: Long,pattern:String="MMM dd, yyyy"): String {

        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(Date(date))
    }
}