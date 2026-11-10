package inventory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Reads simple comma separated files with a header line. Bad lines are skipped and counted. */
public class CsvReader {
    private final List<String> problems = new ArrayList<>();

    public List<String> problems() {
        return problems;
    }

    /** Generic: turns every data line into a T with the given parser. */
    public <T> List<T> read(Path file, Function<String[], T> parser) throws IOException {
        List<T> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(file);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) continue;
            try {
                rows.add(parser.apply(line.split(",", -1)));
            } catch (RuntimeException e) {
                problems.add(file.getFileName() + " line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return rows;
    }

    public static Product product(String[] f) {
        if (f.length != 6) throw new IllegalArgumentException("expected 6 columns");
        return new Product(f[0].trim(), f[1].trim(), f[2].trim(), Integer.parseInt(f[3].trim()),
                Integer.parseInt(f[4].trim()), new BigDecimal(f[5].trim()));
    }

    public static Sale sale(String[] f) {
        if (f.length != 3) throw new IllegalArgumentException("expected 3 columns");
        int qty = Integer.parseInt(f[2].trim());
        if (qty <= 0) throw new IllegalArgumentException("quantity must be positive");
        return new Sale(LocalDate.parse(f[0].trim()), f[1].trim(), qty);
    }
}
