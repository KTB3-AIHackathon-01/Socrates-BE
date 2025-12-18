package com.socrates.app.webflux.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.socrates.app.webflux.chat.repository")
@EnableReactiveMongoAuditing
public class MongoConfig {
}
