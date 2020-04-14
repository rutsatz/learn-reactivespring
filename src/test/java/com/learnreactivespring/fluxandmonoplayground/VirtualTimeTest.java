package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithoutVirtualTime() {

        /* Gera elementos a cada 1 segundo. Usa o relógio real da máquina, então o teste vai demorar. */
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                /* Pega somente os 3 primeiros. */
                .take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void testingWithVirtualTime() {

        /* Configurar para não usar o relógio real da máquina, mas sim um relógio virtual. */
        VirtualTimeScheduler.getOrSet();

        /* Gera elementos a cada 1 segundo. Nesse caso, como está usando o relógio virtual, ele não vai
         * demorar para executar, igual o teste anterior demora. */
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                /* Pega somente os 3 primeiros. */
                .take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                /* Para o relógio virtual funcionar, preciso saber quanto tempo o teste vai demorar
                 * e então passar esse tempo para o thenAwait(). */
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();

    }

}
