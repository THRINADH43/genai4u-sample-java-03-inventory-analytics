package inventory;

import java.math.BigDecimal;

public record Product(String sku, String name, String category, int stock, int reorderLevel, BigDecimal price) {
    public boolean isLowStock() {
        return stock <= reorderLevel;
    }
}
