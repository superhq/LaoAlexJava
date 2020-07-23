package com.example.laoalexelasticsearch.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "author")
@Getter
@Setter
@ToString
public class Author {
    @Id
    private String id;
    private String name;

    public Author(String name) {
        this.name = name;
    }
}
