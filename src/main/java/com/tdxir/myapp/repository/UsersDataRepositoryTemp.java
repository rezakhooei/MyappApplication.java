package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.UsersData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersDataRepositoryTemp extends JpaRepository<UsersData,Long> {


    //public interface MyEntity extends JpaRepository {//PagingAndSortingRepository {
        //  List findByFieldIn(Set myField);

        List<UsersData> findByUserid(String userid);

}
