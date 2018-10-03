package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter @Getter
public class post_schema {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToMany(mappedBy = "post_schema")
    @JoinColumn(name="belongto")
    private List<comment_schema> comment;

    @OneToMany(mappedBy = "post_schema")
    @JoinColumn(name="belongto")
    private List<image_schema> image;

    @OneToMany(mappedBy = "post_schema")
    @JoinColumn(name="belongto")
    private List<recommender_schema> recommender;


    @Column(name="title")
    String title;

    @Column(name="content")
    String content;

    @Column(name="writer")
    String writer;

    @Column(name="star")
    int star;

    @Column(name="createdat")
    String createat;

    @Column(name="updatedat")
    String updateat;

    @Column(name="commentcount")
    int commentcount;

    @Column(name="views")
    int views;

    @Column(name="area")
    int area;

    @Column(name="areagourp")
    int areagroup;

    public post_schema(String title,String content,String writer,int star,String createat,String updateat,int commentcount,int views,int area,int areagroup)
    {
        this.title=title;
        this.content=content;
        this.writer=writer;
        this.star=star;
        this.createat=createat;
        this.updateat=updateat;
        this.commentcount=commentcount;
        this.views=views;
        this.area=area;
        this.areagroup=areagroup;

    }
    public void addComment(comment_schema data){
        if( comment == null ){
            comment = new ArrayList<comment_schema>();
        }
        comment.add(data);
    }

    public void addImage(image_schema data){
        if( image == null ){
            image = new ArrayList<image_schema>();
        }
        image.add(data);
    }

    public void addRecommender(recommender_schema data){
        if( recommender == null ){
            recommender = new ArrayList<recommender_schema>();
        }
        recommender.add(data);
    }


}
