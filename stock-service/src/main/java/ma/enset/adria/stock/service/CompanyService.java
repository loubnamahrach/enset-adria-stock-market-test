package ma.enset.adria.stock.service;

import ma.enset.adria.stock.dto.CompanyRequest;
import ma.enset.adria.stock.dto.CompanyResponse;
import ma.enset.adria.stock.entity.Company;
import ma.enset.adria.stock.exception.DuplicateResourceException;
import ma.enset.adria.stock.exception.ResourceNotFoundException;
import ma.enset.adria.stock.mapper.StockMapper;
import ma.enset.adria.stock.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        String name = request.name().trim();
        String symbol = request.symbol().trim().toUpperCase();
        if (companyRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("A company named '" + name + "' already exists");
        }
        if (companyRepository.existsBySymbolIgnoreCase(symbol)) {
            throw new DuplicateResourceException("A company with symbol '" + symbol + "' already exists");
        }
        return StockMapper.toResponse(companyRepository.save(new Company(name, symbol)));
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll().stream().map(StockMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(Long id) {
        return companyRepository.findById(id)
                .map(StockMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }
}
