package com.galip.brokerapp.service;

import com.galip.brokerapp.model.Asset;
import com.galip.brokerapp.model.Customer;
import com.galip.brokerapp.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public void addAsset(Customer customer, String assetName, BigDecimal size) {
        Asset asset = assetRepository.findByCustomerAndAssetName(customer, assetName)
                .orElse(new Asset());

        asset.setCustomer(customer);
        asset.setAssetName(assetName);
        asset.setSize(asset.getSize() == null ? size : asset.getSize().add(size));

        assetRepository.save(asset);
    }

    public void subtractAsset(Customer customer, String assetName, BigDecimal size) {
        Asset asset = assetRepository.findByCustomerAndAssetName(customer, assetName)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        asset.setSize(asset.getSize().subtract(size));

        if (asset.getSize().compareTo(BigDecimal.ZERO) <= 0) {
            assetRepository.delete(asset);
        } else {
            assetRepository.save(asset);
        }
    }

}
