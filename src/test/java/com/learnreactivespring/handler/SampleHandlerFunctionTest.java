package com.learnreactivespring.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * O @WebFluxTest escaneia somente pacotes anotados com @Controller, e @Service,
 * ele não escaneia classes com @Component. Então, como nosso handler está anotado
 * com @Component, não podemos usar o @WebFluxTest. Por isso, colocamos o @SpringBootTest.
 * No entando, somente ele não é suficiente, pois ele não cria o WebTestClient, por isso
 * usamos ele em conjunto com o @AutoConfigureWebClient, que cria o WebTestClient.
 */
@SpringBootTest
@AutoConfigureWebTestClient
public class SampleHandlerFunctionTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void flux_approach1() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/functional/flux")
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
    public void mono() {
        Integer expectedValue = 1;
        webTestClient.get().uri("/functional/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    Assertions.assertEquals(expectedValue, response.getResponseBody());
                });
    }

}
