package ua.pp.spidchenko.importcsv.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CsvReader {

    static final String[] HEADERS = {"Id konta", "Nazwa", "Data utworzenia", "Typ", "Status", "Opis", "Rodzaj",
            "Profil użytkownika"};
    static final int FIRST_PRODUCTION_ID = 640;

    private final String fileName;
    private List<CSVRecord> records;

    public CsvReader(String fileName) {
        this.fileName = fileName;
    }

    public void readFile() throws IOException {
        try (Reader reader = new FileReader(fileName)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(",").setHeader(HEADERS)
                    .setSkipHeaderRecord(true).build();

            records = csvFormat.parse(reader).stream()
                    .filter(r -> Integer.parseInt(r.get("Id konta")) >= FIRST_PRODUCTION_ID).toList();
        }
    }

    public Map<LocalDateTime, Integer> getHourlyData() {
        // Create a Map to store the hourly counts
        Map<LocalDateTime, Integer> hourlyCounts = new HashMap<>();

        for (CSVRecord record : records) {
            String createdAt = record.get("Data utworzenia");
            LocalDateTime hourOfCreation = DateUtil.getHour(createdAt);

            // Increment the count for this hour
            hourlyCounts.merge(hourOfCreation, 1, Integer::sum);
        }

        // Generate a list of all hours within the time range
        LocalDateTime minDateTime = hourlyCounts.keySet().stream().min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        LocalDateTime maxDateTime = hourlyCounts.keySet().stream().max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
        List<LocalDateTime> allHours = new ArrayList<>();
        LocalDateTime currentHour = minDateTime;
        while (!currentHour.isAfter(maxDateTime)) {
            allHours.add(currentHour);
            currentHour = currentHour.plusHours(1);
        }

        // Create a sorted and filled map with hourly counts
        Map<LocalDateTime, Integer> sortedHourlyCounts = allHours.stream()
                .collect(Collectors.toMap(hour -> hour, hour -> hourlyCounts.getOrDefault(hour, 0), (a, b) -> a,
                        // Merge function, keeping the existing value if there are duplicates
                        TreeMap::new // Sort the map by keys (hours)
                ));

        return sortedHourlyCounts;
    }

    public Map<LocalDateTime, Integer> getDailyData() {
        // Create a Map to store the hourly counts
        Map<LocalDateTime, Integer> dailyCounts = new TreeMap<>();

        for (CSVRecord record : records) {
            String createdAt = record.get("Data utworzenia");
            LocalDateTime dayOfCreation = DateUtil.getDay(createdAt);

            // Increment the count for this hour
            dailyCounts.merge(dayOfCreation, 1, Integer::sum);
        }
        return dailyCounts;
    }

    public int getTotalProductionAcounts() {
        if (records == null) {
            throw new IllegalStateException("File content has not been read. Call readFile first.");
        }
        return records.size();
    }
}
