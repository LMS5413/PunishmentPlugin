package com.github.lms5413.punishment.utils

import com.github.lms5413.punishment.Punishment

fun convertStringToTime(time: String): Long? {
    val timeUnits = mapOf(
        's' to 1000L,
        'm' to 60 * 1000L,
        'h' to 60 * 60 * 1000L,
        'd' to 24 * 60 * 60 * 1000L
    )

    var totalTime = 0L
    val currentNumber = StringBuilder()
    var hasValidFormat = false

    for (char in time) {
        when {
            char.isDigit() -> currentNumber.append(char)
            timeUnits.containsKey(char) -> {
                if (currentNumber.isEmpty()) return null
                totalTime += currentNumber.toString().toLong() * timeUnits.getValue(char)
                currentNumber.clear()
                hasValidFormat = true
            }
            else -> return if (hasValidFormat) totalTime else null
        }
    }

    return if (hasValidFormat) totalTime else null
}

fun convertTimeToString(time: Long): String {

    val fomatterSection = Punishment.getInstance().config.getConfigurationSection("formatter")

    val daysFormat = fomatterSection?.getString("days") ?: "d"
    val hoursFormat = fomatterSection?.getString("hours") ?: "h"
    val minutesFormat = fomatterSection?.getString("minutes") ?: "m"
    val secondsFormat = fomatterSection?.getString("seconds") ?: "s"

    val timeUnits = mapOf(
        daysFormat to 24 * 60 * 60 * 1000L,
        hoursFormat to 60 * 60 * 1000L,
        minutesFormat to 60 * 1000L,
        secondsFormat to 1000L
    )

    var remainingTime = time
    val timeStringBuilder = StringBuilder()

    for ((unit, unitValue) in timeUnits) {
        if (remainingTime >= unitValue) {
            val unitCount = remainingTime / unitValue
            remainingTime %= unitValue
            timeStringBuilder.append("$unitCount$unit ")
        }
    }

    return timeStringBuilder.toString().trim()
}