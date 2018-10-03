package com.example.demo.model;

import javax.persistence.*;

public class comment_schema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @ManyToOne(targetEntity = post_schema.class,fetch = FetchType.LAZY)
    post_schema post;

    @Column(name="content")
    String content;

    @Column(name="writer")
    String writer;

    @Column(name="created_at")
    String createdat;

    @Column(name="belongto")
    int belongto;

    public comment_schema(String content,String writer,String createdat,int belongto,post_schema post)
    {
        this.content=content;
        this.writer=writer;
        this.createdat=createdat;
        this.belongto=belongto;
        this.post=post;
    }

}
