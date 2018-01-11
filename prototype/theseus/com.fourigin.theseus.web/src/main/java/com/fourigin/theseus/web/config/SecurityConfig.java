//package com.fourigin.apps.theseus.prototype.config;
//
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
////@Configuration
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .authorizeRequests().anyRequest().authenticated();
//        http
//            .formLogin()
//                .failureUrl("/login?error")
//                .defaultSuccessUrl("/")
//                .loginPage("/login")
//                .permitAll()
//            .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login")
//            .permitAll();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.authenticationProvider(authenticationProvider());
//
//        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
//    }
//
//    private AuthenticationProvider authenticationProvider() {
//        return null;
//    }
//
//
//}
