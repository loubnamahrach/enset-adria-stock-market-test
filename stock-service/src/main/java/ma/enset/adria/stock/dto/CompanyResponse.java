package ma.enset.adria.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record CompanyResponse(
        Long id,
        String name,
        String symbol,
        BigDecimal currentPrice,
        Instant priceUpdatedAt) {
}
