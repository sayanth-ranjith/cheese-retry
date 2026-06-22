# Cheese Retry Core

<p align="center">
  <strong>A flexible, battle-tested Java retry library for handling transient failures with grace.</strong>
</p>

---

## 📋 Overview

**Cheese Retry Core** is a lightweight, production-ready Java library that provides seamless retry handling without the pain of hardcoding retry logic. It enables developers to configure sophisticated retry strategies with exponential backoff, custom exception handling, and configurable attempt limits.

Built with:
- ☕ **Java 21**
- 🚀 **Spring Boot 4.1.0**
- 🏗️ **Lombok** for clean, maintainable code
- 🧪 Comprehensive validation and exception handling

---

## ✨ Features

- **🔄 Flexible Retry Policies** - Define custom retry behavior through builder pattern
- **⏱️ Multiple Backoff Strategies** - Exponential, linear, fixed, and custom implementations
- **🎯 Selective Exception Handling** - Retry only specific exceptions that matter
- **⚡ Zero Configuration Default** - Works out of the box with sensible defaults (max 40 retries)
- **📦 Lightweight & Fast** - Minimal dependencies, zero overhead
- **🛡️ Type-Safe** - Compile-time safety with Java generics
- **📚 Well-Documented** - Comprehensive JavaDoc and examples
- **🔒 Immutable & Thread-Safe** - Safe to use in concurrent environments

---

## 🚀 Quick Start

### Installation

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.sayanth-ranjith</groupId>
    <artifactId>cheese-retry-core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import com.sayanth_ranjith.cheese_retry_core.core.RetryPolicy;
import com.sayanth_ranjith.cheese_retry_core.core.strategy.ExponentialBackoffStrategy;
import com.sayanth_ranjith.cheese_retry_core.core.predicate.DefaultRetryPredicate;
import java.util.Set;

// Create a retry policy
RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(5)
    .backoffStrategy(new ExponentialBackoffStrategy(100))
    .retryPredicate(new DefaultRetryPredicate(
        Set.of(IOException.class, SocketTimeoutException.class)
    ))
    .build();

// Use the policy in your code
try {
    // Your operation here
    performDatabaseOperation();
} catch (IOException e) {
    if (policy.shouldRetry(e)) {
        // Wait and retry
        Thread.sleep(policy.getBackoffStrategy().nextDelayInMillis(attemptNumber));
        // Retry
    }
}
```

---

## 📖 Usage Examples

### Example 1: Simple Fixed Retry

```java
// Retry up to 3 times with fixed 1-second delay
RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(3)
    .backoffStrategy(new FixedBackoffStrategy(1000))
    .retryPredicate(new DefaultRetryPredicate(Set.of(Exception.class)))
    .build();
```

### Example 2: Exponential Backoff for API Calls

```java
// Perfect for API rate limiting
RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(5)
    .backoffStrategy(new ExponentialBackoffStrategy(100)) // Start with 100ms, double each time
    .retryPredicate(new DefaultRetryPredicate(
        Set.of(HttpClientErrorException.class, TimeoutException.class)
    ))
    .build();

// Delays: 200ms, 400ms, 800ms, 1600ms, 3200ms
```

### Example 3: Selective Exception Handling

```java
// Only retry on temporary failures, not on validation errors
Set<Class<? extends Exception>> retryableExceptions = Set.of(
    SocketTimeoutException.class,
    IOException.class,
    SQLException.class  // Retry database connection errors
);

RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(3)
    .backoffStrategy(new ExponentialBackoffStrategy(50))
    .retryPredicate(new DefaultRetryPredicate(retryableExceptions))
    .build();

// ValidationException won't trigger a retry
```

---

## 🏗️ Architecture

### Core Components

```
cheese-retry-core/
├── core/
│   ├── RetryPolicy.java              # Main policy class
│   ├── strategy/
│   │   ├── BackoffStrategy.java      # Interface for backoff algorithms
│   │   └── ExponentialBackoffStrategy.java  # Exponential backoff implementation
│   ├── predicate/
│   │   ├── RetryPredicate.java       # Interface for retry decisions
│   │   └── DefaultRetryPredicate.java    # Default implementation
│   └── exception/
│       ├── CheeseRetryConfigurationException.java  # Configuration errors
│       └── ExceptionConstants.java   # Error messages
```

### Component Responsibilities

| Component | Responsibility |
|-----------|-----------------|
| **RetryPolicy** | Orchestrates retry logic and validates configuration |
| **BackoffStrategy** | Calculates delay between retry attempts |
| **RetryPredicate** | Determines which exceptions should trigger retries |
| **CheeseRetryConfigurationException** | Signals configuration issues |

---

## 📚 API Reference

### RetryPolicy

The main class for defining retry behavior.

#### Builder Methods

```java
RetryPolicy.builder()
    .maxAttempts(int)              // Max retry attempts (default: 40)
    .backoffStrategy(BackoffStrategy)  // Required: delay calculation
    .retryPredicate(RetryPredicate)    // Required: exception filter
    .build()                       // Creates and validates policy
```

#### Properties

```java
policy.getMaxAttempts()            // Get max attempt count
policy.getBackoffStrategy()        // Get backoff strategy
policy.getRetryPredicate()         // Get retry predicate
```

#### Validation

All parameters are validated on object creation:
- `maxAttempts` must be > 0
- `backoffStrategy` cannot be null
- `retryPredicate` cannot be null

Throws `CheeseRetryConfigurationException` on invalid configuration.

---

### BackoffStrategy Interface

Implementations determine the delay between retry attempts.

#### Contract

```java
long nextDelayInMillis(int attempt)
```

- **Parameter:** Retry attempt number (1-indexed)
- **Returns:** Delay in milliseconds (must be >= 0)

#### Built-in Implementations

**ExponentialBackoffStrategy**

```java
new ExponentialBackoffStrategy(100)
// Formula: baseDelay * 2^attempt
// Example: 100 * 2^1 = 200ms, 100 * 2^2 = 400ms, etc.
```

### RetryPredicate Interface

Implementations determine which exceptions warrant a retry.

#### Contract

```java
boolean shouldRetry(Exception exception)
```

- **Parameter:** The exception that was thrown
- **Returns:** True if operation should be retried, false otherwise

#### Built-in Implementations

**DefaultRetryPredicate**

```java
new DefaultRetryPredicate(Set.of(IOException.class, TimeoutException.class))
// Retries if exception is an instance of any configured class
```

---

## ⚙️ Advanced Configuration

### Custom Backoff Strategy

```java
public class CustomBackoffStrategy implements BackoffStrategy {
    @Override
    public long nextDelayInMillis(int attempt) {
        // Your custom logic
        return Math.min(1000, attempt * 100);  // Linear up to 1 second
    }
}

RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(5)
    .backoffStrategy(new CustomBackoffStrategy())
    .retryPredicate(new DefaultRetryPredicate(Set.of(IOException.class)))
    .build();
```

### Custom Retry Predicate

```java
public class CustomRetryPredicate implements RetryPredicate {
    @Override
    public boolean shouldRetry(Exception exception) {
        // Retry only if it's a temporary network issue
        return exception instanceof SocketTimeoutException ||
               (exception instanceof IOException && 
                exception.getMessage().contains("connection reset"));
    }
}

RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(3)
    .backoffStrategy(new ExponentialBackoffStrategy(100))
    .retryPredicate(new CustomRetryPredicate())
    .build();
```

---

## 🔧 Building from Source

### Prerequisites

- Java 21+
- Maven 3.6.0+

### Build Steps

```bash
# Clone or navigate to the project
cd cheese-retry-core

# Build
./mvnw clean package

# Run tests
./mvnw test

# Install locally
./mvnw clean install
```

---

## 📝 Configuration Validation

All configurations are validated at build time:

```java
// ❌ This will throw CheeseRetryConfigurationException
RetryPolicy.builder()
    .maxAttempts(0)  // Must be > 0!
    .backoffStrategy(new ExponentialBackoffStrategy(100))
    .retryPredicate(new DefaultRetryPredicate(Set.of(Exception.class)))
    .build();

// ❌ This will throw CheeseRetryConfigurationException
RetryPolicy.builder()
    .maxAttempts(3)
    .backoffStrategy(null)  // Cannot be null!
    .retryPredicate(new DefaultRetryPredicate(Set.of(Exception.class)))
    .build();

// ❌ This will throw CheeseRetryConfigurationException
RetryPolicy.builder()
    .maxAttempts(3)
    .backoffStrategy(new ExponentialBackoffStrategy(100))
    .retryPredicate(null)  // Cannot be null!
    .build();
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Standards

- Follow Java naming conventions
- Write clear, self-documenting code
- Add comprehensive JavaDoc comments
- Include unit tests for new features
- Ensure all tests pass: `./mvnw test`

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Author

**Sayanth P V** - [@sayanth-ranjith](https://github.com/sayanth-ranjith)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Lombok project for reducing boilerplate
- The Java community for feedback and suggestions

---

## 📞 Support

- 📧 Create an [Issue](../../issues) for bug reports
- 💬 Discussions for feature requests
- 📖 Check existing [Documentation](./docs) for common questions

---

## 🗺️ Roadmap

- [ ] Async retry support
- [ ] CircuitBreaker pattern integration
- [ ] Metrics and monitoring hooks
- [ ] Spring Boot auto-configuration
- [ ] Additional backoff strategies (Linear, Fibonacci)
- [ ] Distributed tracing support

---

**Happy Retrying! 🎉**
