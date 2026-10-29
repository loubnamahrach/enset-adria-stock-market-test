package ma.enset.adria.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A listed company. Its {@code currentPrice} is derived from the most recent
 * {@link StockMarket} quotation (by date) and refreshed by the service layer.
 */
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(name = "current_price", precision = 19, scale = 4)
    private BigDecimal currentPrice;

    @Column(name = "price_updated_at")
    private Instant priceUpdatedAt;

    protected Company() {
        // required by JPA
    }

    public Company(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public Instant getPriceUpdatedAt() {
        return priceUpdatedAt;
    }

    /** Updates the current price and records when it was refreshed. */
    public void updateCurrentPrice(BigDecimal price, Instant at) {
        this.currentPrice = price;
        this.priceUpdatedAt = at;
    }
}
