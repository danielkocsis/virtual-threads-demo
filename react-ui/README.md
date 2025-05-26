# React UI for Spring Boot Virtual Threads Demo

## Description

This React application serves as a user interface to interact with and test the different Spring Boot backend modules:
-   Spring MVC
-   Spring Webflux
-   Spring Loom (Virtual Threads)

The UI allows users to send requests to each backend module and view the number of audit log entries created by each, demonstrating their basic functionality and interaction with a shared data source.

## Prerequisites

Before you begin, ensure you have the following installed:
-   [Node.js](https://nodejs.org/) (which includes npm)
    -   Verify installation with `node -v` and `npm -v`.
-   Alternatively, you can use [Yarn](https://yarnpkg.com/) if you prefer.
    -   Verify installation with `yarn --version`.

## Installation

1.  **Navigate to the UI directory:**
    ```bash
    cd path/to/your/project/react-ui
    ```

2.  **Install dependencies:**
    If you are using npm:
    ```bash
    npm install
    ```
    If you are using Yarn:
    ```bash
    yarn install
    ```

## Running the Application

1.  **Start the React development server:**
    If you are using npm:
    ```bash
    npm start
    ```
    If you are using Yarn:
    ```bash
    yarn start
    ```
    This will typically open the application in your default web browser at `http://localhost:3000`.

## Important Note for Backend Interaction

For the UI to function correctly and interact with the backend services, ensure that the corresponding Spring Boot applications are running. By default, the React application is configured to proxy API requests to `http://localhost:8080`.

Each Spring Boot module (MVC, Webflux, Loom) runs its own embedded server, typically on port 8080. You will need to run one of these backend applications at a time to test its specific UI component, or configure them to run on different ports if you wish to test all simultaneously (though the current UI proxy is set for a single port 8080).

-   **spring-mvc**: Serves MVC endpoints.
-   **spring-webflux**: Serves Webflux endpoints.
-   **spring-loom**: Serves Loom (virtual thread based) endpoints.

Refer to the main project README for instructions on how to run the Spring Boot applications.
