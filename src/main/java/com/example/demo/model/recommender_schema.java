package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter @Getter
public class recommender_schema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name="user")
    String user;

    int belongto;

    public recommender_schema(String user)
    {
        this.user=user;
    }


}
