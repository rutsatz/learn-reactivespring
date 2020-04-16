package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Não deve ser executado no profile de teste.
 */
@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @Autowired
    private ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {

        initialDataSetUp();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty()
                /* Limite máximo de documentos. */
                .maxDocuments(20)
                /* Tamanho da collection, em bytes. */
                .size(50000)
                /* Diz que é uma Capped Collection. */
                .capped());
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch", 349.99),
                new Item("ABC", "Beats HeadPhones", 19.99));
    }

    private void initialDataSetUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> System.out.println("Item inserted from CommandLineRunner : " + item));
    }

    private void dataSetUpForCappedCollection() {
        /* Cria um novo item a cada segundo. */
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item " + i, 100.0 + i));

        itemReactiveCappedRepository
                /* O insert faz o subscribe no flux, e a cada novo item emitido, ele faz o insert na
                 * base. */
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> {
                    log.info("Inserted Item is " + itemCapped);
                });
    }

}
