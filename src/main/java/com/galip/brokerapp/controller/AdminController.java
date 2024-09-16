package com.galip.brokerapp.controller;

import com.galip.brokerapp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/match-orders")
    public ResponseEntity<String> matchOrders() {
        orderService.matchPendingOrders();
        return ResponseEntity.ok("Pending orders matched successfully");
    }

}
