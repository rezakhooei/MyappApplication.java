package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <Users,Integer>{


    Optional<Users> findByEmail(String email);
}
