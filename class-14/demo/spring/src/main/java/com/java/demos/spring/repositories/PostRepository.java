package com.java.demos.spring.repositories;

import com.java.demos.spring.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAppUser_Username(String name);
    void deletePostByAppUser_UsernameAndId(String username, Long id);
}