package com.galip.brokerapp.service;

import com.galip.brokerapp.model.*;
import com.galip.brokerapp.repository.AssetRepository;
import com.galip.brokerapp.repository.CustomerRepository;
import com.galip.brokerapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetService assetService;

    public Order createOrder(Order order) {
        //Check if customer exist
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        // check TRY asset
        if (order.getOrderSide() == OrderSide.BUY) {
            var totalCost = order.getPrice().multiply(order.getSize());
            if (customer.getBalance().compareTo(totalCost) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            // update customer's TRY asset
            customer.setBalance(customer.getBalance().subtract(totalCost));
            customerRepository.save(customer);

            order.setStatus(OrderStatus.PENDING);
            order.setCreateDate(LocalDateTime.now());
            return orderRepository.save(order);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            // check if customer has asset
            Asset asset = assetRepository.findByCustomerId(order.getCustomerId()).stream()
                    .filter(a -> a.getAssetName().equals(order.getAssetName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Asset not found"));
            if (asset.getUsableSize().compareTo(order.getSize()) < 0) {
                throw new RuntimeException("Insufficient asset size");
            }
            // update remaining asset
            asset.setUsableSize(asset.getUsableSize().subtract(order.getSize()));
            assetRepository.save(asset);

            order.setStatus(OrderStatus.PENDING);
            order.setCreateDate(LocalDateTime.now());
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Invalid order side");
        }
    }

    public List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be canceled");
        }
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        // update customer's TRY asset
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (order.getOrderSide() == OrderSide.BUY) {
            var totalCost = order.getPrice().multiply(order.getSize());
            customer.setBalance(customer.getBalance().add(totalCost));
            customerRepository.save(customer);
        } else if (order.getOrderSide() == OrderSide.SELL) {
            Asset asset = assetRepository.findByCustomerId(order.getCustomerId()).stream()
                    .filter(a -> a.getAssetName().equals(order.getAssetName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Asset not found"));

            asset.setUsableSize(asset.getUsableSize().add(order.getSize()));
            assetRepository.save(asset);
        }
    }

    public void matchPendingOrders() {
        List<Order> pendingBuyOrders =
                orderRepository.findByStatusAndOrderSide(OrderStatus.PENDING, OrderSide.BUY);
        List<Order> pendingSellOrders =
                orderRepository.findByStatusAndOrderSide(OrderStatus.PENDING, OrderSide.SELL);

        for (Order buyOrder : pendingBuyOrders) {
            for (Order sellOrder : pendingSellOrders) {
                if (buyOrder.getAssetName().equals(sellOrder.getAssetName()) &&
                        buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0 &&
                        buyOrder.getSize().compareTo(sellOrder.getSize()) == 0) {

                    // find buyer and seller
                    Customer buyer = customerRepository.findById(buyOrder.getCustomerId()).orElseThrow();
                    Customer seller = customerRepository.findById(sellOrder.getCustomerId()).orElseThrow();

                    BigDecimal transactionAmount = buyOrder.getPrice().multiply(buyOrder.getSize());

                    // remove from buyers balance
                    buyer.setBalance(buyer.getBalance().subtract(transactionAmount));

                    // add to sellers balance
                    seller.setBalance(seller.getBalance().add(transactionAmount));

                    // update assets
                    assetService.addAsset(buyer, buyOrder.getAssetName(), buyOrder.getSize());
                    assetService.subtractAsset(seller, sellOrder.getAssetName(), sellOrder.getSize());

                    buyOrder.setStatus(OrderStatus.MATCHED);
                    sellOrder.setStatus(OrderStatus.MATCHED);

                    customerRepository.save(buyer);
                    customerRepository.save(seller);
                    orderRepository.save(buyOrder);
                    orderRepository.save(sellOrder);

                    pendingSellOrders.remove(sellOrder);
                    break;
                }
            }
        }
    }

    public List<Asset> listAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    public void depositMoney(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setBalance(customer.getBalance().add(amount));
        customerRepository.save(customer);
    }

    public void withdrawMoney(Long customerId, BigDecimal amount, String iban) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        if(customer.getIban().isEmpty()){
            throw new RuntimeException("Invalid iban");
        }
        customer.setBalance(customer.getBalance().subtract(amount));
        customerRepository.save(customer);
    }

}
