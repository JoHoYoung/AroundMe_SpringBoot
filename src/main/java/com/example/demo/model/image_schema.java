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


    int belongto;

    public image_schema()
    {
        this.imagename="kk";
    }

    public image_schema(String imagename)
    {
        this.imagename=imagename;

    }

}
