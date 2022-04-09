package com.lucasnscr.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lucasnscr.orderservice.dto.UserDto;
import com.lucasnscr.orderservice.entity.PurchaseOrder;
import com.lucasnscr.orderservice.entity.User;
import com.lucasnscr.orderservice.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceEventHandler {

    private static final ObjectMapper OBJECT_MAPPER = Jackson2ObjectMapperBuilder.json().build();

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @SqsListener(value = "user-service-event")
    public void consume(String userDtoJson){
        try{
            Gson g = new Gson();
            UserDto userDto = g.fromJson(userDtoJson, UserDto.class);
            User user = this.parseUserDtoInUser(userDto);
            this.updateUser(user);
        }catch (Exception e){
            throw new RuntimeException("Cannot process message from SQS", e);
        }
    }

    @Transactional
    public void updateUser(User user){
        List<PurchaseOrder> userOrders = this.purchaseOrderRepository.findByUserId(user.getId());
        userOrders.forEach(userF -> userF.setUser(user));
        this.purchaseOrderRepository.saveAll(userOrders);
    }

    private User parseUserDtoInUser(UserDto userDto){
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(user.getEmail());
        return user;
    }

}
