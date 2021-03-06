package com.learnreactivespring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document // Equivalente a @Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCapped {

    @Id
    private String id;

    private String description;

    private Double price;

}
