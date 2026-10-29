package ma.enset.adria.stock.repository;

import ma.enset.adria.stock.entity.StockMarket;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockMarketRepository extends JpaRepository<StockMarket, Long> {

    /** Most recent quotation by date; the id breaks ties for quotations of the same day. */
    Optional<StockMarket> findFirstByCompanyIdOrderByDateDescIdDesc(Long companyId);

    /** Quotations of a company, newest first, limited through the {@link Pageable}. */
    List<StockMarket> findByCompanyIdOrderByDateDescIdDesc(Long companyId, Pageable pageable);
}
