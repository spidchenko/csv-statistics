package ua.pp.spidchenko.importcsv.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import ua.pp.spidchenko.importcsv.util.getDay
import ua.pp.spidchenko.importcsv.util.getHour
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

class AccountsCsvReader(override val fileName: File) : CsvReader() {

    override val fileFormat: CSVFormat = defaultFormat.builder().setHeader(Headers::class.java).build()
    override var headers = listOf("Id konta", "Nazwa", "Data utworzenia", "Typ", "Status", "Opis", "Rodzaj", "Profil użytkownika")

    private var recordsIndividual: List<CSVRecord>? = null
    private var recordsAnonymous: List<CSVRecord>? = null
    private var recordsCorporate: List<CSVRecord>? = null

    private val individualActiveUsers
        get() = recordsIndividual?.count { it[Headers.STATUS] == Status.ACCOUNT_ACTIVE.value }
    private val individualCanceledUsers
        get() = recordsIndividual?.count { it[Headers.STATUS] == Status.ACCOUNT_CANCELED.value }
    private val individualBlockedUsers
        get() = recordsIndividual?.count { it[Headers.STATUS] == Status.ACCOUNT_BLOCKED.value }
    private val anonymousActiveUsers
        get() = recordsAnonymous?.count { it[Headers.STATUS] == Status.ACCOUNT_ACTIVE.value }
    private val anonymousCanceledUsers
        get() = recordsAnonymous?.count { it[Headers.STATUS] == Status.ACCOUNT_CANCELED.value }
    private val anonymousBlockedUsers
        get() = recordsAnonymous?.count { it[Headers.STATUS] == Status.ACCOUNT_BLOCKED.value }
    private val corporateActiveUsers
        get() = recordsCorporate?.count { it[Headers.STATUS] == Status.ACCOUNT_ACTIVE.value }
    private val corporateCanceledUsers
        get() = recordsCorporate?.count { it[Headers.STATUS] == Status.ACCOUNT_CANCELED.value }
    private val corporateBlockedUsers
        get() = recordsCorporate?.count { it[Headers.STATUS] == Status.ACCOUNT_BLOCKED.value }

    @Throws(IOException::class)
    override fun readFile() {
        FileReader(fileName).use { reader ->
            records = fileFormat.parse(reader).toList()
            recordsIndividual = records.filter { it[Headers.TYPE] == Type.ACCOUNT_INDIVIDUAL.value }
            recordsAnonymous = records.filter { it[Headers.TYPE] == Type.ACCOUNT_ANONYMOUS.value }
            recordsCorporate = records.filter { it[Headers.KIND] == Type.ACCOUNT_CORPORATE.value }
        }
    }

    val hourlyData: Map<LocalDateTime, Int>
        get() {
            // Create a Map to store the hourly counts
            val hourlyCounts: MutableMap<LocalDateTime, Int> = HashMap()
            for (record in records) {
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
            for (record in records) {
                val createdAt = record["Data utworzenia"]
                val dayOfCreation = getDay(createdAt)

                // Increment the count for this hour
                dailyCounts.merge(dayOfCreation, 1) { a: Int?, b: Int? -> Integer.sum(a!!, b!!) }
            }
            return dailyCounts
        }

    override fun toString(): String {
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val today = LocalDate.now().format(dateFormat)
        return listOf(today, individualActiveUsers, individualCanceledUsers, individualBlockedUsers, "   ", anonymousActiveUsers, anonymousCanceledUsers, anonymousBlockedUsers, "   ", corporateActiveUsers, corporateCanceledUsers, corporateBlockedUsers).joinToString()
    }
    @Suppress("unused")
    enum class Headers {
        ID, NAME, CREATION_DATE, TYPE, STATUS, DESCRIPTION, KIND, PROFILE
    }

    enum class Status(val value: String) {
        ACCOUNT_ACTIVE("Aktywne konto klienta"),
        ACCOUNT_CANCELED("Anulowane konto klienta"),
        ACCOUNT_BLOCKED("Zablokowane konto klienta"),
    }

    enum class Type(val value: String) {
        ACCOUNT_INDIVIDUAL("Osobiste konto klienta"),
        ACCOUNT_ANONYMOUS("Anonimowe konto klienta"),
        ACCOUNT_CORPORATE("Firmowe konto klienta")
    }
}