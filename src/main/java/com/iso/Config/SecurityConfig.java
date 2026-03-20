package com.iso.Config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.iso.Service.UserDetailsServiceImpl;




@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/vendor/**", "/verify-otp", "/uploads/**", "/about", "/css/**", "/js/**", "/images/**",
                                 "/reset-password", "/forgot-password", "/register", "/register-organization", "/login", "/contact").permitAll()
                .requestMatchers("/admin/**").hasRole("SUPER_ADMIN")
                .requestMatchers("/hr/**").hasRole("HR")
                .requestMatchers("/account/**").hasRole("ACCOUNT")
                .requestMatchers("/sales/**").hasRole("SALES")
                .requestMatchers("/provincehead/**").hasRole("PROVINCE_HEAD")
                .requestMatchers("/consultant/**").hasRole("CONSULTANT")
                .requestMatchers("/edeo/**").hasRole("EDEO")
                .requestMatchers("/cro/**").hasRole("CRO")
                .requestMatchers("/principal/**").hasRole("PRINCIPAL")
                .requestMatchers("/coordinator/**").hasRole("COORDINATOR")
                .requestMatchers("/deo/**").hasRole("DEO")
                .requestMatchers("/educator/**").hasRole("EDUCATOR")
                .requestMatchers("/learner/**").hasRole("LEARNER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
            	    .loginPage("/login")
            	    .successHandler((request, response, authentication) -> {

            	        var user = (org.springframework.security.core.userdetails.User)
            	                authentication.getPrincipal();

            	        String role = user.getAuthorities().iterator().next().getAuthority();

            	        switch (role) {
            	            case "ROLE_SUPER_ADMIN" -> response.sendRedirect("/admin/dashboard");
            	            case "ROLE_HR" -> response.sendRedirect("/hr/dashboard");
            	            case "ROLE_ACCOUNT" -> response.sendRedirect("/account/dashboard");
            	            case "ROLE_SALES" -> response.sendRedirect("/sales/dashboard");
            	            case "ROLE_PROVINCE_HEAD" -> response.sendRedirect("/provincehead/dashboard");
            	            case "ROLE_CONSULTANT" -> response.sendRedirect("/consultant/dashboard");
            	            case "ROLE_EDEO" -> response.sendRedirect("/edeo/dashboard");
            	            case "ROLE_CRO" -> response.sendRedirect("/cro/dashboard");
            	            case "ROLE_PRINCIPAL" -> response.sendRedirect("/principal/dashboard");
            	            case "ROLE_COORDINATOR" -> response.sendRedirect("/coordinator/dashboard");
            	            case "ROLE_DEO" -> response.sendRedirect("/deo/dashboard");
            	            case "ROLE_EDUCATOR" -> response.sendRedirect("/educator/dashboard");
            	            case "ROLE_LEARNER" -> response.sendRedirect("/learner/dashboard");
            	        }
            	    })
            	)

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }
    
    
}

