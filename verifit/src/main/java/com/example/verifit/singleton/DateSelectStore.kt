package com.example.verifit.singleton

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateSelectStore {
    var date_selected: String
    init {
        val date_clicked = Date()
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        date_selected = dateFormat.format(date_clicked)
    }

}