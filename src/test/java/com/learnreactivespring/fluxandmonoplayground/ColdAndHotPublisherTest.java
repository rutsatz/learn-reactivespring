package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        /* Toda vez que adicionamos um subscriber, ele emite os valores do inicio.
         * Isso é chamado de Cold Publisher.*/
        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        Thread.sleep(2000);

        /* Mesmo que já tenham outros subscribers incritos, ao adicionar um novo, ele recebe os valores do
         * inicio. */
        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        /* Começa a receber os elementos do começo. */
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        Thread.sleep(3000);

        /* Começa a receber os elementos a partir do momento que fez o subscribe. */
        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));
        Thread.sleep(4000);

    }

}
