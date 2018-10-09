package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/**");
        web.ignoring().antMatchers("/resources/**");

    }
    @Bean
    public BCryptPasswordEncoder BCryptPasswordEncoder(){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
    @Bean
    public FacebookConnectionFactory connectionFactory()
    {
        FacebookConnectionFactory beanfact = new FacebookConnectionFactory("982849978562138","669c4090e46462b987db27c1153d81b8");
        return beanfact;
    }

    @Bean
    public OAuth2Parameters oAuth2Parameters()
    {
        OAuth2Parameters auth=new OAuth2Parameters();
        auth.setScope("email");
        auth.setRedirectUri("https://localhost:3000/facebooklogin");
        return auth;
    }

//     <bean id="connectionFactory"
//    class="org.springframework.social.facebook.connect.FacebookConnectionFactory">
//        <constructor-arg value="Client ID" />
//        <constructor-arg value="Secret Code" />
//    </bean>
//
//    <bean id="oAuth2Parameters"
//    class="org.springframework.social.oauth2.OAuth2Parameters">
//        <property name="scope" value="email" />
//        <property name="redirectUri" value="https://localhost:8443/Path..." />
//    </bean>


}
