package com.co.ias.clases.ClasesWebflux.configuration;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;


@Configuration
@EnableR2dbcRepositories
public class DBConfiguration extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration
                        .builder()
                        .host("rdslist4100.czq1dhhdewlx.us-east-1.rds.amazonaws.com")
                        .port(5432)
                        .username("MichinTheCat")
                        .password("MichinTheCat")
                        .database("Clases")
                        .build()
        );
    }
}
