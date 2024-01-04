# Spring Boot 3.2 Tracing Example
This example shows how to configure a Spring Boot 3.2 WebFlux application
to enable the tracing feature in Rest Controllers, Kafka Producers/Consumers, and Web Clients.

A `docker-compose-environment.yml` docker-compose file can help you
set up a local environment for testing the application.

Here is an example request to the service with the predefined Trace ID that the application will support and propagate:
`curl -X 'POST' -H 'traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01' 'http://localhost:8080/greet?name=Pichu'`

Alternatively, a new Trace ID will be generated and propagated if you omit the `traceparent` header.
