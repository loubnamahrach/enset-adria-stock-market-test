package ma.enset.adria.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
        @NotBlank(message = "name is required")
        @Size(max = 120, message = "name must not exceed 120 characters") String name,
        @NotBlank(message = "symbol is required")
        @Size(max = 20, message = "symbol must not exceed 20 characters") String symbol) {
}
