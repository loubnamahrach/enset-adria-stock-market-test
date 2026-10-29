package ma.enset.adria.stock.repository;

import ma.enset.adria.stock.entity.StockMarket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StockMarketRepositoryTest {

    @Autowired
    private StockMarketRepository repository;

    private StockMarket quote(String date, String close, long companyId) {
        return repository.save(new StockMarket(LocalDate.parse(date), new BigDecimal("10.00"),
                new BigDecimal(close), 1000L, companyId));
    }

    @Test
    void latestQuotationIsChosenByDateNotByInsertionOrder() {
        quote("2024-03-10", "50.00", 1L);
        quote("2024-03-12", "70.00", 1L); // newest date
        quote("2024-03-11", "60.00", 1L); // inserted last, but older
        quote("2024-03-20", "99.00", 2L); // another company

        var latest = repository.findFirstByCompanyIdOrderByDateDescIdDesc(1L);

        assertThat(latest).isPresent();
        assertThat(latest.get().getCloseValue()).isEqualByComparingTo("70.00");
    }

    @Test
    void latestQuotationIsEmptyWhenCompanyHasNoQuotation() {
        assertThat(repository.findFirstByCompanyIdOrderByDateDescIdDesc(42L)).isEmpty();
    }

    @Test
    void listsQuotationsNewestFirstWithLimit() {
        quote("2024-01-01", "1.00", 1L);
        quote("2024-01-03", "3.00", 1L);
        quote("2024-01-02", "2.00", 1L);

        List<StockMarket> result = repository.findByCompanyIdOrderByDateDescIdDesc(1L, PageRequest.of(0, 2));

        assertThat(result).extracting(q -> q.getCloseValue().intValue()).containsExactly(3, 2);
    }
}
