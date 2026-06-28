# Cheese Retry

Visit **https://cheese-retry.vercel.app/ to know more about Cheese.

🧀 A **lightweight, fun, and easy-to-use** Java library for retry logic. Built for learning and for developers who want to add retry capabilities to their applications **quickly and simply**.

**No complexity. No bloat. Just retry logic that works.**

## 📋 Overview

**Cheese Retry** provides a straightforward framework for implementing retry mechanisms in Java applications. Whether you're handling transient failures, API timeouts, or temporary network issues, Cheese Retry makes it easy without cluttering your business logic.

Perfect for:
- Developers learning about retry patterns
- Projects needing quick retry implementations
- Spring Boot applications looking for lightweight solutions
- Anyone who finds other retry libraries overkill

## ✨ Features

- **Lightweight & Simple** - Minimal dependencies, maximum clarity
- **Annotation-Based (`@CheeseRetry`)** - Spring Boot auto-configuration ready
- **Fluent API** - Traditional programmatic approach when you need it
- **Multiple Backoff Strategies** - Exponential, linear, or fixed delays
- **Conditional Retries** - Retry only on specific exceptions
- **Max Retry Limits** - Control attempts and timeout duration
- **Built for Learning** - Clean, readable source code

## 🚀 Quick Start

### Installation

Add Cheese Retry to your project:

```xml
<!-- Maven -->
<dependency>
    <groupId>io.github.sayanth-ranjith</groupId>
    <artifactId>cheese-retry</artifactId>
    <version>0.0.11</version>
</dependency>
```

### Option 1: Spring Boot Annotation (Recommended for Spring apps)

```java
import com.sayanth.cheese.retry.CheeseRetry;
import com.sayanth.cheese.retry.RetryPredicateType;
import com.sayanth.cheese.retry.BackoffStrategyType;

@Service
public class ApiService {
    
    // Simple retry with defaults
    @CheeseRetry
    public String fetchData() {
        return callExternalApi();
    }
    
    // Customized retry strategy
    @CheeseRetry(
        maxAttempts = 5,
        delayInMillis = 500,
        retryPredicateType = RetryPredicateType.TYPED_BASED_RETRY,
        retryOn = {IOException.class, TimeoutException.class},
        backoffStrategyType = BackoffStrategyType.EXPONENTIAL
    )
    public String fetchDataWithBackoff() {
        return callExternalApi();
    }
    
    // Exponential backoff configuration
    @CheeseRetry(
        maxAttempts = 4,
        delayInMillis = 100,
        backoffStrategyType = BackoffStrategyType.EXPONENTIAL,
        retryPredicateType = RetryPredicateType.ALWAYS_RETRY
    )
    public void databaseOperation() {
        // Implementation here
    }
}
```

**Annotation Parameters:**
- `maxAttempts` - Maximum retry attempts (default: 3)
- `delayInMillis` - Delay between retries in milliseconds (default: 1000)
- `retryOn` - Exception classes that trigger retry (default: Exception.class)
- `backoffStrategyType` - FIXED, EXPONENTIAL (default: FIXED)
- `retryPredicateType` - ALWAYS_RETRY or custom predicate logic

### Option 2: Programmatic API

```java
import com.sayanth.cheese.retry.RetryPolicy;
import com.sayanth.cheese.retry.CoreRetryExecutor;
import com.sayanth.cheese.retry.ExponentialBackoffStrategy;
import com.sayanth.cheese.retry.AlwaysRetryPredicate;

// Create a retry policy
RetryPolicy policy = RetryPolicy.builder()
    .maxAttempts(5)
    .retryPredicate(new AlwaysRetryPredicate())
    .backoffStrategy(new ExponentialBackoffStrategy(1000))
    .build();

// Execute with retry logic
RetryExecutor executor = new CoreRetryExecutor(policy);

try {
    executor.execute(() -> {
        // Your task that might fail
        return fetchData();
    });
} catch (Exception e) {
    throw new RuntimeException("Failed after retries", e);
}
```

## 📖 Documentation

### Core Components

#### @CheeseRetry Annotation
Apply to any method in a Spring Boot application for automatic retry handling:
```java
@CheeseRetry(maxAttempts = 3, delayInMillis = 500)
public String riskyMethod() {
    // Implementation
}
```

#### RetryPolicy (Fluent Builder)
Programmatic configuration with a clean, readable API:
```java
RetryPolicy.builder()
    .maxAttempts(3)
    .delayMs(1000)
    .execute(() -> riskyOperation());
```

#### Backoff Strategies

**Fixed Delay** - Same wait time between retries
```java
new FixedBackoffStrategy(1000) // Wait 1 second each time
```

**Exponential Backoff** - Exponentially increase wait time
```java
new ExponentialBackoffStrategy(1000) // 1s, 2s, 4s, 8s...
```
### Retry Predicates

**AlwaysRetryPredicate** - Retry on any exception
```java
.retryPredicate(new AlwaysRetryPredicate())
```

**Custom Predicates** - Implement your own logic
```java
.retryIf(ex -> ex.getMessage().contains("temporary"))
```

## 💡 Real-World Examples

### Example 1: REST API Call with Exponential Backoff

```java
@Service
public class ExternalApiClient {
    
    @CheeseRetry(
        maxAttempts = 5,
        delayInMillis = 200,
        backoffStrategyType = BackoffStrategyType.EXPONENTIAL,
        retryOn = {HttpClientErrorException.class}
    )
    public ResponseEntity<Data> fetchUserData(String userId) {
        return restTemplate.getForEntity("/api/users/" + userId, Data.class);
    }
}
```

### Example 2: Database Operation with Fixed Delay

```java
@Repository
public class UserRepository {
    
    @CheeseRetry(
        maxAttempts = 3,
        delayInMillis = 500,
        retryOn = {DataAccessException.class}
    )
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
```

### Example 3: Programmatic Retry

```java
public class PaymentService {
    public void processPayment(Payment payment) throws Exception {
        RetryPolicy policy = RetryPolicy.builder()
            .maxAttempts(3)
            .retryPredicate(new AlwaysRetryPredicate())
            .backoffStrategy(new ExponentialBackoffStrategy(1000))
            .build();
        
        new CoreRetryExecutor(policy).execute(() -> {
            paymentGateway.charge(payment);
            return null;
        });
    }
}
```

## 🏗️ Project Status

✅ **Lightweight & Production-Ready for Simple Use Cases**

This is a learning project built for fun and simplicity. Perfect for:
- Understanding retry patterns
- Adding retry logic without heavyweight dependencies
- Spring Boot projects needing quick solutions
- Educational purposes

**Note:** For enterprise-grade retry solutions with advanced features (circuit breakers, bulkheads, metrics), consider libraries like Resilience4j.

### Potential Future Additions

- [ ] Async support with CompletableFuture
- [ ] Jitter support for backoff strategies
- [ ] Built-in metrics and logging hooks
- [ ] Circuit breaker integration

## 🤝 Contributing

Contributions and improvements are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 💬 Feedback & Questions

Have ideas or run into issues? Open an issue on the [GitHub Issues page](https://github.com/sayanth-ranjith/cheese-retry/issues).

---

**Made with ❤️ by Sayanth Ranjith** - Because sometimes, the simplest solution is the best solution. 🧀
