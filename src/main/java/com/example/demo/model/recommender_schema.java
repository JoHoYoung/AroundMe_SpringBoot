package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter @Getter
public class recommender_schema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @ManyToOne(targetEntity = post_schema.class,fetch = FetchType.LAZY)
    post_schema post;

    @Column(name="user")
    String user;

    @Column(name="belongto")
    int belongto;

    public recommender_schema(String user,int belongto,post_schema post)
    {
        this.user=user;
        this.belongto=belongto;
        this.post=post;
    }


}
