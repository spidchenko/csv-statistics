package ua.pp.spidchenko.importcsv

import ua.pp.spidchenko.importcsv.csv.AccountsCsvReader
import ua.pp.spidchenko.importcsv.csv.CardsCsvReader
import ua.pp.spidchenko.importcsv.csv.CsvReader
import ua.pp.spidchenko.importcsv.util.DOWNLOADS_FOLDER_PATH
import ua.pp.spidchenko.importcsv.util.findFreshAccountsFile
import ua.pp.spidchenko.importcsv.util.findFreshCardsFile
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.listDirectoryEntries

object App {

    @JvmStatic
    fun main(args: Array<String>) {

        val folder: Path = Paths.get(DOWNLOADS_FOLDER_PATH)
        val csvFilesInFolder: List<File> = folder.listDirectoryEntries("export-*.csv").sortedByDescending { file -> file.getLastModifiedTime() }.map { it.toFile() }

        val accountsFile = findFreshAccountsFile(csvFilesInFolder)
        val cardsFile = findFreshCardsFile(csvFilesInFolder)

        println("Scanning \"Downloads\" folder for *.csv files...")

        accountsFile?.let {
            println("Found accounts file:\t${accountsFile.name}")
            val accountsCsvReader: CsvReader = AccountsCsvReader(accountsFile)
            accountsCsvReader.readFile()
            //csvReader.dailyData.forEach { (day: LocalDateTime, count: Int) -> println(day.toLocalDate().toString() + ", " + count) }
            println("Total csv records: ${accountsCsvReader.totalRecords}")
            println("$accountsCsvReader\n")
        }

        cardsFile?.let {
            println("Found cards file:\t\t${cardsFile.name}")
            val cardsCsvReader = CardsCsvReader(cardsFile)
            cardsCsvReader.readFile()
            println("Total csv records: ${cardsCsvReader.totalRecords}")
            println(cardsCsvReader)
        }

    }
}
