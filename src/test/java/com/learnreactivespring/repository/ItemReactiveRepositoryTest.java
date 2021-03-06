package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

/**
 * @DataMongoTest carrega apenas as classes necessárias para fazer o teste com o mongo embarcado.
 * @DirtiesContext é necessário quando vamos fazer testes que alteram o contexto da aplicação, como
 * por exemplo, o saveItem(). Se eu rodo o teste individual, sem colocar essa annotation, ele passa,
 * mas quando ele roda dentro do build do gradle, ele dá erro se não tivermos colocado ela.
 * Basicamente, cada novo teste executado, ganha um contexto novo.
 */
@DataMongoTest
@DirtiesContext
@ActiveProfiles("test")
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

    @Test
    public void saveItem() {
        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("saveItem : "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        double newPrice = 520.0;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                /* Converte Mono<Item> para o Item para poder salvar. Essa conversão não
                 * é possível usando o map(). */
                .flatMap(item -> itemReactiveRepository.save(item));

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.0)
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        Mono<Void> deletedItem = itemReactiveRepository
                /* Retorna um Mono<Item>. */
                .findById("ABC")
                /* Converte de Mono<Item> para String. */
                .map(Item::getId)
                /* Pega a String com o id e deleta. */
                .flatMap(id ->
                        /* deleteById retorna um Mono<Void>. */
                        itemReactiveRepository.deleteById(id));

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                /* Como é um delete, não podemos esperar nada de retorno, por isso
                 * chamamos direto o verifyComplete(), pois não pode haver nenhum outro
                 * evento além do subscribe e onComplete. */
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List : "))
                /* Garantir que ele foi deletado do banco, */
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemReactiveRepository
                /* Retorna um Mono<Item>. */
                .findByDescription("LG TV")
                /* Deleta usando direto o objeto.. */
                .flatMap(item ->
                        /* deleteById retorna um Mono<Void>. */
                        itemReactiveRepository.delete(item));

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                /* Como é um delete, não podemos esperar nada de retorno, por isso
                 * chamamos direto o verifyComplete(), pois não pode haver nenhum outro
                 * evento além do subscribe e onComplete. */
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List : "))
                /* Garantir que ele foi deletado do banco, */
                .expectNextCount(4)
                .verifyComplete();
    }

}
