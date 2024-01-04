package com.github.lunchee.springtracing;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GreetingController {

    private final Tracer tracer;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebClient webClient;

    @PostMapping("/greet")
    public Mono<String> sendGreeting(@RequestParam String name) {
        logTraceContext("Send Greeting");

        String greeting = String.format("Hello, %s!", name);
        return Mono.fromFuture(kafkaTemplate.send("greetings", greeting))
                .thenReturn(greeting);
    }

    private void logTraceContext(String checkpointName) {
        if (tracer.currentSpan() != null) {
            log.info(checkpointName + ": Trace ID={}, Span ID={}", tracer.currentSpan().context().traceId(), tracer.currentSpan().context().spanId());
        } else {
            log.warn(checkpointName + ": No tracing context");
        }
    }

    @KafkaListener(id = "greetingListener", topics = "greetings")
    public void listenGreetings(String greeting) {
        logTraceContext("Greeting Listener");

        webClient.post()
                .uri(uriBuilder ->
                        uriBuilder.path("/print-greeting")
                                .queryParam("greeting", greeting)
                                .build())
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @PostMapping("/print-greeting")
    public Mono<Void> printGreeting(@RequestParam String greeting) {
        logTraceContext("Print Greeting");
        log.info(greeting);

        return Mono.empty();
    }
}
