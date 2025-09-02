package com.bi.assistant.repository;

import com.bi.assistant.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(s.revenue) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDate startDate, 
                                         @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM Sale s WHERE s.product.category = :category AND s.saleDate BETWEEN :startDate AND :endDate")
    List<Sale> findByCategoryAndDateRange(@Param("category") String category,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT DISTINCT s.region FROM Sale s WHERE s.region IS NOT NULL ORDER BY s.region")
    List<String> findAllRegions();
}
