package ma.enset.adria.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Current price of a company: close value of its latest quotation (by date).
 * {@code price} and {@code quotationDate} are null when the company has no quotation yet.
 */
public record CurrentPriceResponse(
        Long companyId,
        String companyName,
        BigDecimal price,
        LocalDate quotationDate,
        Instant updatedAt) {
}
