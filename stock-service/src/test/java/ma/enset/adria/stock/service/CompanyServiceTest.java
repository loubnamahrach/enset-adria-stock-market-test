package ma.enset.adria.stock.service;

import ma.enset.adria.stock.dto.CompanyRequest;
import ma.enset.adria.stock.entity.Company;
import ma.enset.adria.stock.exception.DuplicateResourceException;
import ma.enset.adria.stock.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository repository;
    @InjectMocks
    private CompanyService service;

    @Test
    void createNormalizesNameAndSymbol() {
        when(repository.existsByNameIgnoreCase("Acme Corp")).thenReturn(false);
        when(repository.existsBySymbolIgnoreCase("ACM")).thenReturn(false);
        when(repository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        var response = service.create(new CompanyRequest("  Acme Corp ", " acm "));

        assertThat(response.name()).isEqualTo("Acme Corp");
        assertThat(response.symbol()).isEqualTo("ACM");
    }

    @Test
    void createRejectsDuplicateName() {
        when(repository.existsByNameIgnoreCase("Acme")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CompanyRequest("Acme", "ACM")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(repository, never()).save(any());
    }
}
