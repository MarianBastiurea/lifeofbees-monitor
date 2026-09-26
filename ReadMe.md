# LifeOfBees Monitor

AI-powered monitoring agent for [lifeofbees.co.uk](https://lifeofbees.co.uk).

`lifeofbees-monitor` is a Java Spring Boot application designed to monitor the availability of a production website, record monitoring events in MongoDB, and use an AI agent to investigate and attempt to resolve website failures.

The application is designed to move beyond simple uptime monitoring. When the website is unavailable, the monitoring agent can investigate the problem, perform predefined diagnostic or recovery actions, and produce a detailed report describing what happened and what actions were attempted.

---

## Project Overview

The application performs a scheduled health check of the website.

The monitoring workflow is:

1. Check the website availability.
2. If the website is available:

    * Record the monitoring event.
    * Send a daily status email.
    * Do not invoke the Diagnostic Agent or OpenAI.
3. If the website is unavailable:

    * Investigate the failure.
    * Analyse the HTTP status and diagnostic information.
    * Invoke the AI investigation agent.
    * Allow the AI agent to perform supported monitoring or recovery actions.
    * Record all actions performed.
    * Generate an AI investigation report.
    * Determine whether the website has recovered.
    * Send a final notification containing the investigation and outcome.
    * Persist the complete monitoring event in MongoDB.

The application therefore combines traditional website monitoring with AI-assisted diagnosis and recovery.

---

## Technology Stack

### Backend

* Java
* Spring Boot

### Java Technologies

* Java HTTP Client
* Java records
* Java collections

### Spring Technologies

* Spring Boot
* Spring Scheduling
* Spring Data MongoDB
* Spring Mail

### Database

* MongoDB Atlas

### AI

* OpenAI API
* AI investigation agent
* AI-assisted diagnostics and recovery

### Testing

* JUnit 5
* Mockito
* Spring Boot Test

### Development Tools

* IntelliJ IDEA
* Git
* GitHub
* Maven

---

## Development Philosophy

The project was built with several principles in mind:

### Separation of Responsibilities

Website checking, diagnostics, AI investigation, persistence and email notification are separated into dedicated components.

### Minimal AI Usage

AI is not called for every monitoring cycle.

When the website is healthy, the application performs only the required monitoring and notification operations.

AI investigation is reserved for actual availability problems.

### Observability

Every monitoring cycle produces a persistent event.

This creates a historical record that can later be used to analyse:

* website availability
* recurring failures
* HTTP error patterns
* AI recovery attempts
* recovery success or failure

### Testability

External dependencies are isolated behind dedicated components so that the core monitoring logic can be tested independently.

---

## Future Development

Potential future improvements include:

* richer monitoring history
* dashboard for MongoDB monitoring events
* additional recovery tools for the AI agent
* improved failure classification
* configurable monitoring intervals
* retry and back-off strategies
* additional notification channels
* authentication and authorisation for a monitoring dashboard
* metrics and application health endpoints
* Docker-based deployment
* cloud deployment and automated CI/CD

---

## Author

**Marian Bastiurea**

Java Developer / Software Development Project

This project demonstrates practical use of:

* Java
* Spring Boot
* REST/HTTP communication
* MongoDB
* AI integration
* automated monitoring
* unit testing
* integration testing
* Git and GitHub

---

## License

This project is currently a personal portfolio and learning project.
