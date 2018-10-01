package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.board;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<board, Long> {

    @Query("select u from board u where u.username = :myname")
    List<board> customfunc(@Param("myname") String name);

    board findByusername(@Param("username")String username);
}
