package com.learnreactivespring.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Classe handler.
 * Abordagem funcional para os mapeamentos.
 * O Router recebe as requisições e se for uma mapeamento válido, envia para o Handler.
 */
@Component
public class SampleHandlerFunction {

    /**
     * O ServerRequest seria o equivalente ao HtppRequest e o ServerResponse ao HttpResponse.
     * Como o response é um, ele é do tipo mono. Mas os dados podem ser vários, então esse
     * Mono<ServerResponse> encapsula o Flux.
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> flux(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Flux.just(1, 2, 3, 4)
                                .log()
                        , Integer.class
                );
    }

    /**
     * O ServerRequest seria o equivalente ao HtppRequest e o ServerResponse ao HttpResponse.
     * Como o response é um, ele é do tipo mono. Mas os dados podem ser vários, então esse
     * Mono<ServerResponse> encapsula o Flux.
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> mono(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(1)
                                .log()
                        , Integer.class
                );
    }

}
