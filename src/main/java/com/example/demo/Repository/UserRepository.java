package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.board;
import com.example.demo.model.user_schema;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<user_schema, Long> {

    @Modifying
    @Transactional
    @Query("update user_schema s set s.auth=1 where s.id=?1")
    void Authuser(@Param("id")String id);

    user_schema findByid(@Param("id")String username);

}
