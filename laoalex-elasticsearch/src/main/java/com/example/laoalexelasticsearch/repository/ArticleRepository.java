package com.example.laoalexelasticsearch.repository;

import com.example.laoalexelasticsearch.entity.Article;
import com.example.laoalexelasticsearch.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article,String> {

    //下面的这两个查询的作用是一样的。一个采用默认的实现方式，一个采用自定义的实现方式
    Page<Article> findByAuthorsName(String name, Pageable pageable);
    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);

    //搜索title字段
    Page<Article> findByTitleIsContaining(String word,Pageable pageable);

    Page<Article> findByTitle(String title,Pageable pageable);
}
