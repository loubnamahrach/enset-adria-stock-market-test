package ma.enset.adria.stock.service;

import ma.enset.adria.stock.dto.StockMarketRequest;
import ma.enset.adria.stock.entity.Company;
import ma.enset.adria.stock.entity.StockMarket;
import ma.enset.adria.stock.exception.ResourceNotFoundException;
import ma.enset.adria.stock.repository.CompanyRepository;
import ma.enset.adria.stock.repository.StockMarketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockMarketServiceTest {

    private static final Instant NOW = Instant.parse("2024-06-01T10:00:00Z");

    @Mock
    private StockMarketRepository stockRepository;
    @Mock
    private CompanyRepository companyRepository;

    private StockMarketService service;
    private Company company;

    @BeforeEach
    void setUp() {
        service = new StockMarketService(stockRepository, companyRepository,
                Clock.fixed(NOW, ZoneOffset.UTC));
        company = new Company("ACME", "ACM");
    }

    private StockMarketRequest request(Long companyId) {
        return new StockMarketRequest(LocalDate.parse("2024-05-30"), new BigDecimal("10.5"),
                new BigDecimal("11.25"), 500L, companyId);
    }

    @Test
    void createSavesQuotationAndRefreshesCompanyPrice() {
        StockMarket latest = new StockMarket(LocalDate.parse("2024-05-30"), new BigDecimal("10.5"),
                new BigDecimal("11.25"), 500L, 1L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(stockRepository.save(any(StockMarket.class))).thenAnswer(i -> i.getArgument(0));
        when(stockRepository.findFirstByCompanyIdOrderByDateDescIdDesc(1L)).thenReturn(Optional.of(latest));

        var response = service.create(request(1L));

        assertThat(response.closeValue()).isEqualByComparingTo("11.25");
        assertThat(company.getCurrentPrice()).isEqualByComparingTo("11.25");
        assertThat(company.getPriceUpdatedAt()).isEqualTo(NOW);
    }

    @Test
    void createFailsWhenCompanyDoesNotExist() {
        when(companyRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request(9L)))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(stockRepository, never()).save(any());
    }

    @Test
    void deleteRemovesQuotationAndFallsBackToPreviousPrice() {
        StockMarket toDelete = new StockMarket(LocalDate.parse("2024-05-30"), BigDecimal.ONE,
                new BigDecimal("20"), 1L, 1L);
        StockMarket previous = new StockMarket(LocalDate.parse("2024-05-29"), BigDecimal.ONE,
                new BigDecimal("15"), 1L, 1L);
        when(stockRepository.findById(5L)).thenReturn(Optional.of(toDelete));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(stockRepository.findFirstByCompanyIdOrderByDateDescIdDesc(1L)).thenReturn(Optional.of(previous));

        service.delete(5L);

        verify(stockRepository).delete(toDelete);
        assertThat(company.getCurrentPrice()).isEqualByComparingTo("15");
    }

    @Test
    void deleteFailsWhenQuotationDoesNotExist() {
        when(stockRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void refreshSetsNullPriceWhenCompanyHasNoQuotation() {
        company.updateCurrentPrice(new BigDecimal("8"), NOW.minusSeconds(60));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(stockRepository.findFirstByCompanyIdOrderByDateDescIdDesc(1L)).thenReturn(Optional.empty());

        var response = service.refreshCurrentPrice(1L);

        assertThat(response.price()).isNull();
        assertThat(response.quotationDate()).isNull();
        assertThat(company.getCurrentPrice()).isNull();
    }

    @Test
    void findByIdFailsWhenQuotationDoesNotExist() {
        when(stockRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(3L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
