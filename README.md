# Image Processing Service

## Overview

This project is a Spring Boot-based application that provides APIs for user authentication and image transformation. It includes features such as user registration, login, and rate-limited image transformation to prevent abuse. [roadmap.sh/projects/image-processing-service](https://roadmap.sh/projects/image-processing-service)*

## Features

- **User Authentication**: Register and log in users with secure password encryption
- **JWT Token Generation**: Secure API calls using JSON Web Tokens (JWT)
- **Rate Limiting**: Prevent abuse of image transformation APIs by limiting requests per user
- **RESTful APIs**: Expose endpoints for user and image operations

## Technologies Used

- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- Bucket4j (Rate Limiting)
- Gradle (Build Tool)
- PostgreSQL


## API Endpoints

### 1. Authentication APIs

#### Register User
- **Endpoint**: `POST /api/auth/register`
- **Description**: Registers a new user and returns a JWT token

**Request Body:**
```json
{
  "username": "exampleUser",
  "password": "examplePassword"
}
```

**Response:**
```json
// 200 OK
{
  "token": "jwt-token-string"
}

// 409 CONFLICT
{
  "message": "User already exists"
}
```

#### Login User
- **Endpoint**: `POST /api/auth/login`
- **Description**: Authenticates a user and returns a JWT token

**Request Body:**
```json
{
  "username": "exampleUser",
  "password": "examplePassword"
}
```

**Response:**
```json
// 200 OK
{
  "token": "jwt-token-string"
}

// 404 NOT FOUND
{
  "message": "User not found"
}
```


### 2. Image Transformation APIs

#### Transform Image
- **Endpoint**: `POST /api/image/{id}/transform`
- **Description**: Transforms an image based on the provided parameters (Rate-limited to 10 requests per minute per user)

**Headers:**
```
Authorization: Bearer <jwt-token>
```

**Request Body:**
```json
{
    "transformations":{
        "resize":{
            "width":"number",
            "height":"number"
        },
        "rotate": "number",
        "format": "String",
    "crop": {
      "width": "number",
      "height": "number"
    }
    }
}
```

**Response:**
```json
// 200 OK
{
  "id" : "id"
  "imageName": "string",
  "url": "http://example.com/transformed-image.jpg"
}

// 429 TOO MANY REQUESTS
{
  "message": "Rate limit exceeded. Please try again later."
}
```

## How to Run the Project

### Prerequisites
- Java 17
- Gradle
- PostgreSQL (version 12 or higher)
- IDE (e.g., IntelliJ IDEA)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/wengchuan/imageprocess.git
   ```

2. Navigate to the project directory:
   ```bash
   cd imageprocess
   ```

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

5. Access the application at `http://localhost:8080`
