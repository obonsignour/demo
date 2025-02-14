package com.castsoftware;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import jakarta.annotation.PreDestroy;
import java.net.URL;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        initializeHibernate();
        SpringApplication.run(Main.class, args);
    }

    private static void initializeHibernate() {
        try {
            // Debug: Print classpath resource
            URL cfgUrl = Main.class.getClassLoader().getResource("hibernate.cfg.xml");
            URL propUrl = Main.class.getClassLoader().getResource("hibernate.properties");
            logger.info("hibernate.cfg.xml found at: {}", cfgUrl);
            logger.info("hibernate.properties found at: {}", propUrl);

            // Set database credentials from environment variables
            String dbUsername = System.getenv("DB_USERNAME");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            if (dbUsername == null || dbPassword == null) {
                throw new RuntimeException("Database credentials not found in environment variables");
            }
            
            // Create the SessionFactory
            sessionFactory = new Configuration()
                    .configure()
                    .setProperty("hibernate.connection.username", dbUsername)
                    .setProperty("hibernate.connection.password", dbPassword)
                    .buildSessionFactory();

        } catch (Exception e) {
            logger.error("Error initializing Hibernate: ", e);
            throw new RuntimeException("Failed to initialize Hibernate", e);
        }
    }

    @Bean
    public SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @PreDestroy
    public void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}