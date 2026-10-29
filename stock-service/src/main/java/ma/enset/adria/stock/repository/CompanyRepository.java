package ma.enset.adria.stock.repository;

import ma.enset.adria.stock.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySymbolIgnoreCase(String symbol);
}
