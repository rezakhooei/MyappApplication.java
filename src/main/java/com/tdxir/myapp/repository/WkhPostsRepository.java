package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.WkhPosts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WkhPostsRepository extends JpaRepository<WkhPosts, Long> {
}
