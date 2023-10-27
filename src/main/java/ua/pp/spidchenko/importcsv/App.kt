package ua.pp.spidchenko.importcsv

import ua.pp.spidchenko.importcsv.csv.AccountsCsvReader
import ua.pp.spidchenko.importcsv.csv.CardsCsvReader
import ua.pp.spidchenko.importcsv.csv.CsvReader
import ua.pp.spidchenko.importcsv.util.DOWNLOADS_FOLDER_PATH
import ua.pp.spidchenko.importcsv.util.findFreshAccountsFile
import ua.pp.spidchenko.importcsv.util.findFreshCardsFile
import ua.pp.spidchenko.importcsv.util.getAllCsvFilesInFolder
import java.nio.file.Path
import java.nio.file.Paths

object App {

    @JvmStatic
    fun main(args: Array<String>) {

        val downloadsFolder: Path = Paths.get(DOWNLOADS_FOLDER_PATH)
        val csvFilesInFolder = getAllCsvFilesInFolder(downloadsFolder)

        val fileWithAccounts = findFreshAccountsFile(csvFilesInFolder)
        val fileWithCards = findFreshCardsFile(csvFilesInFolder)

        println("Scanning \"Downloads\" folder for *.csv files...")

        fileWithAccounts?.let {
            println("Found accounts file:\t${fileWithAccounts.name}")
            val accountsCsvReader: CsvReader = AccountsCsvReader(fileWithAccounts)
            accountsCsvReader.readFile()
            //csvReader.dailyData.forEach { (day: LocalDateTime, count: Int) -> println(day.toLocalDate().toString() + ", " + count) }
            println("Total csv records: ${accountsCsvReader.totalRecords}")
            println("$accountsCsvReader\n")
        }

        fileWithCards?.let {
            println("Found cards file:\t\t${fileWithCards.name}")
            val cardsCsvReader = CardsCsvReader(fileWithCards)
            cardsCsvReader.readFile()
            println("Total csv records: ${cardsCsvReader.totalRecords}")
            println(cardsCsvReader)
        }

    }
}
