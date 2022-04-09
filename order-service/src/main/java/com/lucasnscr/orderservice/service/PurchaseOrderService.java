package com.lucasnscr.orderservice.service;

import com.lucasnscr.orderservice.entity.PurchaseOrder;
import com.lucasnscr.orderservice.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public List<PurchaseOrder> getPurchaseOrder(){
        Iterable<PurchaseOrder> all = this.purchaseOrderRepository.findAll();
        List<PurchaseOrder> purchaseOrders = StreamSupport
                .stream(all.spliterator(), false)
                .collect(Collectors.toList());
        return purchaseOrders;
    }

    public void createPurchaseOrder(PurchaseOrder purchaseOrder){
        this.purchaseOrderRepository.save(purchaseOrder);
    }

}
