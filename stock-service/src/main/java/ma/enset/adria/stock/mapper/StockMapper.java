package ma.enset.adria.stock.mapper;

import ma.enset.adria.stock.dto.CompanyResponse;
import ma.enset.adria.stock.dto.StockMarketRequest;
import ma.enset.adria.stock.dto.StockMarketResponse;
import ma.enset.adria.stock.entity.Company;
import ma.enset.adria.stock.entity.StockMarket;

/** Entity/DTO conversions, kept in one place to avoid duplication. */
public final class StockMapper {

    private StockMapper() {
    }

    public static StockMarket toEntity(StockMarketRequest request) {
        return new StockMarket(request.date(), request.openValue(), request.closeValue(),
                request.volume(), request.companyId());
    }

    public static StockMarketResponse toResponse(StockMarket entity) {
        return new StockMarketResponse(entity.getId(), entity.getDate(), entity.getOpenValue(),
                entity.getCloseValue(), entity.getVolume(), entity.getCompanyId());
    }

    public static CompanyResponse toResponse(Company company) {
        return new CompanyResponse(company.getId(), company.getName(), company.getSymbol(),
                company.getCurrentPrice(), company.getPriceUpdatedAt());
    }
}
