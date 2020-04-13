package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                /* Adicionamos uma exception ao Flux, para forçar uma exception. Depois que o erro
                 * é lançado, não emitido mais nenhum evento e o método completed não é chamado. */
//                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After Error"))
                /* O Flux tem um log para permitir ver o que está acontecendo por trás
                 * dos panos. Ele irá logar todos os eventos que são recebidos. */
                .log();
        /* A única maneira de acessar os dados do flux é através do Subscribe. Quando fazemos o subscribe,
        o flux começa a emitir os valores para o subscriber.
        Na programação imperativa, as exceptions são tratadas com try/catch. Na reativa, passamos handlers que
        são chamados quando um erro acontece. No subscribe, o segundo parâmetro recebe a função que é chamada
        quando algum erro acontece.
        */
        stringFlux.subscribe(
                /* Função que processa cada um dos itens. */
                System.out::println,
                /* Função para tratar as exceptions. */
                e -> System.err.println("Exception is " + e),
                /* Função executa ao completar o stream. */
                () -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .log();
        /* Verifica que os elementos são recebidos na ordem em que são criados no Flux.
         *  */
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactor Spring")
                /* O verifyComplete faz o subscribe no Flux e verifica que os elementos são recebidos
                 * conforme passamos pra ele. Se não chamarmos ele, nenhum evento é consumido, pois
                 * nõa haverá ninguém inscrito no flux. */
                .verifyComplete();
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        /* Verifica que os elementos são recebidos na ordem em que são criados no Flux.
         *  */
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactor Spring")
                /* Dizemos que estamos esperando uma exception. */
//                .expectError(RuntimeException.class)
                /* Também podemos verificar a mensagem da exception. */
                .expectErrorMessage("Exception Occurred")
                /* Aqui, como temos uma exception, o stream não vai completar, então o verifyComplete()
                 * não faz sentido. Nesse cenário, registramos a exception acima e chamamos o verify(). */
                .verify();
    }

    @Test
    public void fluxTestElementsCount_WithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        /* Verifica que os elementos são recebidos na ordem em que são criados no Flux.
         *  */
        StepVerifier.create(stringFlux)
                /* Ao invés de verificar cada elemento individualmente, podemos verificar a quantidade
                 * de elementos. */
                .expectNextCount(3)
                /* Dizemos que estamos esperando uma exception. */
//                .expectError(RuntimeException.class)
                /* Também podemos verificar a mensagem da exception. */
                .expectErrorMessage("Exception Occurred")
                /* Aqui, como temos uma exception, o stream não vai completar, então o verifyComplete()
                 * não faz sentido. Nesse cenário, registramos a exception acima e chamamos o verify(). */
                .verify();
    }

    @Test
    public void fluxTestElements_WithError1() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        /* Verifica que os elementos são recebidos na ordem em que são criados no Flux.
         *  */
        StepVerifier.create(stringFlux)
                /* Uma pequena variação, em que podemos passar todos os valores esperados de uma vez,
                 * ao invés de chamar vários expectNext. */
                .expectNext("Spring", "Spring Boot", "Reactor Spring")
                /* Também podemos verificar a mensagem da exception. */
                .expectErrorMessage("Exception Occurred")
                /* Aqui, como temos uma exception, o stream não vai completar, então o verifyComplete()
                 * não faz sentido. Nesse cenário, registramos a exception acima e chamamos o verify(). */
                .verify();
    }

    @Test
    public void monoTest() {

        Mono<String> stringMono = Mono.just("Spring");

        /* Depois que criei o Mono, posso chamar os métodos, como nesse caso que chamo o log e depois passo
         * a instância para o create. */
        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error() {

        /* Como o mono é somente um elemento, posso chamar direto o error, que ele já cria um Mono
         * que retorna um erro. */
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
