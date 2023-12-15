# Virtual Threads 

Inspired by [this](https://www.danvega.dev/blog/virtual-threads-spring-boot) great article by Dan Vega and the release of [Spring Boot 3.2 with JDK21 support](https://spring.io/blog/2023/09/09/all-together-now-spring-boot-3-2-graalvm-native-images-java-21-and-virtual) (and for more details please see the [release notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.2-Release-Notes#support-for-virtual-threads)).

This small projects is aiming to measure basic performance differences of the same very made up blocking web application considering the following scenarios:

> Common: Spring Boot version is 3.2 and JDK version is 21

1. OS Threads + Spring Web MVC (Tomcat)
2. Virtual Threads + Spring MVC (Tomcat)
3. OS Threads + Spring Reactor (Netty)
4. Virtual Threads + Spring Reactor (Netty)

## Project structure

I think structure must be obvious for anyone looking at this repo :) However, source code handling wise, in `main` you can find 2 commits, first contains Tomcat specific state and second is the Webflux specific one.

## Measurement conditions

I used the [Apache Benchmark](https://httpd.apache.org/docs/2.4/programs/ab.html) first, as suggested by Dan, however I run issues with response processing when I used 
Virtual Threads and tried to generate higher load with increased concurrency. I tried [hey](https://github.com/rakyll/hey) next 
and it worked flawlessly, so the results were generated by that.

I tried to simulate a scenario when concurrent requests count is way higher what our infrastructure of the application can handle,
so in case of Apache Tomcat I limited the Tomcat threads to 50% of my machine core count (4) and used the following test parameters:

`hey -n 100 -c 50 http://localhost:8080/httpbin/block/3`

## Results 
The total time in seconds to process the 100 requests with 50 concurrent workers.

|                            | Virtual Threads ON | Virtual Threads Off |
|----------------------------|:------------------:|:-------------------:|
| **Spring MVC + Tomcat**    |        7.7s        |         40s         |
| **Spring Reactor + Netty** |        8.7s        |        8.7s         |


### Summary

### Details

For the sceptics :) 

<details>
<summary>Tomcat with Virtual Threads On</summary>

```
Summary:
  Total:	40.0051 secs
  Slowest:	17.1136 secs
  Fastest:	3.1142 secs
  Average:	6.9724 secs
  Requests/sec:	2.4997

  Total data:	897 bytes
  Size/request:	39 bytes

Response time histogram:
  3.114 [1]	|■■■
  4.514 [14]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  5.914 [0]	|
  7.314 [0]	|
  8.714 [0]	|
  10.114 [0]	|
  11.514 [4]	|■■■■■■■■■■■
  12.914 [0]	|
  14.314 [0]	|
  15.714 [0]	|
  17.114 [4]	|■■■■■■■■■■■


Latency distribution:
  10% in 3.1278 secs
  25% in 3.2048 secs
  50% in 3.7997 secs
  75% in 10.6461 secs
  90% in 16.8909 secs
  95% in 17.1136 secs
  0% in 0.0000 secs

Details (average, fastest, slowest):
  DNS+dialup:	0.0036 secs, 3.1142 secs, 17.1136 secs
  DNS-lookup:	0.0008 secs, 0.0000 secs, 0.0018 secs
  req write:	0.0001 secs, 0.0000 secs, 0.0012 secs
  resp wait:	6.9686 secs, 3.1142 secs, 17.1048 secs
  resp read:	0.0001 secs, 0.0000 secs, 0.0001 secs

Status code distribution:
  [200]	23 responses

Error distribution:
  [77]	Get "http://localhost:8080/httpbin/block/3": context deadline exceeded (Client.Timeout exceeded while awaiting headers)
```
</details>

<details>
<summary>Tomcat with Virtual Threads On</summary>

```
Summary:
  Total:	7.7112 secs
  Slowest:	4.4568 secs
  Fastest:	3.1099 secs
  Average:	3.3882 secs
  Requests/sec:	12.9681

  Total data:	7200 bytes
  Size/request:	72 bytes

Response time histogram:
  3.110 [1]	|■
  3.245 [39]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  3.379 [5]	|■■■■■
  3.514 [35]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  3.649 [12]	|■■■■■■■■■■■■
  3.783 [4]	|■■■■
  3.918 [1]	|■
  4.053 [0]	|
  4.187 [1]	|■
  4.322 [0]	|
  4.457 [2]	|■■


Latency distribution:
  10% in 3.1131 secs
  25% in 3.1172 secs
  50% in 3.4899 secs
  75% in 3.5101 secs
  90% in 3.6078 secs
  95% in 3.7783 secs
  99% in 4.4568 secs

Details (average, fastest, slowest):
  DNS+dialup:	0.0042 secs, 3.1099 secs, 4.4568 secs
  DNS-lookup:	0.0013 secs, 0.0000 secs, 0.0030 secs
  req write:	0.0003 secs, 0.0000 secs, 0.0020 secs
  resp wait:	3.3831 secs, 3.1098 secs, 4.4471 secs
  resp read:	0.0000 secs, 0.0000 secs, 0.0002 secs

Status code distribution:
  [200]	100 responses
```
</details>

<details>
<summary>Netty with Virtual Threads Off</summary>

```
Summary:
  Total:	8.7523 secs
  Slowest:	5.5336 secs
  Fastest:	3.1106 secs
  Average:	3.8188 secs
  Requests/sec:	11.4255

  Total data:	3300 bytes
  Size/request:	33 bytes

Response time histogram:
  3.111 [1]	|■
  3.353 [38]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  3.595 [9]	|■■■■■■■■■
  3.837 [2]	|■■
  4.080 [0]	|
  4.322 [31]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  4.564 [11]	|■■■■■■■■■■■■
  4.807 [6]	|■■■■■■
  5.049 [1]	|■
  5.291 [0]	|
  5.534 [1]	|■


Latency distribution:
  10% in 3.1155 secs
  25% in 3.1559 secs
  50% in 4.2899 secs
  75% in 4.3094 secs
  90% in 4.4880 secs
  95% in 4.6722 secs
  99% in 5.5336 secs

Details (average, fastest, slowest):
  DNS+dialup:	0.0036 secs, 3.1106 secs, 5.5336 secs
  DNS-lookup:	0.0010 secs, 0.0000 secs, 0.0023 secs
  req write:	0.0002 secs, 0.0000 secs, 0.0025 secs
  resp wait:	3.8139 secs, 3.1105 secs, 5.5232 secs
  resp read:	0.0000 secs, 0.0000 secs, 0.0003 secs

Status code distribution:
  [200]	100 responses
```
</details>


<details>
<summary>Netty with Virtual Threads On</summary>

```
hey -n 100 -c 50 http://localhost:8080/httpbin/block/3

Summary:
  Total:	8.7813 secs
  Slowest:	5.2182 secs
  Fastest:	3.1112 secs
  Average:	3.7778 secs
  Requests/sec:	11.3878

  Total data:	3300 bytes
  Size/request:	33 bytes

Response time histogram:
  3.111 [1]	|■
  3.322 [44]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  3.533 [3]	|■■■
  3.743 [2]	|■■
  3.954 [0]	|
  4.165 [0]	|
  4.375 [36]	|■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
  4.586 [11]	|■■■■■■■■■■
  4.797 [1]	|■
  5.007 [1]	|■
  5.218 [1]	|■


Latency distribution:
  10% in 3.1204 secs
  25% in 3.1362 secs
  50% in 4.2967 secs
  75% in 4.3023 secs
  90% in 4.4416 secs
  95% in 4.5709 secs
  99% in 5.2182 secs

Details (average, fastest, slowest):
  DNS+dialup:	0.0026 secs, 3.1112 secs, 5.2182 secs
  DNS-lookup:	0.0007 secs, 0.0000 secs, 0.0017 secs
  req write:	0.0001 secs, 0.0000 secs, 0.0010 secs
  resp wait:	3.7743 secs, 3.1111 secs, 5.2107 secs
  resp read:	0.0001 secs, 0.0000 secs, 0.0006 secs

Status code distribution:
  [200]	100 responses
```
</details>
