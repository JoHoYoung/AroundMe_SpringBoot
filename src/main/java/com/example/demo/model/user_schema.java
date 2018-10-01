package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class user_schema {

    @Id
    @Column(name ="id")
    String id;

    @Column(name ="nickname")
    String nickname;

    @Column(name ="password")
    String password;

    @Column(name="name")
    String name;

    @Column(name="age")
    int age;

    @Column(name="createdat")
    String created_at;

    @Column(name="sex")
    String sex;

    @Column(name="birth")
    String birth;

    @Column(name="phone")
    String phone;

    @Column(name="tokken")
    String tokken;

    @Column(name="auth")
    int auth;

    @Column(name="email")
    String email;

    public user_schema()
    {
        this.id="kk";
    }

    public user_schema(String id,String password,String name,int age,String create_at,String sex,String birth,String phone,String tokken,int auth,String email,String nickname)
    {
        this.id=id;
        this.password=password;
        this.name=name;
        this.age=age;
        this.created_at=create_at;
        this.sex=sex;
        this.birth=birth;
        this.phone=phone;
        this.tokken=tokken;
        this.auth=auth;
        this.email=email;
        this.nickname=nickname;
    }

    public String getPassword()
    {
        return password;
    }

    public String getNickname()
    {
        return nickname;
    }


    public int getAuth()
    {
        return auth;
    }

    public String getTokken()
    {
        return tokken;
    }


}
