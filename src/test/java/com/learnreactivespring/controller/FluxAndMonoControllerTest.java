package com.learnreactivespring.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

/**
 * @DirtiesContext é necessário quando vamos fazer testes que alteram o contexto da aplicação, como
 * por exemplo, o saveItem(). Se eu rodo o teste individual, sem colocar essa annotation, ele passa,
 * mas quando ele roda dentro do build do gradle, ele dá erro se não tivermos colocado ela.
 * Basicamente, cada novo teste executado, ganha um contexto novo.
 */
@WebFluxTest
@DirtiesContext
public class FluxAndMonoControllerTest {

    /**
     * WebTestClient é um client para fazer testes de endpoints não bloqueantes.
     * O @WebFluxTest que cria a instância dele pra gente.
     */
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                /* Faz a chamada ao endpoint. Atua como se fosse um subscriber. */
                .exchange()
                /* Espera que o resultado seja ok */
                .expectStatus().isOk()
                /* O resultado vai ser do tipo Integer. */
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                /* Espera receber a confirmação do subscription. */
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2() {
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                /* Faz a chamada ao endpoint. Atua como se fosse um subscriber. */
                .exchange()
                /* Espera que o resultado seja ok */
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_approach3() {
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);
        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                /* Faz a chamada ao endpoint. Atua como se fosse um subscriber. */
                .exchange()
                /* Espera que o resultado seja ok */
                .expectStatus().isOk()
                /* Converte o Flux para um List. Espera todos os eventos serem emitidos, ai faz a conversão. */
                .expectBodyList(Integer.class)
                /* Ao invés de fazer o assert, retornamos o resultado da chamada. */
                .returnResult();

        Assertions.assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4() {
        List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                /* Faz a chamada ao endpoint. Atua como se fosse um subscriber. */
                .exchange()
                /* Espera que o resultado seja ok */
                .expectStatus().isOk()
                /* Converte o Flux para um List. Espera todos os eventos serem emitidos, ai faz a conversão. */
                .expectBodyList(Integer.class)
                /* Usa um consumer. */
                .consumeWith(entityExchangeResult -> {
                    Assertions.assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
                });
    }

    @Test
    public void fluxStream() {

        Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                /* Faz a chamada ao endpoint. Atua como se fosse um subscriber. */
                .exchange()
                /* Espera que o resultado seja ok */
                .expectStatus().isOk()
                /* O resultado vai ser do tipo Integer. */
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                /* Como temos um stream infinito, não podemos testar todos os valores, então testamos
                 * os primeiros e mandamos o cancel(), para remover o subscription, e validamos
                 * os primeiros valores recebidos. */
                .thenCancel()
                .verify();

    }

    @Test
    public void mono() {

        Integer expectedValue = 1;

        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    Assertions.assertEquals(expectedValue, response.getResponseBody());
                });

    }

}
