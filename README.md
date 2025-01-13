# Project

## Tech Stack

This project is built using the following technologies:

### Languages and Frameworks
- Java
- Spring Boot

### Core Dependencies
- Spring Data JDBC
- Spring Data JPA
- Spring Web
- Spring Validation

### Security
- Spring Security
- JWT

### Testing
- Spring Boot Test
- Spring Test
- JUnit 5

### Database
- Liquibase
- PostgreSQL

### Development Tools
- Springdoc OpenAPI
- Lombok

## How to Run the Application

1. Clone the repository:

    ```bash
    git clone https://github.com/ravshanbekn/order-service.git
    ```

2. Navigate to the project directory:

    ```bash
    cd <project-directory>
    ```

3. Build the project:

    ```bash
    gradlew clean build
    ```

4. Run the application using Docker Compose:

    ```bash
    docker-compose up
    ```

## How to Use the API

1. **Access Swagger UI**  
   Open the Swagger UI by navigating to the following URL in your browser:  
   `http://localhost:8080/swagger-ui/`

2. **Register a User**  
   In Swagger UI, use the `/register` endpoint to create a new user. Provide the necessary details such as username, password.

3. **Login to Get JWT**  
   After registering, use the `/login` endpoint to authenticate the user. Upon successful authentication, you will receive a JSON Web Token (JWT).

4. **Authorize Using JWT**  
   In the Swagger UI, click on the "Authorize" button. A dialog will appear where you can enter the JWT token you received in the login step. Paste the token into the input field and click "Authorize."

5. **Access Admin Role (Optional)**  
   If needed, you can obtain an admin role by using the `/admin` endpoint.

6. **Use Other APIs**  
   You can now explore and use other available API endpoints in the Swagger UI.