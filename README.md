# Cheese Retry

A work-in-progress Java library designed to simplify retry handling logic with a clean, fluent API. Say goodbye to hardcoded retry logic and hello to configurable, reusable retry strategies!

## 📋 Overview

**Cheese Retry** provides a flexible framework for implementing sophisticated retry mechanisms in Java applications without cluttering your business logic with repetitive retry code. Whether you're handling transient failures, API timeouts, or temporary network issues, Cheese Retry makes it easy to define retry policies and apply them consistently across your codebase.

## ✨ Features

- **Flexible Retry Strategies** - Define custom retry policies with configurable delays, backoff mechanisms, and conditions
- **Fluent API** - Chain methods for readable and maintainable retry configuration
- **Multiple Backoff Strategies** - Support for exponential backoff, linear backoff, and custom delay functions
- **Conditional Retries** - Retry only on specific exceptions or conditions
- **Max Retry Limits** - Control maximum retry attempts and total timeout duration
- **Clean Separation of Concerns** - Keep retry logic separate from business logic

## 🚀 Quick Start

### Installation

Add Cheese Retry to your project (Maven/Gradle instructions coming soon):

```xml
<!-- Maven -->
<dependency>
    <groupId>com.sayanth</groupId>
    <artifactId>cheese-retry</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import com.sayanth.cheese.retry.RetryPolicy;

// Simple retry with default settings
var result = RetryPolicy.builder()
    .maxAttempts(3)
    .delayMs(1000)
    .execute(() -> riskyOperation());

// Retry only on specific exceptions
var result = RetryPolicy.builder()
    .maxAttempts(5)
    .delayMs(500)
    .retryOn(IOException.class, TimeoutException.class)
    .execute(() -> callExternalApi());

// Exponential backoff strategy
var result = RetryPolicy.builder()
    .maxAttempts(4)
    .initialDelayMs(100)
    .exponentialBackoff(2.0) // multiplier
    .execute(() -> databaseOperation());
```

## 📖 Documentation

### Core Concepts

#### RetryPolicy
The main builder class for configuring and executing operations with retry logic.

**Methods:**
- `maxAttempts(int)` - Maximum number of retry attempts (default: 3)
- `delayMs(long)` - Fixed delay between retries in milliseconds
- `initialDelayMs(long)` - Initial delay for backoff strategies
- `exponentialBackoff(double)` - Enable exponential backoff with multiplier
- `linearBackoff(long)` - Enable linear backoff with increment
- `retryOn(Class<? extends Exception>...)` - Specify which exceptions trigger retry
- `retryIf(Predicate<Exception>)` - Custom retry condition
- `timeout(long)` - Maximum total execution time in milliseconds
- `execute(Callable<T>)` - Execute the operation with retry logic

#### Backoff Strategies

**Fixed Delay:** Waits the same amount of time between each retry attempt
```java
RetryPolicy.builder()
    .maxAttempts(3)
    .delayMs(1000)
    .execute(operation);
```

**Exponential Backoff:** Increases delay exponentially after each attempt
```java
RetryPolicy.builder()
    .maxAttempts(5)
    .initialDelayMs(100)
    .exponentialBackoff(2.0) // 100ms, 200ms, 400ms, 800ms
    .execute(operation);
```

**Linear Backoff:** Increases delay linearly after each attempt
```java
RetryPolicy.builder()
    .maxAttempts(4)
    .initialDelayMs(500)
    .linearBackoff(500) // 500ms, 1000ms, 1500ms, 2000ms
    .execute(operation);
```

## 🔧 Advanced Usage

### Custom Retry Conditions

```java
// Retry only on specific status codes or conditions
RetryPolicy.builder()
    .maxAttempts(3)
    .delayMs(1000)
    .retryIf(ex -> ex.getMessage().contains("temporary"))
    .execute(() -> apiCall());
```

### Timeout Protection

```java
// Fail fast if total execution time exceeds timeout
RetryPolicy.builder()
    .maxAttempts(10)
    .delayMs(100)
    .timeout(5000) // 5 second maximum total time
    .execute(() -> operation());
```

### Combining Strategies

```java
// Exponential backoff + timeout + specific exceptions
RetryPolicy.builder()
    .maxAttempts(5)
    .initialDelayMs(200)
    .exponentialBackoff(1.5)
    .timeout(10000)
    .retryOn(TemporaryFailureException.class, TimeoutException.class)
    .execute(() -> criticalOperation());
```

## 🏗️ Project Status

⚠️ **Work in Progress** - This library is currently under active development. APIs and features may change. We welcome feedback and contributions!

### Planned Features

- [ ] Async retry support with CompletableFuture
- [ ] Jitter support for backoff strategies
- [ ] Metrics and monitoring hooks
- [ ] Circuit breaker pattern integration
- [ ] Retry budgets and rate limiting

## 🤝 Contributing

Contributions are welcome! Please feel free to:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 💬 Feedback & Issues

Found a bug or have a feature request? Please open an issue on the [GitHub Issues page](https://github.com/sayanth-ranjith/cheese-retry/issues).

## 📞 Support

For questions and discussions, feel free to reach out or open a discussion in the repository.

---

**Made with ❤️ by Sayanth Ranjith**
