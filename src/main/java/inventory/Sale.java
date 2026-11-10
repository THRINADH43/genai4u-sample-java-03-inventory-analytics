package inventory;

import java.time.LocalDate;

public record Sale(LocalDate date, String sku, int quantity) {
}
