package com.example.demo.Repository;

import com.example.demo.model.recommender_schema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommenderRepository extends JpaRepository<recommender_schema,Long> {

    recommender_schema findByBelongtoAndUser(@Param("belongto")int belongto,@Param("user")String user);
}
