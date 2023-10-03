package ua.pp.spidchenko.importcsv

import ua.pp.spidchenko.importcsv.util.AccountsCsvReader
import ua.pp.spidchenko.importcsv.util.CardsCsvReader
import ua.pp.spidchenko.importcsv.util.DOWNLOADS_FOLDER_PATH

object App {
    private const val ACCOUNTS_FILE_NAME = "export-2023-10-03-13-34-40.csv"
    private const val CARDS_FILE_NAME = "export-2023-10-02-12-27-25.csv"

    @JvmStatic
    fun main(args: Array<String>) {
        val accountsCsvReader = AccountsCsvReader(DOWNLOADS_FOLDER_PATH + ACCOUNTS_FILE_NAME)
        accountsCsvReader.readFile()
        //csvReader.dailyData.forEach { (day: LocalDateTime, count: Int) -> println(day.toLocalDate().toString() + ", " + count) }
        println("Accounts. Total csv records: ${accountsCsvReader.totalRecords}")
        println(accountsCsvReader)

        val cardsCsvReader = CardsCsvReader(DOWNLOADS_FOLDER_PATH + CARDS_FILE_NAME)
        cardsCsvReader.readFile()
        println("\nCards. Total csv records: ${cardsCsvReader.totalRecords}")
        println(cardsCsvReader)
    }
}
