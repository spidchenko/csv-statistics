package ua.pp.spidchenko.importcsv;

import ua.pp.spidchenko.importcsv.util.CsvReader;

import java.io.IOException;
import java.nio.file.FileSystems;

public class App {

    static final String FILE_PATH = "C:\\Users\\DmytroSpidchenkoAdmi\\Downloads\\";
    static final String FILE_NAME = "export-2023-09-20-08-29-27.csv";

    public static void main(String[] args) throws IOException {

        CsvReader csvReader = new CsvReader(FILE_PATH + FileSystems.getDefault().getSeparator() + FILE_NAME);
        csvReader.readFile();

        csvReader.getDailyData().forEach((day, count) -> {
            System.out.println(day.toLocalDate() + ", " + count);
        });

        System.out.println("\nTotal production users: " + csvReader.getTotalProductionAcounts());
    }
}
