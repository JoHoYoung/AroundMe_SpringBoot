//package com.example.demo.model;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//
//@Entity
//@Setter @Getter
//public class comment_schema {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    int id;
//
////    @ManyToOne(targetEntity = post_schema.class,fetch = FetchType.LAZY)
////    post_schema post;
//
//    @Column(name="content")
//    String content;
//
//    @Column(name="writer")
//    String writer;
//
//    @Column(name="created_at")
//    String createdat;
//
//    @Column(name="id")
//    int belongto;
//
//    public comment_schema(String content,String writer,String createdat,int belongto)
//    {
//        this.content=content;
//        this.writer=writer;
//        this.createdat=createdat;
//        this.belongto=belongto;
//       // this.post=post;
//    }
//
//}
