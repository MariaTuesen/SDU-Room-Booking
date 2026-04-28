package com.example.sduroombooking.validation

fun validateBookingTime(
    startTime: String,
    endTime: String
): String? {
    val start = timeToMinutes(startTime)
    val end = timeToMinutes(endTime)

    val duration = end - start

    return when {
        end <= start -> "End time must be after start time"
        duration > 240 -> "You can only reserve up to 4 hours"
        else -> null
    }
}

fun validateBookingInputs(
    date: String?,
    startTime: String?,
    endTime: String?,
    location: String,
    building: String
): String? {
    if (date == null) return "Please select a date"
    if (startTime == null || endTime == null) return "Please select time"
    if (location.isBlank()) return "Please select location"
    if (building.isBlank()) return "Please select building"

    return null
}

private fun timeToMinutes(hhmm: String): Int {
    val parts = hhmm.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return h * 60 + m
}