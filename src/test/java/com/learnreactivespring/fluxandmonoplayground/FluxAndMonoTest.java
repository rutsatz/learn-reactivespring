package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {

        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactor Spring");
        /* A única maneira de acessar os dados do flux é através do Subscribe. Quando fazemos o subscribe,
        o flux começa a emitir os valores para o subscriber.
        Na programação imperativa, as exceptions são tratadas com try/catch. Na reativa, passamos handlers que
        são chamados quando um erro acontece. No subscribe, o segundo parâmetro recebe a função que é chamada
        quando algum erro acontece.
        */
        stringFlux.subscribe(System.out::println);

    }

}
