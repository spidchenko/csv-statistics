package ua.pp.spidchenko.importcsv.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.apache.commons.io.input.BOMInputStream
import java.io.*

abstract class CsvReader {
    abstract val fileName: File
    protected val defaultFormat: CSVFormat = CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).build()
    protected abstract val fileFormat: CSVFormat
    protected lateinit var records: List<CSVRecord>
    abstract var headers: List<String>
    @Throws(IOException::class)
    abstract fun readFile()
    abstract override fun toString(): String

    val totalRecords
        get() = if (::records.isInitialized) records.size else 0

    fun checkHeaders(): Boolean {
        // We need this to remove BOM - Byte Order Mask (0xfeff)
        InputStreamReader(BOMInputStream(FileInputStream(fileName)), "UTF-8").use { reader ->
            return headers == CSVFormat.DEFAULT.parse(reader).iterator().next().values().toList()
        }
    }
}