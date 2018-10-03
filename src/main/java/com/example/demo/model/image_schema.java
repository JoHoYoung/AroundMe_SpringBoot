package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Setter @Getter
public class image_schema {

    @Id
    String imagename;

    @ManyToOne(targetEntity = post_schema.class,fetch = FetchType.LAZY)
    post_schema post;

    @Column(name="belongto")
    int belongto;

    public image_schema(String imagename,int belongto,post_schema post)
    {
        this.imagename=imagename;
        this.post=post;
        this.belongto=belongto;

    }

}
