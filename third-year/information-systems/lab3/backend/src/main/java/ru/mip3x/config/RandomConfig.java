package ru.mip3x.config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RandomConfig {

    @Bean
    public Random appRandom(@Value("${app.seed:#{null}}") Long seed) {
        return seed == null ? new Random() : new Random(seed);
    }
}
