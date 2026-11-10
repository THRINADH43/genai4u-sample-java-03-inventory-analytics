# Inventory analytics

Reads products and sales from CSV files and reports low stock, best sellers and revenue by
category. Built with Maven, using records, generics, collections and streams.

## Build and run

You need Java 17+ and Maven.

```
mvn package
java -cp target/classes inventory.Main data
```

Output with the sample data:

```
Skipped sales.csv line 9: For input string: "abc"

Low stock:
  Pressure cooker   2 left (reorder at 3)
  Toor dal         6 left (reorder at 10)
  Gel pen         30 left (reorder at 40)

Best sellers:
  Gel pen         35 units
  Notebook        35 units
  Basmati rice     8 units

Revenue by category:
  Grocery        Rs    1562.00
  Kitchen        Rs    1960.00
  Stationery     Rs    1925.00
```

## Input files

- `data/products.csv`: `sku,name,category,stock,reorder_level,price`
- `data/sales.csv`: `date,sku,quantity`

Lines with the wrong number of columns or bad numbers are skipped and listed. Sales for a
SKU that is not in the products file are ignored, because they have no price.

## Design

- `Product` and `Sale` are records: plain data that never changes.
- `CsvReader.read` is generic (`<T> List<T> read(Path, Function<String[], T>)`), so one method
  reads both files.
- `Analytics` keeps products in a `Map` by SKU for fast lookup, and uses streams with
  `groupingBy`, `summingInt` and `reducing` for the reports. Money is `BigDecimal`. Results
  go into a `TreeMap`, so they print in a stable order.

## Tests

`mvn test` runs the JUnit 5 tests in `src/test/java/inventory/AnalyticsTest.java`.
