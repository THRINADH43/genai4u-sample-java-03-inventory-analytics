package inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AnalyticsTest {
    static final List<Product> PRODUCTS = List.of(
            new Product("A", "Rice", "Grocery", new BigDecimal("100.00"), 5, 10),
            new Product("B", "Pen", "Stationery", new BigDecimal("10.50"), 50, 10),
            new Product("C", "Book", "Stationery", new BigDecimal("40.00"), 10, 10));
    static final List<Sale> SALES = List.of(
            new Sale(LocalDate.of(2026, 1, 1), "A", 2),
            new Sale(LocalDate.of(2026, 1, 2), "B", 10),
            new Sale(LocalDate.of(2026, 1, 3), "C", 1),
            new Sale(LocalDate.of(2026, 1, 3), "B", 4),
            new Sale(LocalDate.of(2026, 1, 4), "ZZZ", 99));

    Analytics a = new Analytics(PRODUCTS, SALES);

    @Test
    void lowStockIncludesItemsAtTheReorderLevel() {
        assertEquals(List.of("Rice", "Book"), a.lowStock().stream().map(Product::name).toList());
    }

    @Test
    void bestSellersAreSortedByUnits() {
        assertEquals(List.of(Map.entry("Pen", 14), Map.entry("Rice", 2)), a.bestSellers(2));
    }

    @Test
    void revenueByCategoryIgnoresUnknownSkus() {
        assertEquals(Map.of("Grocery", new BigDecimal("200.00"), "Stationery", new BigDecimal("187.00")),
                a.revenueByCategory());
    }

    @Test
    void readerParsesProductColumnsInOrder() throws Exception {
        List<Product> products = new CsvReader().read(Path.of("data/products.csv"), CsvReader::product);
        assertEquals(new Product("P1", "Basmati rice", "Grocery", new BigDecimal("120.00"), 40, 10), products.get(0));
    }

    @Test
    void readerSkipsBadLines() throws Exception {
        CsvReader reader = new CsvReader();
        List<Sale> sales = reader.read(Path.of("data/sales.csv"), CsvReader::sale);
        assertEquals(9, sales.size());
        assertEquals(1, reader.problems().size());
    }
}
