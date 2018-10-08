package com.example.demo.Repository;

import com.example.demo.model.comment_schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<comment_schema,Long> {

    comment_schema findById(@Param("id")int id);


}
