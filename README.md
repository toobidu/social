# 💬 Socialapp

Welcome to **Socialapp**! A modern, reactive chat application built with high-performance technologies. 🚀

## 🛠️ Tech Stack

This project leverages a powerful stack of modern technologies to ensure scalability, performance, and a great user experience.

### 🔙 Backend

| Technology          | Icon | Description                                           |
| ------------------- | ---- | ----------------------------------------------------- |
| **Java 17**         | ☕   | Core programming language.                            |
| **Spring Boot 3**   | 🍃   | Framework for building production-ready applications. |
| **Spring WebFlux**  | ⚡   | Reactive web framework for non-blocking I/O.          |
| **Cassandra**       | 🗄️   | High-performance, distributed NoSQL database.         |
| **Elasticsearch**   | 🔍   | Powerful search and analytics engine.                 |
| **Spring Security** | 🔐   | Robust authentication and access-control framework.   |
| **Maven**           | 🐘   | Build automation tool.                                |

### 🎨 Frontend

| Technology       | Icon | Description                                               |
| ---------------- | ---- | --------------------------------------------------------- |
| **Angular 19**   | 🅰️   | Component-based framework for building scalable web apps. |
| **TypeScript**   | 🟦   | Typed superset of JavaScript.                             |
| **Tailwind CSS** | 🌬️   | Utility-first CSS framework.                              |
| **Spartan**      | 🛡️   | Beautifully designed components for Angular.              |
| **FontAwesome**  | 🚩   | Icon set and toolkit.                                     |
| **Jest**         | 🃏   | Delightful JavaScript Testing Framework.                  |

### ⚙️ Tools & DevOps

- 🐳 **Docker & Docker Compose**: Containerization for consistent development and deployment environments.
- 📡 **SonarQube**: Continuous code quality inspection.
- 🎩 **JHipster**: Development platform to generate, develop, and deploy modern web applications.
- 💅 **Prettier & ESLint**: Code formatting and linting.

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine.

### Prerequisites

Ensure you have the following installed:

- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [Node.js](https://nodejs.org/) (v22+)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)

### 🏃‍♂️ Running the Application

1.  **Start Database & Services**
    Use Docker Compose to start Cassandra, Elasticsearch, and other services.

    ```bash
    docker compose -f src/main/docker/services.yml up -d
    ```

2.  **Run the Backend**
    Start the Spring Boot application.

    ```bash
    ./mvnw
    ```

3.  **Run the Frontend**
    In a separate terminal, start the Angular development server.
    ```bash
    npm start
    ```
    The application will be available at `http://localhost:4200`.

## 🧪 Testing

- **Backend Tests**: `./mvnw verify`
- **Frontend Tests**: `npm test`

## 📂 Project Structure

- `src/main/java`: Java source code (Spring Boot).
- `src/main/webapp`: Frontend source code (Angular).
- `src/main/docker`: Docker configuration files.
- `src/main/resources`: Configuration files (application.yml, etc.).
