package ua.pp.spidchenko.importcsv.util

import java.nio.file.FileSystems
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

    private const val DATETIME_PATTERN = "yyyy/MM/dd HH:mm"
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN) // 2023/08/25 12:46
    val DOWNLOADS_FOLDER_PATH = System.getProperty("user.home") +
        FileSystems.getDefault().separator +
        "Downloads" +
        FileSystems.getDefault().separator

    fun getHour(dateTime: String?): LocalDateTime {
        val creationDateTime = LocalDateTime.parse(dateTime, formatter)
        return creationDateTime.withMinute(0).withSecond(0)
    }

    fun getDay(dateTime: String?): LocalDateTime {
        val creationDateTime = LocalDateTime.parse(dateTime, formatter)
        return creationDateTime.withHour(0).withMinute(0).withSecond(0)
    }