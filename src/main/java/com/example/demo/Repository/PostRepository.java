package com.example.demo.Repository;

import com.example.demo.model.post_schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<post_schema, Long> {

    List<post_schema> findAll();

    post_schema findByid(@Param("id")int id);

    void deleteByid(@Param("id")String id);


}