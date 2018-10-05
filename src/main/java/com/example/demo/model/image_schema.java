package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter @Getter
public class image_schema {

    @Id
    @Column(name="imagename")
    String imagename;

//    @ManyToOne(targetEntity = post_schema.class,fetch = FetchType.LAZY)
//    @JoinColumn(name="id")
//    post_schema post;

    //@Column(name="belongto")
//    @ManyToOne(targetEntity = post_schema.class,fetch = FetchType.LAZY)
//    @JoinColumn(referencedColumnName = "id",name="belongto")

    int belongto;

    public image_schema()
    {
        this.imagename="kk";
    }

    public image_schema(String imagename,int belongto)
    {
        this.imagename=imagename;
        //this.post=post;
        this.belongto=belongto;

    }

}
