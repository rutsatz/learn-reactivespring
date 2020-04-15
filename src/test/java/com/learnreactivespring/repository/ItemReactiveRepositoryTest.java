package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

/**
 * @DataMongoTest carrega apenas as classes necessárias para fazer o teste com o mongo embarcado.
 */
@DataMongoTest
public class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0)
            , new Item(null, "LG TV", 420.0)
            , new Item(null, "Apple Watch", 299.99)
            , new Item(null, "Beats Headphones", 149.99)
            , new Item("ABC", "Bose Headphones", 149.99)
    );

    @BeforeEach
    public void setUp() {
        /* Limpa os dados anteriores. */
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                /* flatMap: É diferente do flatMap da StreamAPI. flatMap no projeto reactor
                 * acessa o objeto atual e faz uma operação e returna um tipo reativo. Por exemplo,
                 * ele acessa o ItemObject e faz a opereração de save e retorna um Mono<Item> que
                 * ele salvou.*/
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted Item is : " + item))
                /* Espera todas as operações terminarem. Fazemos isso para ter certeza que
                 * quando os testes começarem a executar, todos os dados foram inseridos.
                 * Atenção: Não deve ser usado em produção, somente em casos de teste. */
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByID() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();

    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones")
                .log("findItemByDescription : "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

}
