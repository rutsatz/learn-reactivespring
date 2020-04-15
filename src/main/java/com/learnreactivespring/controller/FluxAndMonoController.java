package com.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    /**
     * Por padrão, o browser espera um JSON, então ele vai esperar receber o json completo para renderizar.
     *
     * @return
     */
    @GetMapping("/flux")
    public Flux<Integer> returnFlux() {
        return Flux.just(1, 2, 3, 4)
                // .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Então, para evitar esse comportamento bloqueante do browser, precisamos colocar no header do response
     * que estamos retornando um stream.
     *
     * @return
     */
    @GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnFluxStream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Por padrão, o browser espera um JSON, então ele vai esperar receber o json completo para renderizar.
     *
     * @return
     */
    @GetMapping("/mono")
    public Mono<Integer> returnMono() {
        return Mono.just(1)
                .log();
    }

}
