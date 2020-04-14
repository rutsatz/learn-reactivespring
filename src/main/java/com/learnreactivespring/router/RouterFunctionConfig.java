package com.learnreactivespring.router;

import com.learnreactivespring.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Mapeia os requests recebidos aos handlers.
 */
@Configuration
public class RouterFunctionConfig {

    /**
     * Usa o RouterFunction para fazer o mapeamento de maneira funcional. Injetamos nossa classe handler
     * como parâmetro para dizer qual método deve ser chamado.
     *
     * @param handlerFunction
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction) {

        return RouterFunctions
                .route(RequestPredicates.GET("/functional/flux")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON))
                        , handlerFunction::flux)
                .andRoute(RequestPredicates.GET("/functional/mono")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON))
                        , handlerFunction::mono);

    }

}
