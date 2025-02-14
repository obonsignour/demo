package com.castsoftware.service;

import com.castsoftware.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final SessionFactory sessionFactory;

    public UserService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (Exception e) {
            logger.error("Error retrieving all users: ", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }

    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user != null) {
                logger.info("Retrieved user by ID {}: {}", id, user);
            } else {
                logger.warn("No user found with ID: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error retrieving user with ID {}: ", id, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }
    }

    public User save(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            if (user.getId() == null) {
                session.persist(user);
            } else {
                user = session.merge(user);
            }
            
            transaction.commit();
            logger.info("User saved successfully: {}", user);
            return user;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error saving user: ", e);
            throw new RuntimeException("Failed to save user", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deleteById(Long id) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("User deleted successfully: {}", user);
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Error deleting user with ID {}: ", id, e);
            throw new RuntimeException("Failed to delete user", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public boolean existsByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
            Long count = session.createQuery(query, Long.class)
                    .setParameter("email", email)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public List<User> retrieveAndDisplayUsers() {
        List<User> users = findAll();
        users.forEach(user -> logger.info("Retrieved user: {}", user));
        return users;
    }

    public List<User> retrieveUsersOneByOne() {
        List<User> users = findAll();
        logger.info("Total number of users to retrieve: {}", users.size());
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            logger.info("Retrieved user #{}: {}", i + 1, user);
        }
        
        logger.info("Completed retrieving all users one by one");
        return users;
    }

    public void createSampleUsers() {
        String[][] usersData = {
            {"John", "Doe", "john.doe@example.com"},
            {"Jane", "Smith", "jane.smith@example.com"}
        };

        for (String[] userData : usersData) {
            if (!existsByEmail(userData[2])) {
                User user = new User(userData[0], userData[1], userData[2]);
                save(user);
                logger.info("Created new user: {}", user);
            } else {
                logger.info("User with email {} already exists, skipping creation", userData[2]);
            }
        }
    }
}