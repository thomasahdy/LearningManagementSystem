# 📚 Learning Management System (LMS)

A full-stack backend system for managing digital learning environments. Built with Java Spring Boot and designed with a layered architecture, this LMS provides robust, scalable, and secure APIs for handling educational operations such as attendance, announcements, grading, and more.

---

## 🔗 Repository Details

- **Project Type:** Backend REST API
- **Tech Stack:** Java, Spring Boot, Spring Security, JPA/Hibernate
- **Architecture:** Layered (Configuration, Controller, Service, DAO, DTO, Model)
- **Code Quality Tool:** SonarQube
- **Ticketing System:** Jira (for tracking bugs/features)

---


---

## 🧪 SonarQube Quality Overview

![SonarQube Overview](docs/images/sonarqube-overview.jpg)

- ✅ **Quality Gate:** Passed
- 🔐 **Security Issues:** 0
- 🛠️ **Maintainability Issues:** 127
- ⚠️ **Reliability Issues:** 35
- 🧪 **Code Coverage:** 0.0%
- 🔁 **Duplications:** 0.0%
- 🚨 **Security Hotspots:** 10

---

## 🛠️ Features (Upcoming in Next Phase)

### ✔️ Attendance Tracking
- Instructors can mark daily attendance
- Students can view history and percentage

### 📢 Announcement System
- Course-wide notifications with read tracking

### 🧮 Grade Tracking
- Track grades and calculate averages

### 📂 Resource Sharing
- Upload/download course materials

### 🗓️ Calendar & Deadline Reminders
- Highlight key course events with notifications

---

## 🎯 Goals of the Codebase

- 🔒 **Security:** Authentication & authorization using Spring Security
- 🧩 **Modularity:** Layered structure for separation of concerns
- 📈 **Scalability:** Easily add new modules/features
- ⚙️ **Maintainability:** SonarQube integrated for code health
- 🧠 **Extensibility:** Supports feature enhancements like analytics, notifications
- ✅ **Testability:** Testable with JUnit & Mockito

---


---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven or Gradle
- PostgreSQL/MySQL (DB)
- SonarQube (optional for analysis)

### Run the Project

```bash
# Build the project
./mvnw clean install

# Run the project
./mvnw spring-boot:run
