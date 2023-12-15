# Virtual Threads

Inspired by [this](https://www.danvega.dev/blog/virtual-threads-spring-boot) great article by Dan Vega and the release
of [Spring Boot 3.2 with JDK21 support](https://spring.io/blog/2023/09/09/all-together-now-spring-boot-3-2-graalvm-native-images-java-21-and-virtual) (
and for more details please see
the [release notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes#support-for-virtual-threads)).

This small projects is aiming to measure basic performance differences of the same (very artificial) blocking I/O heavy
java web application considering the following scenarios:

> Common: Spring Boot version is 3.2.5 and JDK version is 21

1. OS Threads + Spring Web MVC (Tomcat)
2. Virtual Threads + Spring MVC (Tomcat)
3. OS Threads + Spring Reactor (Netty)

## Project structure

All scenarios are placed in a separate Gradle module.

## Measurement conditions

In case of Tomcat based application I limited the worker threads count to 10 in order to provide meaningful result from
my laptop running the load test and the Docker container for Httbin too.

To measure the application throughput under certain load I used load generator [Oha](https://github.com/hatoo/oha),
but [Hey](https://github.com/rakyll/hey) is also a nice option.

## Results

### Scenario 1

Each API calls sends **one** external HTTP call which takes 3 seconds to complete.

`oha -z 10s -c 20 http://localhost:8080/api/demo/{service}`

|                                  | Request/sec |
|----------------------------------|:-----------:|
| **Spring MVC + Virtual threads** |     20      |
| **Spring MVC**                   |     10      |
| **Spring Reactor(Netty)**        |     20      |

### Scenario 2

Each API calls sends **three** external HTTP calls which takes 3 seconds to complete.
In this version the Spring MVC controller
utilised [Structured Concurrency](https://docs.oracle.com/en/java/javase/21/core/structured-concurrency.html)
JDK21 preview feature to invoke the RestClient calls parallel.

`oha -z 10s -c 200 http://localhost:8080/api/demo/{service}`

|                                  | Request/sec |
|----------------------------------|:-----------:|
| **Spring MVC + Virtual Threads** |     194     |
| **Spring MVC**                   |     10      |
| **Spring Reactor(Netty)**        |     191     |