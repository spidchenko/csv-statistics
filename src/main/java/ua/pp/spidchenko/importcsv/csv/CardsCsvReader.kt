package ua.pp.spidchenko.importcsv.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CardsCsvReader(override val fileName: File) : CsvReader() {

    override val fileFormat: CSVFormat = defaultFormat.builder().setHeader(Headers::class.java).build()
    override var headers = listOf("Id", "Profil użytkownika", "Firma", "Status", "Id konta", "Id klienta", "Identyfikator", "Token", "Typ", "Taryfa profilu klienta", "Klasa kontraktu", "Data wygaśnięcia", "Metoda wyznaczania ceny")

    private var recordsFalaCards: List<CSVRecord>? = null
    private var recordsMobileApp: List<CSVRecord>? = null
    private var recordsEmvCards: List<CSVRecord>? = null


    private val falaActiveCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_ACTIVE.value }
    private val falaOnTheWayCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_ON_THE_WAY.value }
    private val falaRegisteredCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_REGISTERED.value }
    private val falaCanceledCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_CANCELED.value }


    override fun readFile() {
        FileReader(fileName).use { reader ->
            records = fileFormat.parse(reader).toList()
            recordsFalaCards = records.filter { it[Headers.TYPE] == Type.FALA_CARD.value }
            recordsMobileApp = records.filter { it[Headers.TYPE] == Type.MOBILE_APPLICATION.value }
            recordsEmvCards = records.filter { it[Headers.TYPE] == Type.EMV_CARD.value }
        }
    }

    override fun toString(): String {
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val today = LocalDate.now().format(dateFormat)
        return listOf(today, falaActiveCards, falaOnTheWayCards, falaRegisteredCards, falaCanceledCards, "---", recordsEmvCards?.size).joinToString()
    }

    @Suppress("unused")
    enum class Headers {
        ID, PROFILE, CORPORATION, STATUS, ACCOUNT_ID, CLIENT_ID, ENTITY_ID, TOKEN, TYPE, TARIFF, CONTRACT, EXP_DATE, PRICE_DETERMINATION_METHOD
    }


    private enum class Status(val value: String) {
        CARD_ACTIVE("Aktywny"),
        CARD_ON_THE_WAY("Nośnik w drodze"),
        CARD_REGISTERED("Zarejestrowany"),
        CARD_CANCELED("Wycofany")
    }

    private enum class Type(val value: String) {
        FALA_CARD("Karta"),
        MOBILE_APPLICATION("Aplikacja mobilna"),
        EMV_CARD("Karta EMV")
    }
}