package com.example.demo.Repository;

import com.example.demo.model.image_schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<image_schema, Long> {

}
