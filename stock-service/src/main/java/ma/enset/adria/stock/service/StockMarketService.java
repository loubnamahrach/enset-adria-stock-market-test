package ma.enset.adria.stock.service;

import ma.enset.adria.stock.dto.CurrentPriceResponse;
import ma.enset.adria.stock.dto.StockMarketRequest;
import ma.enset.adria.stock.dto.StockMarketResponse;
import ma.enset.adria.stock.entity.Company;
import ma.enset.adria.stock.entity.StockMarket;
import ma.enset.adria.stock.exception.ResourceNotFoundException;
import ma.enset.adria.stock.mapper.StockMapper;
import ma.enset.adria.stock.repository.CompanyRepository;
import ma.enset.adria.stock.repository.StockMarketRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class StockMarketService {

    private final StockMarketRepository stockRepository;
    private final CompanyRepository companyRepository;
    private final Clock clock;

    public StockMarketService(StockMarketRepository stockRepository,
                              CompanyRepository companyRepository,
                              Clock clock) {
        this.stockRepository = stockRepository;
        this.companyRepository = companyRepository;
        this.clock = clock;
    }

    /** Adds a quotation, then recomputes the current price of its company. */
    @Transactional
    public StockMarketResponse create(StockMarketRequest request) {
        requireCompany(request.companyId());
        StockMarket saved = stockRepository.save(StockMapper.toEntity(request));
        refreshCurrentPrice(saved.getCompanyId());
        return StockMapper.toResponse(saved);
    }

    /** Deletes a quotation, then recomputes the current price of its company. */
    @Transactional
    public void delete(Long id) {
        StockMarket quotation = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));
        stockRepository.delete(quotation);
        stockRepository.flush();
        refreshCurrentPrice(quotation.getCompanyId());
    }

    @Transactional(readOnly = true)
    public List<StockMarketResponse> findAll() {
        return stockRepository.findAll(Sort.by(Sort.Direction.DESC, "date", "id")).stream()
                .map(StockMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StockMarketResponse findById(Long id) {
        return stockRepository.findById(id)
                .map(StockMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation", id));
    }

    /** Latest quotations of a company, newest first. */
    @Transactional(readOnly = true)
    public List<StockMarketResponse> findLatestByCompany(Long companyId, int limit) {
        requireCompany(companyId);
        return stockRepository.findByCompanyIdOrderByDateDescIdDesc(companyId, PageRequest.of(0, limit))
                .stream().map(StockMapper::toResponse).toList();
    }

    /**
     * Computes and stores the current price of a company from its latest quotation
     * (by date). The price is null when no quotation exists.
     */
    @Transactional
    public CurrentPriceResponse refreshCurrentPrice(Long companyId) {
        Company company = requireCompany(companyId);
        Optional<StockMarket> latest = stockRepository.findFirstByCompanyIdOrderByDateDescIdDesc(companyId);
        Instant now = clock.instant();
        company.updateCurrentPrice(latest.map(StockMarket::getCloseValue).orElse(null), now);
        companyRepository.save(company);
        return new CurrentPriceResponse(company.getId(), company.getName(),
                company.getCurrentPrice(), latest.map(StockMarket::getDate).orElse(null), now);
    }

    private Company requireCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
    }
}
