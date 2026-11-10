package inventory;

import java.math.BigDecimal;

public record Product(String id, String name, String category, BigDecimal price, int stock, int reorderLevel) {
    public boolean isLowStock() {
        return stock <= reorderLevel;
    }
}
