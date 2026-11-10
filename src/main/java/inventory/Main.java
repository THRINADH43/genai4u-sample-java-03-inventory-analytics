package inventory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Path dir = Path.of(args.length > 0 ? args[0] : "data");
        CsvReader reader = new CsvReader();
        List<Product> products = reader.read(dir.resolve("products.csv"), CsvReader::product);
        List<Sale> sales = reader.read(dir.resolve("sales.csv"), CsvReader::sale);
        reader.problems().forEach(p -> System.out.println("Skipped " + p));

        Analytics a = new Analytics(products, sales);
        System.out.println("\nLow stock:");
        a.lowStock().forEach(p -> System.out.printf("  %-14s %3d left (reorder at %d)%n", p.name(), p.stock(), p.reorderLevel()));
        System.out.println("\nBest sellers:");
        a.bestSellers(3).forEach(e -> System.out.printf("  %-14s %3d units%n", e.getKey(), e.getValue()));
        System.out.println("\nRevenue by category:");
        a.revenueByCategory().forEach((c, r) -> System.out.printf("  %-14s Rs %10s%n", c, r));
    }
}
