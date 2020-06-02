package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeRequests()

//                .antMatchers("/login").access("hasAnyAuthority('USER','ADMIN')")
//                .antMatchers("/admin").access("hasAnyAuthority('ADMIN')")
// "/" = index
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/**").hasAnyRole( "ADMIN", "USER")

                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true").permitAll();
    }
    @Autowired
    private DataSource dataSource;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
                .withUser(User.withUsername("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles("ADMIN"))
                .withUser(User.withUsername("user")
                     .password(passwordEncoder().encode("user"))
                     .roles("USER"));

    }
}
