# **Spring Proxy Application**

This project implements a **proxy server** using **Spring Boot** that redirects requests to the official website `https://spring.io`. The application modifies HTML content by appending the symbol "™" to all six-letter words while preserving JavaScript, CSS, images, and other resources. Additionally, it adjusts all internal navigation links to point to the proxy server.

## **Features**
1. **Proxy for spring.io**:
    - Redirects requests, such as `http://localhost:8080/solutions`, to `https://spring.io/solutions`.
    - Automatically handles HTTP redirects (e.g., HTTP 308 or 301).
2. **HTML Content Modification**:
    - Appends "™" to every six-letter word in the HTML content.
3. **Seamless Navigation**:
    - Internal links on the website are rewritten to point to the proxy server (e.g., links on `https://spring.io` become `http://localhost:8080`).
4. **Preserves Static Resources**:
    - Ensures CSS, JS, images, and other resources are proxied without modification.
5. **Containerization**:
    - Fully containerized and runs in Docker.

## **Requirements**
- Java 17 or higher
- Maven for building the project
- Docker (optional for containerization)

## **Getting Started**
### **1. Clone the Repository**
```bash
git clone https://github.com/your-repo/spring-proxy
cd spring-proxy
```
### **2. Run VmediaApplication class for start application**