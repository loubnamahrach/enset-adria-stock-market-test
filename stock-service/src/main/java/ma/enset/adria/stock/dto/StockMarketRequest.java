package ma.enset.adria.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Payload used to create a quotation. */
public record StockMarketRequest(
        @NotNull(message = "date is required") LocalDate date,
        @NotNull(message = "openValue is required")
        @Positive(message = "openValue must be positive") BigDecimal openValue,
        @NotNull(message = "closeValue is required")
        @Positive(message = "closeValue must be positive") BigDecimal closeValue,
        @NotNull(message = "volume is required")
        @PositiveOrZero(message = "volume must not be negative") Long volume,
        @NotNull(message = "companyId is required")
        @Positive(message = "companyId must be positive") Long companyId) {
}
