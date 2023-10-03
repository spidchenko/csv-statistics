package ua.pp.spidchenko.importcsv.util

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.FileReader
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CardsCsvReader(private val fileName: String) {
    private var records: List<CSVRecord>? = null
    private var recordsFalaCards: List<CSVRecord>? = null
    private var recordsMobileApp: List<CSVRecord>? = null
    private var recordsEmvCards: List<CSVRecord>? = null

    val totalRecords
        get() = records?.size

    private val falaActiveCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_ACTIVE.value }
    private val falaOnTheWayCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_ON_THE_WAY.value }
    private val falaRegisteredCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_REGISTERED.value }
    private val falaCanceledCards
        get() = recordsFalaCards?.count { it[Headers.STATUS] == Status.CARD_CANCELED.value }
    @Throws(IOException::class)
    fun readFile() {
        FileReader(fileName).use { reader ->
            val csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(",").setHeader(Headers::class.java).setSkipHeaderRecord(true).build()
            records = csvFormat.parse(reader).toList()
            recordsFalaCards = records?.filter { it[Headers.TYPE] == Type.FALA_CARD.value }
            recordsMobileApp = records?.filter { it[Headers.TYPE] == Type.MOBILE_APPLICATION.value }
            recordsEmvCards= records?.filter { it[Headers.TYPE] == Type.EMV_CARD.value }
        }
    }

    override fun toString(): String {
        val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val today = LocalDate.now().format(dateFormat)
        return listOf(today, falaActiveCards, falaOnTheWayCards, falaRegisteredCards, falaCanceledCards, "   ").joinToString()
    }

    companion object {
        enum class Headers {
            //Id,Profil użytkownika,Firma,Status,Id konta,Id klienta,Identyfikator,Token,Typ,Taryfa profilu klienta,Klasa kontraktu,Data wygaśnięcia,Metoda wyznaczania ceny
            ID, PROFILE, CORPORATION, STATUS, ACCOUNT_ID, CLIENT_ID, ENTITY_ID, TOKEN, TYPE, TARIFF, CONTRACT, EXP_DATE, PRICE_DETERMINATION_METHOD
        }

        enum class Status(val value: String) {
            CARD_ACTIVE("Aktywny"),
            CARD_ON_THE_WAY("Nośnik w drodze"),
            CARD_REGISTERED("Zarejestrowany"),
            CARD_CANCELED("Wycofany")
        }

        enum class Type(val value: String) {
            FALA_CARD("Karta"),
            MOBILE_APPLICATION("Aplikacja mobilna"),
            EMV_CARD("Karta EMV")
        }
    }
}