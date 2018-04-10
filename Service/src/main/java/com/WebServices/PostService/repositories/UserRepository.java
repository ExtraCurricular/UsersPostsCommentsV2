package com.WebServices.PostService.repositories;

import com.WebServices.PostService.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findByEmail(String email);
    List<User> findByUsername(String username);
}
