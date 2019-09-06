# Spring Boot Introduction Outline

## Application Introduction

### Java Main Class
 * invoking
 * command line args

### Java/Jar Classpath
 * JVM Classpath
 * Executable JAR

### Classpath Scanning

### Annotations

### Spring Boot Application
* Executable JAR Format
* MANIFEST.MF properties

## Application Configuration Introduction

### Spring Boot Configuration
* What did those startup seconds give me?

### Component Scanning
* Scan configuration for components to include in application

### Bean Factories

### Starters
* Conditionals
* Built-in Conditionals (in order of expense)
  * ConditionalOn(Missing)Class
  * ConditionalOnResource
  * ConditionalOnJava
  * ConditionalOn(Not)WebApplication
  * ConditionalOnJNDI
  * ConditionalOnProperty
  * ConditionalOnExpression
  * ConditionalOnSingleCandidate
  * ConditionalOn(Missing)Bean
* Configuration Phases
  * User Configuration
    * Limit use of Conditionals because full context not yet available
    * e.g., ConditionalOn(Missing)Bean
  * Auto Configuration
* Phase-Dependent Conditionals
  * ConditionalOn(Missing)Bean
  * ConditionalOnSingleCandidate
* Custom Conditions
  * SpringBootCondition
  * ConditionalOutcome
  * Conditional
  * Conditional Annotation
* Conditional Placement
  * Class
  * Method
* Conditional Ordering
  * AutoConfigureAfter/Before

### Configuration Properties
* Annotation Processor
* META-INF/spring-configuration-metadata.json

### Environment Post Processor
* do something extra to environment before starting application

### Property Sources
* Actuator can show property sources

### Dev Tools

### Spring Boot Startup Events

    Run Application
        ApplicationStartingEvent
    Logging Initialization Begins
        ApplicationEvironmentPreparedEvent
    Config Files are Read
    Event Post Processors Called
    Logging Initialization Completes
        ApplicationPreparedEvent
    Application Context Refreshed
        ContextRefreshedEvent
    Embedded Servlet Container Connectors Started
        EmbeddedServletContainerInitializedEvent
        ApplicationReadyEvent
    CommandLineRunners and ApplicationRunners Called

## Logging

<!-- @PageBreak -->
