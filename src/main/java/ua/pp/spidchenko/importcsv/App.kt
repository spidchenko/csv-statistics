package ua.pp.spidchenko.importcsv

import ua.pp.spidchenko.importcsv.util.CsvReader
import ua.pp.spidchenko.importcsv.util.DOWNLOADS_FOLDER_PATH
import java.time.LocalDateTime

object App {
    private const val FILE_NAME = "export-2023-09-20-08-29-27.csv"
    @JvmStatic
    fun main(args: Array<String>) {
        val csvReader = CsvReader(DOWNLOADS_FOLDER_PATH + FILE_NAME)
        csvReader.readFile()
        csvReader.dailyData.forEach { (day: LocalDateTime, count: Int) -> println(day.toLocalDate().toString() + ", " + count) }
        println("\nTotal production users: ${csvReader.totalProductionAcounts}")
    }
}
