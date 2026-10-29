package ma.enset.adria.stock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

/** A daily stock quotation of a company. */
@Entity
@Table(name = "stock_market",
        indexes = @Index(name = "idx_stock_company_date", columnList = "company_id, quotation_date"))
public class StockMarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quotation_date", nullable = false)
    private LocalDate date;

    @Column(name = "open_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal openValue;

    @Column(name = "close_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal closeValue;

    @Column(nullable = false)
    private Long volume;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    protected StockMarket() {
        // required by JPA
    }

    public StockMarket(LocalDate date, BigDecimal openValue, BigDecimal closeValue,
                       Long volume, Long companyId) {
        this.date = date;
        this.openValue = openValue;
        this.closeValue = closeValue;
        this.volume = volume;
        this.companyId = companyId;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getOpenValue() {
        return openValue;
    }

    public BigDecimal getCloseValue() {
        return closeValue;
    }

    public Long getVolume() {
        return volume;
    }

    public Long getCompanyId() {
        return companyId;
    }
}
