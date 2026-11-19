
# Java JDBC Order Management System

A console-based Order Management System built using Java, JDBC, and PostgreSQL.

## Features

- Connects Java to PostgreSQL using JDBC.
- Stores and manages customer orders.
- Uses SQL to interact with the database.
- Organizes the project using Maven.

## Technologies Used

- Java
- JDBC
- PostgreSQL
- Maven
- SQL

## Prerequisites

- Java JDK
- PostgreSQL
- Maven (or the included Maven Wrapper)

## Setup

1. Create a PostgreSQL database.
2. Create the required `Orders` table.
3. Update the database URL, username, and password in the Java source file.
4. Compile the project:

   ```bash
   .\mvnw.cmd clean compile
   ```

5. Run the `OrderManagementSystem` class from your IDE.

## Security Note

Do not commit real database passwords or other secrets to GitHub.
