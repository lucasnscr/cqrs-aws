package com.lucasnscr.userservice.repository;

import com.lucasnscr.userservice.entity.Users;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface UsersRepository extends CrudRepository<Users, String> {
}
