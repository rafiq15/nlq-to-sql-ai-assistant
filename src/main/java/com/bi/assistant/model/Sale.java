package com.bi.assistant.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;
    
    @Column(name = "revenue", nullable = false, precision = 10, scale = 2)
    private BigDecimal revenue;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "customer_id")
    private Long customerId;
    
    @Column(name = "region")
    private String region;
    
    @Column(name = "sales_person")
    private String salesPerson;
}
