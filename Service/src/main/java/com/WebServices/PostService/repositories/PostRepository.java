package com.WebServices.PostService.repositories;

import com.WebServices.PostService.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>{
}