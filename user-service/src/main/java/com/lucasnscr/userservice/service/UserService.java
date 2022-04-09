package com.lucasnscr.userservice.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.lucasnscr.userservice.dto.UserDto;
import com.lucasnscr.userservice.entity.Users;
import com.lucasnscr.userservice.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {


    private UsersRepository usersRepository;
    private ObjectMapper objectMapper;
    private QueueMessagingTemplate queueMessagingTemplate;


    @Autowired
    public UserService(UsersRepository usersRepository, ObjectMapper objectMapper, AmazonSQSAsync amazonSQSAsyn){
        this.usersRepository = usersRepository;
        this.objectMapper = objectMapper;
        this.queueMessagingTemplate = new QueueMessagingTemplate(amazonSQSAsyn);
    }

    public String createUser(UserDto userDto){
        Users user = new Users();
        user.setEmail(user.getEmail());
        return this.usersRepository.save(user).getId();
    }

    @Transactional
    public void updateUser(UserDto userDto){
        this.usersRepository.findById(userDto.getId())
                .ifPresent(user -> {
                    user.setEmail(userDto.getEmail());
                    this.event(userDto);
                });
    }

    private void event(UserDto userDto){
        String userDtoJson = new Gson().toJson(userDto);
        try {
            this.queueMessagingTemplate.send("user-service-event", MessageBuilder.withPayload(userDtoJson).build());
        }catch (Exception e){
            log.error("Exception ocurred while pushing event to sqs : {} and stacktrace ; {}", e.getMessage(), e);
        }
    }
}
