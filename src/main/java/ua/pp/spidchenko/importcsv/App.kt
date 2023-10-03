package ua.pp.spidchenko.importcsv

import ua.pp.spidchenko.importcsv.util.CsvReader
import ua.pp.spidchenko.importcsv.util.DOWNLOADS_FOLDER_PATH

object App {
    private const val ACCOUNTS_FILE_NAME = "export-2023-10-02-14-56-55.csv"
    @JvmStatic
    fun main(args: Array<String>) {
        val csvReader = CsvReader(DOWNLOADS_FOLDER_PATH + ACCOUNTS_FILE_NAME)
        csvReader.readFile()
        //csvReader.dailyData.forEach { (day: LocalDateTime, count: Int) -> println(day.toLocalDate().toString() + ", " + count) }
        println("Accounts. Total csv records: ${csvReader.totalRecords}")
        println(csvReader)
    }
}
