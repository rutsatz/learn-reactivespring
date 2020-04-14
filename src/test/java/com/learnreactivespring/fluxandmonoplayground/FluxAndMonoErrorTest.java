package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                /* Após ocorrer um erro, o fluxo é interrompido, pois o erro é predominante. */
                .concatWith(Flux.just("D"))
                /* Ao ocorrer um erro, esse bloco é executado. Ele retorna um flux, que continua a ser executado.
                 * Essa é uma das maneiras de tratar exceptions.*/
                .onErrorResume(e -> {
                    /* Somente imprime a exception. */
                    System.out.println("Exception is : " + e);
                    /* Retorna o Flux que será processado após a exception. */
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                //.verify();
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                /* Após ocorrer um erro, o fluxo é interrompido, pois o erro é predominante. */
                .concatWith(Flux.just("D"))
                /* Em caso de erro, retorna esse valor. */
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                /* Após ocorrer um erro, o fluxo é interrompido, pois o erro é predominante. */
                .concatWith(Flux.just("D"))
                /* Converte uma exception para um tipo customizado. */
                .onErrorMap(e -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                /* Após ocorrer um erro, o fluxo é interrompido, pois o erro é predominante. */
                .concatWith(Flux.just("D"))
                /* Converte uma exception para um tipo customizado. */
                .onErrorMap(e -> new CustomException(e))
                /* Em caso de erro, tenta executar novamente antes de lançar a exception. Nesse caso, executa
                 * novamente duas vezes. */
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                /* Após ocorrer um erro, o fluxo é interrompido, pois o erro é predominante. */
                .concatWith(Flux.just("D"))
                /* Converte uma exception para um tipo customizado. */
                .onErrorMap(e -> new CustomException(e))
                /* O retryBackoff é igual o retry, só que ele espera um tempo antes de fazer uma nova tentativa. */
                .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                /* Tenho que capturar essa exceção pois é a que retry joga quando atinge o limite de retries,
                 * mas ainda está em erro. */
                .expectError(IllegalStateException.class)
                .verify();
    }

}
