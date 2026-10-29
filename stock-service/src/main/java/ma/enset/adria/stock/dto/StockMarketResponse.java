package ma.enset.adria.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockMarketResponse(
        Long id,
        LocalDate date,
        BigDecimal openValue,
        BigDecimal closeValue,
        Long volume,
        Long companyId) {
}
