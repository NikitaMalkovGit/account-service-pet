package com.pet.accountservice.config;

import com.pet.accountservice.model.event.Action;
import com.pet.accountservice.model.user.User;
import com.pet.accountservice.service.EventService;
import com.pet.accountservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import static com.pet.accountservice.model.user.Role.*;

@Configuration
@EnableWebSecurity(debug = false)
@AllArgsConstructor
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final RestAuthEntryPoint restAuthEntryPoint;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EventService eventService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(restAuthEntryPoint)
                .and()
                .csrf()
                .disable()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .authorizeRequests()
                // AdminController
                .antMatchers("/api/admin/**")
                .hasAnyRole(ADMINISTRATOR.name())
                // AuthController
                .antMatchers(HttpMethod.POST, "/api/auth/signup")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/auth/changepass")
                .hasAnyRole(USER.name(), ACCOUNTANT.name(), ADMINISTRATOR.name())
                .antMatchers(HttpMethod.GET, "/api/auth/login")
                .authenticated()
                // BusinessController
                .antMatchers(HttpMethod.GET, "/api/empl/payment")
                .hasAnyRole(USER.name(), ACCOUNTANT.name())
                .antMatchers(HttpMethod.POST, "/api/acct/payments")
                .hasAnyRole(ACCOUNTANT.name())
                .antMatchers(HttpMethod.PUT, "/api/acct/payments")
                .hasAnyRole(ACCOUNTANT.name())
                // SecurityController
                .antMatchers("/api/security/**")
                .hasAnyRole(AUDITOR.name())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            eventService.log(Action.ACCESS_DENIED, user);
            response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
        };
    }

}
