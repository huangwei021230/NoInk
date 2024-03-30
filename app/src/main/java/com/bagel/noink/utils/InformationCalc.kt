package com.bagel.noink.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InformationCalc {
    companion object {
        fun calculateAge(birthday: String): Int {
            // 校验日期格式
            val pattern = "\\d{4}.\\d{1,2}.\\d{1,2}".toRegex()
            if (!birthday.matches(pattern)) {
                return -1
            }

            val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val currentDate = Calendar.getInstance()

            val birthDate = Calendar.getInstance()
            birthDate.time = dateFormat.parse(birthday)!!

            var age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

            if (currentDate.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                (currentDate.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
                        currentDate.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))
            ) {
                age--
            }
            return age
        }

        fun convertDateFormat(date: String): String {
            val parts = date.split(".")
            val year = parts[0]
            val month = parts[1]
            val day = parts[2]
            return "$year-$month-$day"
        }
    }
}