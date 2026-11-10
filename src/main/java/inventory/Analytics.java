package inventory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Answers questions about the inventory using streams. */
public class Analytics {
    private final Map<String, Product> productsById;
    private final List<Sale> sales;

    public Analytics(List<Product> products, List<Sale> sales) {
        this.productsById = products.stream()
                .collect(Collectors.toMap(Product::id, Function.identity()));
        // sales for unknown product ids cannot be priced, so they are left out
        this.sales = sales.stream().filter(s -> productsById.containsKey(s.productId())).toList();
    }

    public List<Product> lowStock() {
        return productsById.values().stream()
                .filter(Product::isLowStock)
                .sorted(Comparator.comparingInt(Product::stock).thenComparing(Product::name))
                .toList();
    }

    /** Units sold per product name. */
    public Map<String, Integer> unitsSold() {
        return sales.stream().collect(Collectors.groupingBy(
                s -> productsById.get(s.productId()).name(), TreeMap::new,
                Collectors.summingInt(Sale::quantity)));
    }

    public List<Map.Entry<String, Integer>> bestSellers(int n) {
        return unitsSold().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .toList();
    }

    public Map<String, BigDecimal> revenueByCategory() {
        return sales.stream().collect(Collectors.groupingBy(
                s -> productsById.get(s.productId()).category(), TreeMap::new,
                Collectors.reducing(BigDecimal.ZERO, this::revenue, BigDecimal::add)));
    }

    private BigDecimal revenue(Sale s) {
        return productsById.get(s.productId()).price().multiply(BigDecimal.valueOf(s.quantity()));
    }
}
