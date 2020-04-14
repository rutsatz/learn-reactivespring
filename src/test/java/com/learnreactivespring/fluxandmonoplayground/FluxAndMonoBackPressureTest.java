package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                /* Assumimos o controle e controlamos os itens que pedimos para o publisher. */
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();
        finiteFlux.subscribe(
                /* Consumer para cada elemento. */
                element -> System.out.println("Element is : " + element)
                /* Consumer em caso de erro. */
                , e -> System.err.println("Exception is : " + e)
                /* Consumer ao terminar o stream. */
                , () -> System.out.println("Done")
                /* Consumer do subscription atual. Maneira de controlar programáticamente os requests. Relacionado ao
                 * backPressure. Nesse caso, estamos pedindo somente dois elementos. */
                , subscription -> subscription.request(2));

    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();
        finiteFlux.subscribe(
                /* Consumer para cada elemento. */
                element -> System.out.println("Element is : " + element)
                /* Consumer em caso de erro. */
                , e -> System.err.println("Exception is : " + e)
                /* Consumer ao terminar o stream. */
                , () -> System.out.println("Done")
                /* Consumer do subscription atual. Maneira de controlar programáticamente os requests. Relacionado ao
                 * backPressure. Nesse caso, estamos cancelando direto, sem receber nenhum elemento. */
                , subscription -> subscription.cancel());
    }

    @Test
    public void customized_backPressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        /* BaseSubscriber permite um controle mais detalhado do subscriber, permitindo controlar
        como os dados devem fluir do publisher. */
        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                /* Pede um elemento por vez. */
                request(1);
                System.out.println("Value received is : " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });

    }

}
