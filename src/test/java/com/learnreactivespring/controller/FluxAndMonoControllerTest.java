package com.learnreactivespring.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@WebFluxTest
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

}
