package com.example.demo.Repository;

import com.example.demo.model.recommender_schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommenderRepository extends JpaRepository<recommender_schema,Long> {

}
