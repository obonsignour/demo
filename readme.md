# Hibernate Demo Application

A simple Java application demonstrating Hibernate usage with PostgreSQL database.

## Prerequisites

- Java 21
- PostgreSQL (running on port 2284)
- Gradle (or use the included Gradle wrapper)

## Database Setup

1. Create database schema:
```sql
CREATE SCHEMA demo;
```

2. Create the users table:
```sql
CREATE TABLE demo.users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT users_email_key UNIQUE (email)
);
```

## Configuration

1. Create a set-env.bat file with your database credentials:
```batch
set DB_USERNAME=your_username
set DB_PASSWORD=your_password
```

2. Run the environment setup:
```batch
set-env.bat
```

## Building the Application

Using Gradle wrapper:
```batch
gradlew.bat clean build
```

## Running the Application

```batch
gradlew.bat run
```

## Features

- User entity management with Hibernate
- Demonstrates both efficient and inefficient database querying methods
- Email uniqueness validation
- Proper transaction management
- Configurable logging levels

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── castsoftware/
│   │           ├── Main.java
│   │           └── entity/
│   │               └── User.java
│   └── resources/
│       ├── hibernate.cfg.xml
│       ├── hibernate.properties
│       └── logback.xml
```