package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestExecutionListeners;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void fluxUsingIterable() {

        /* Cria um flux a partir de um ArrayList. */
        Flux<String> namesFlux = Flux.fromIterable(names)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {

        String[] names = new String[]{"adam", "anna", "jack", "jenny"};

        /* Cria um flux a partir de um array. */
        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {

        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                /* O stream usa o LazyEvaluation, então, quando chamamos o verifyComplete(), ele faz o
                 * subscribe, que começa a fazer o stream do names, um por um, passando pro flux, que por sua vez,
                 * passa o Flux, um por um, para o subscriber. */
                .verifyComplete();

    }

    @Test
    public void monoUsingJustOrEmpty() {

        /* Quando chamamos o justOrEmpty passando null, recebemos um Mono Empty, ou seja, seria o equivalente
         * a fazer Mono.empty(). */
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier.create(mono.log())
                /* Como temos um mono vazio, ele não vai emitir nenhum evento, então não podemos esperar
                 * nada, aí chamamos direto o verifyComplete(). */
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {

        /* Supplier é uma interface funcional introduzida no Java8, junto com o pacote Function. */
        Supplier<String> stringSupplier = () -> "adam";

        /* Ele recupera o valor do Supplier quando chama o método get(). */
        System.out.println(stringSupplier.get());

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {

        /* Cria um flux com o range, iniciando no valor 1, e incrementando mais 5 valores a partir do 1. */
        Flux<Integer> integerFlux = Flux.range(1, 5).log();

        StepVerifier.create(integerFlux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();

    }

}
