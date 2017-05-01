/*
 * Copyright (C) 2017 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Cloud Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.configclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Security related configuration of the Config Client Vault application.
 *
 * @author Dominik Schadow
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Limits access to admin pages to admin and actuator roles, disables CSRF protection for easy Swagger UI usage
     * and enables frames on same origin for h2 console usage.
     *
     * @param http HttpSecurity
     * @throws Exception Any exception during configuration
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .authorizeRequests()
                .mvcMatchers("/admin/**").hasAnyRole("ADMIN", "ACTUATOR")
                .mvcMatchers("/").permitAll()
                .and()
            .csrf()
                .disable()
            .headers()
                .frameOptions().sameOrigin()
                .and()
            .formLogin();
        // @formatter:on
    }

    /**
     * Creates an in memory user named user with password user and roles admin and actuator.
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception Any exception during configuration
     */
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("user").roles("ADMIN", "ACTUATOR");
    }
}
