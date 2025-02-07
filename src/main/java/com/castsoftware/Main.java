package com.castsoftware;

import com.castsoftware.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
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
                    .configure() // configures settings from hibernate.cfg.xml
                    .setProperty("hibernate.connection.username", dbUsername)
                    .setProperty("hibernate.connection.password", dbPassword)
                    .buildSessionFactory();

            // Create and save multiple users for testing
            createSampleUsers();
            
            // Retrieve and display users (efficient way)
            retrieveAndDisplayUsers();
            
            // Retrieve and display users one by one (inefficient way)
            retrieveUsersOneByOne();

        } catch (Exception e) {
            logger.error("Error in main application: ", e);
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    private static boolean userExists(String email) {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
            Long count = session.createQuery(query, Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    private static void createSampleUsers() {
        // Sample users data
        String[][] usersData = {
            {"John", "Doe", "john.doe@example.com"},
            {"Jane", "Smith", "jane.smith@example.com"}
        };

        for (String[] userData : usersData) {
            if (!userExists(userData[2])) {
                User user = new User(userData[0], userData[1], userData[2]);
                saveUser(user);
                logger.info("Created new user: {}", user);
            } else {
                logger.info("User with email {} already exists, skipping creation", userData[2]);
            }
        }
    }

    private static void saveUser(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User saved successfully: {}", user);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving user: ", e);
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    private static User getUser(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                logger.info("Retrieved user by ID {}: {}", id, user);
            } else {
                logger.warn("No user found with ID: {}", id);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: ", id, e);
            return null;
        }
    }

    private static void retrieveAndDisplayUsers() {
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("from User", User.class)
                    .list()
                    .forEach(user -> logger.info("Retrieved user: {}", user));
        } catch (Exception e) {
            logger.error("Error retrieving users: ", e);
        }
    }

    private static void retrieveUsersOneByOne() {
        try (Session session = sessionFactory.openSession()) {
            // First, get total count of users
            String countQuery = "SELECT COUNT(*) FROM demo.users";
            Long totalUsers = session.createNativeQuery(countQuery, Long.class)
                    .getSingleResult();
            
            logger.info("Total number of users to retrieve: {}", totalUsers);
            
            // Retrieve users one by one using direct SQL
            for (long i = 1; i <= totalUsers; i++) {
                String sql = "SELECT * FROM demo.users WHERE id = :userId";
                User user = session.createNativeQuery(sql, User.class)
                        .setParameter("userId", i)
                        .uniqueResult();
                
                if (user != null) {
                    logger.info("Retrieved user #{}: {}", i, user);
                }
            }
            
            logger.info("Completed retrieving all users one by one");
        } catch (Exception e) {
            logger.error("Error retrieving users one by one: ", e);
        }
    }
}