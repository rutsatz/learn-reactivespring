package com.learnreactivespring.handler;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * Usa o @ActiveProfiles para rodar no Profile de teste, e evitar que quando subir o
 * contexto da aplicação, seja executado o CommandLineRunner e feito a conexão com banco
 * e os inserts. Pois lá, tem um @Profile("!test").
 */
@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats HeadPhones", 19.99));
    }

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is : " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                /* Verifica o preço retornado no json. */
                .jsonPath("$.price", 149.99);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

}
