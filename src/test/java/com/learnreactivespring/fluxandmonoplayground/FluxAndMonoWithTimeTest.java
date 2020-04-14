package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {

        /* A cada 200ms, gera um novo valor. */
        Flux<Long> infiteFlux = Flux.interval(Duration.ofMillis(200))
                .log();

        infiteFlux.subscribe(element -> System.out.println("Value is : " + element));
        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        /* A cada 100ms, gera um novo valor. */
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                /* Pega somente os 3 primeiros elementos, tornando a sequência finita. */
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() throws InterruptedException {
        /* A cada 100ms, gera um novo valor. */
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                /* Converte de Long para Integer. */
                .map(l -> l.intValue())
                /* Pega somente os 3 primeiros elementos, tornando a sequência finita. */
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_withDelay() throws InterruptedException {
        /* A cada 100ms, gera um novo valor. */
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                /* Converte de Long para Integer. */
                .map(l -> l.intValue())
                /* Pega somente os 3 primeiros elementos, tornando a sequência finita. */
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

}
