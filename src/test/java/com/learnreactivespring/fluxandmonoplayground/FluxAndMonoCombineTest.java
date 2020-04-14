package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                /* Valida que tem um Subscription, pois isso é a primeira coisa que acontece
                 * quando você faz um subscribe. */
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        /* Ao combinar, ele começa a consumir o primeiro flux, mas não espera terminar de consumir
         * ele até o fim para começar a consumir o próximo, ele começa a consumir o primeiro e logo
         * em seguida o próximo. Então não podemos garantir a ordem. */
        StepVerifier.create(mergedFlux.log())
                /* Valida que tem um Subscription, pois isso é a primeira coisa que acontece
                 * quando você faz um subscribe. */
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                /* Valida que tem um Subscription, pois isso é a primeira coisa que acontece
                 * quando você faz um subscribe. */
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat_withDelay() {

        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        /* O concat é o mesmo que o merge, só que ele garante a ordem. No caso do delay,
         * ele demora mais, pois ele somente consome o flux2 quando terminar o flux1. */
        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.withVirtualTime(() -> mergedFlux.log())
                /* Valida que tem um Subscription, pois isso é a primeira coisa que acontece
                 * quando você faz um subscribe. */
                .expectSubscription()
                /* Para o relógio virtual funcionar, preciso saber quanto tempo o teste vai demorar
                 * e então passar esse tempo para o thenAwait(). */
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

//        StepVerifier.create(mergedFlux.log())
//                /* Valida que tem um Subscription, pois isso é a primeira coisa que acontece
//                 * quando você faz um subscribe. */
//                .expectSubscription()
//                .expectNext("A", "B", "C", "D", "E", "F")
//                .verifyComplete();
    }

    @Test
    public void combineUsingZip() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        /* O zip recebe mais dois parâmetros que são as funções que vão fazer o combine, recebe os pares
         * de elementos por parâmetro, por exemplo: (A,D) - (B,E) - (C,F).
         * Ou seja, ele combina os dois flux em um, e você passa a função que irá fazer esse combine. */
        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            /* Concatena as strings. */
            return t1.concat(t2);
        });

        StepVerifier.create(mergedFlux.log())
                /* Valida que tem um Subscription, pois isso é a primeira coisa que acontece
                 * quando você faz um subscribe. */
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

}
