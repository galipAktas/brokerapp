package com.galip.brokerapp.controller;

import com.galip.brokerapp.model.Asset;
import com.galip.brokerapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Asset>> listAssets(@RequestParam Long customerId) {
        return ResponseEntity.ok(orderService.listAssets(customerId));
    }

}
