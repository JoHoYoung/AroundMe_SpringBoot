package com.example.demo.Repository;

import com.example.demo.model.image_schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
public interface ImageRepository extends JpaRepository<image_schema, Long> {

    image_schema findByimagename(@Param("imagename")String imagename);

    void deleteByImagename(@Param("imagename")String imagename);

}
