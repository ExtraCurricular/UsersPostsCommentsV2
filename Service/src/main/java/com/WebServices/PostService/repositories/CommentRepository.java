package com.WebServices.PostService.repositories;

import com.WebServices.PostService.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{
}