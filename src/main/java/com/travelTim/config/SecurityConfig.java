package com.travelTim.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtRequestFilter jwtRequestFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable().authorizeRequests()
                .antMatchers(
                "/user", "/authenticate", "/user/register",
                        "/user/all", "/category/all", "/category/{categoryId}",
                        "/category/offers/**", "/lodging/physical/{offerId}",
                        "/food/{offerId}", "/attraction/{offerId}", "/activity/{offerId}",
                        "/image/user/{userId}", "/image/offer/{offerId}",
                        "/lodging/legal/{offerId}", "/image/business/{businessId}",
                        "/business/{businessId}/offers/lodging", "/lodging/{offerId}/price",
                        "/currency/**", "/image/business/{businessId}/front",
                        "/image/business/{businessId}/all", "/food/{offerId}/details",
                        "/attraction/{offerId}/details", "/activity/{offerId}/details",
                        "/business/{businessId}", "/business/{businessId}/offers/lodging/business-page",
                        "/business/{businessId}/offers/food", "/business/{businessId}/offers/attractions",
                        "/business/{businessId}/offers/activities", "/user/{userId}/details",
                        "/user/{userId}/offers/lodging", "/user/{userId}/offers/attractions",
                        "/user/{userId}/offers/activities", "/lodging//{offerId}/details",
                        "/lodging/legal/{offerId}/business/schedule", "/review/business/{businessId}",
                        "/review/user/{userId}", "/review/user/{userId}/rating",
                        "/review/business/{businessId}/rating", "/recommendations/lodging/business-type",
                        "/recommendations/lodging/user-type", "/recommendations/food",
                        "/recommendations/attractions/business-type", "/recommendations/attractions/user-type",
                        "/recommendations/activities/business-type", "/recommendations/activities/user-type",
                        "/lodging/price/request", "/image/cloud-test")
                .permitAll()
                .antMatchers(HttpMethod.OPTIONS,"*")
                .permitAll().anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(this.jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
