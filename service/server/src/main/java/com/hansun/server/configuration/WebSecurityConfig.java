package com.hansun.server.configuration;

import com.hansun.server.filter.JwtAuthenticationTokenFilter;
import com.hansun.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


/**
 * Created by yuanl2 on 2017/4/23.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userService;

    @Bean
    public PasswordEncoder passwordEncoder() throws Exception {
        return new Md5PasswordEncoder();
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // 设置UserDetailsService
                .userDetailsService(this.userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http              // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
                .authorizeRequests().
                antMatchers("/ui/**", "/index", "/index/**", "/iview-admin/**","/device", "/device/**", "/callback", "/callback/**", "/assets/**",
                        "/callback/**", "/js/**", "/css/**", "/pic/**", "/images/**", "/api/deviceStatus", "/api/deviceStatus/**",
                        "/detail", "/detail/**", "/disable", "/disable/**", "/testcmd", "/testcmd/**", "/testdevice", "/testdevice/**",
                        "/finish", "/finish/**", "/weixin/savepackage", "/weixin/savepackage/**", "/weixin/payNotify", "/paysuccess", "/paysuccess/**", "/weixin/paycancel", "/weixin/paycancel/**").permitAll()
               .anyRequest().authenticated()
                .antMatchers("/monitor/**").hasAuthority("admin");
//                .and().formLogin().loginPage("/")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll()
//                .and().httpBasic();
        http.csrf().disable();
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

}
