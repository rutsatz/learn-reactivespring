package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_repeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                /* Posso dizer que quero repetir esse fluxo, n vezes. */
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                /* Esse encadeamento de filter, map, etc, é chamado de pipeline. */
                .filter(s -> s.length() > 4)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                /* O flatmap é um caso de uso especial para quando precisamos passar os elementos para
                 * um serviço externo, como uma chamada ao banco de dados ou uma API externa.
                 * Nesse exemplo aqui, ele vai dar um Flux<String>. */
                .flatMap(s -> {
                    return Flux.fromIterable(converToList(s));
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> converToList(String s) {
        /* Simula a chamada a um banco. */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformUsingFlatMap_usingParallel() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                /* Ao invés de passar um elemento por vez, ele espera por n elementos e passa todos por vez.
                 * Ele retorna um Flux<Flux<String>>. */
                .window(2)
                /* O flatmap é um caso de uso especial para quando precisamos passar os elementos para
                 * um serviço externo, como uma chamada ao banco de dados ou uma API externa.
                 * Nesse exemplo aqui, ele vai dar um Flux<String>. */
                .flatMap(s -> {
                    /* Return um Flux<List<String>>. E executa em paralelo. O subscribeOn que troca
                     * a execução de uma thread para várias e o parallel faz com que as
                     * operações sejam executadas em paralelo. A partir desse momento, ele muda
                     * o contexto de execução para outras threads.*/
                    return s.map(this::converToList).subscribeOn(Schedulers.parallel());
                })
                /* Converte novamente para um Flux<String> */
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_parallel_maintain_order() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                /* Ao invés de passar um elemento por vez, ele espera por n elementos e passa todos por vez.
                 * Ele retorna um Flux<Flux<String>>. */
                .window(2)
                /* O concatMap faz o mesmo que o flatMap, só que mantém a ordem dos elementos. Mas
                 * nesse cenário que a chamada externa demora 1 segundos, voltamos ao problema de
                 * demorar 6 segundos para executar tudo, mesmo que em threads separadas. */
//                .concatMap(s -> {
                /* Para resolver o problema do concatMap, usamos o flatMapSequential que executa em
                * paralelo mantendo a ordem. */
                .flatMapSequential(s -> {
                    /* Return um Flux<List<String>>. E executa em paralelo. O subscribeOn que troca
                     * a execução de uma thread para várias e o parallel faz com que as
                     * operações sejam executadas em paralelo. A partir desse momento, ele muda
                     * o contexto de execução para outras threads.*/
                    return s.map(this::converToList).subscribeOn(Schedulers.parallel());
                })
                /* Converte novamente para um Flux<String> */
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

}
