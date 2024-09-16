package com.galip.brokerapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String assetName;
    private BigDecimal size;
    private BigDecimal usableSize;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
