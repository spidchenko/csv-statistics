package ua.pp.spidchenko.importcsv.util

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.FileReader
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors

class CsvReader(private val fileName: String) {
    private var records: List<CSVRecord>? = null
    @Throws(IOException::class)
    fun readFile() {
        FileReader(fileName).use { reader ->
            val csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(",").setHeader(*HEADERS)
                    .setSkipHeaderRecord(true).build()
            records = csvFormat.parse(reader).stream()
                    .filter { r: CSVRecord -> r["Id konta"].toInt() >= FIRST_PRODUCTION_ID }.toList()
        }
    }

    val hourlyData: Map<LocalDateTime, Int>
        get() {
            // Create a Map to store the hourly counts
            val hourlyCounts: MutableMap<LocalDateTime, Int> = HashMap()
            for (record in records!!) {
                val createdAt = record["Data utworzenia"]
                val hourOfCreation = getHour(createdAt)

                // Increment the count for this hour
                hourlyCounts.merge(hourOfCreation, 1) { a: Int?, b: Int? -> Integer.sum(a!!, b!!) }
            }

            // Generate a list of all hours within the time range
            val minDateTime = hourlyCounts.keys.stream().min { obj: LocalDateTime, other: LocalDateTime? -> obj.compareTo(other) }
                    .orElse(LocalDateTime.now())
            val maxDateTime = hourlyCounts.keys.stream().max { obj: LocalDateTime, other: LocalDateTime? -> obj.compareTo(other) }
                    .orElse(LocalDateTime.now())
            val allHours: MutableList<LocalDateTime> = ArrayList()
            var currentHour = minDateTime
            while (!currentHour.isAfter(maxDateTime)) {
                allHours.add(currentHour)
                currentHour = currentHour.plusHours(1)
            }

            // Create a sorted and filled map with hourly counts
            return allHours.stream()
                    .collect(Collectors.toMap({ hour: LocalDateTime? -> hour }, { hour: LocalDateTime -> hourlyCounts.getOrDefault(hour, 0) }, { a: Int, _: Int? -> a }, { TreeMap() }))
        }
    val dailyData: Map<LocalDateTime, Int>
        get() {
            // Create a Map to store the hourly counts
            val dailyCounts: MutableMap<LocalDateTime, Int> = TreeMap()
            for (record in records!!) {
                val createdAt = record["Data utworzenia"]
                val dayOfCreation = getDay(createdAt)

                // Increment the count for this hour
                dailyCounts.merge(dayOfCreation, 1) { a: Int?, b: Int? -> Integer.sum(a!!, b!!) }
            }
            return dailyCounts
        }
    val totalProductionAcounts: Int
        get() {
            checkNotNull(records) { "File content has not been read. Call readFile first." }
            return records!!.size
        }

    companion object {
        val HEADERS = arrayOf("Id konta", "Nazwa", "Data utworzenia", "Typ", "Status", "Opis", "Rodzaj",
                "Profil użytkownika")
        const val FIRST_PRODUCTION_ID = 640
    }
}
