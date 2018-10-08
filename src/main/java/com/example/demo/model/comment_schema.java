package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter @Getter
public class comment_schema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name="content")
    String content;

    @Column(name="writer")
    String writer;

    @Column(name="createdat")
    String createdat;

    int belongto;

    public comment_schema(String content,String writer,String createdat)
    {
        this.content=content;
        this.writer=writer;
        this.createdat=createdat;
    }

}
