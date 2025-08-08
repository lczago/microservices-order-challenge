package com.zago.service.infrastructure.config.dependency;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.zago.domain.order"})
@EnableJpaRepositories(basePackages = {"com.zago.domain.order"})
@ComponentScan(basePackages = {"com.zago.domain", "com.zago.service"})
public class DomainConfig {
}