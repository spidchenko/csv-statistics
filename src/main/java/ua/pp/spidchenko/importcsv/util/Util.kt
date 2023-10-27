package ua.pp.spidchenko.importcsv.util

import ua.pp.spidchenko.importcsv.csv.AccountsCsvReader
import ua.pp.spidchenko.importcsv.csv.CardsCsvReader
import ua.pp.spidchenko.importcsv.csv.CsvReader
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.listDirectoryEntries

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

fun findFreshAccountsFile(files: List<File>): File? {
    files.forEach {
        val accountsFile: CsvReader = AccountsCsvReader(it)
        if (accountsFile.checkHeaders()) return it
    }
    return null
}

fun findFreshCardsFile(files: List<File>): File? {
    files.forEach {
        val accountsFile: CsvReader = CardsCsvReader(it)
        if (accountsFile.checkHeaders()) return it
    }
    return null
}

fun getAllCsvFilesInFolder(folder: Path): List<File> =
    folder
        .listDirectoryEntries("export-*.csv")
        .sortedByDescending { file -> file.getLastModifiedTime() }
        .map { it.toFile() }