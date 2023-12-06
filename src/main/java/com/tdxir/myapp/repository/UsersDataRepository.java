package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.Users;
import com.tdxir.myapp.model.UsersData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsersDataRepository extends JpaRepository<UsersData,Long> {




}
