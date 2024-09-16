package com.galip.brokerapp.repository;

import com.galip.brokerapp.model.Asset;
import com.galip.brokerapp.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCustomerId(Long customerId);

    Optional<Asset> findByCustomerAndAssetName(Customer customer, String assetName);

}
