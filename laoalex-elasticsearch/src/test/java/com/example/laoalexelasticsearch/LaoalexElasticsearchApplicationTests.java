package com.example.laoalexelasticsearch;

import com.example.laoalexelasticsearch.entity.Article;
import com.example.laoalexelasticsearch.entity.Author;
import com.example.laoalexelasticsearch.repository.ArticleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.Queue;

import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;

@SpringBootTest
class LaoalexElasticsearchApplicationTests {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void contextLoads() {
    }



    //检查相应的索引是否存在，如果spring.data.elasticsearch.repositories.enabled=True,则会自动创建索引
    private boolean checkIndexExists(Class<?> cls){
        boolean isExist = elasticsearchRestTemplate.indexOps(cls).exists();
        //获取索引名
        String indexName = cls.getAnnotation(Document.class).indexName();
        System.out.printf("index %s is %s\n", indexName, isExist ? "exist" : "not exist");
        return isExist;
    }
    @Test
    void checkIndex() {
        checkIndexExists(Article.class);
        checkIndexExists(Author.class);
    }

    private void _deleteIndex(Class<?> cls){
        if(checkIndexExists(cls)){
            String indexName = cls.getAnnotation(Document.class).indexName();
            elasticsearchRestTemplate.indexOps(cls).delete();
        }
    }
    @Test
    void deleteIndex(){
        _deleteIndex(Article.class);
        _deleteIndex(Author.class);
    }
    @Test
     void save(){
        Article article = new Article("Spring Data Elasticsearch");
        article.setAuthors(asList(new Author("LaoAlex"),new Author("John")));
        articleRepository.save(article);

        article = new Article("Spring Data Elasticsearch2");
        article.setAuthors(asList(new Author("LaoAlex"),new Author("King")));
        articleRepository.save(article);

        article = new Article("Spring Data Elasticsearch3");
        article.setAuthors(asList(new Author("LaoAlex"),new Author("Bill")));
        articleRepository.save(article);
    }

    @Test
    void queryAuthorName() throws JsonProcessingException {
        Page<Article> articles = articleRepository.findByAuthorsName("LaoAlex", PageRequest.of(0,10));
        //将对象转为Json字符串
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(articles);
        System.out.println(json);
    }

    //使用自定义查询
    @Test
    void queryAuthorNameByCustom() throws JsonProcessingException {
        Page<Article> articles = articleRepository.findByAuthorsNameUsingCustomQuery("John",PageRequest.of(0,10));
        //将对象转为Json字符串
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(articles);
        System.out.println(json);
    }

    //关键字查询
    @Test
    void queryTileContain() throws JsonProcessingException {
        Page<Article> articles = articleRepository.findByTitleIsContaining("2",PageRequest.of(0,10));
        //将对象转为Json字符串
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(articles);
        System.out.println(json);
    }
    //使用Template进行关键字查询
    //关于正则表达式可以参考https://www.runoob.com/java/java-regular-expressions.html
    //.*data.* 可以匹配ddata, dataa等
    @Test
    void queryTileContainByTemplate() throws JsonProcessingException {
        Query query = new NativeSearchQueryBuilder().withFilter(regexpQuery("title",".*elasticsearch2.*")).build();
        SearchHits<Article> articles = elasticsearchRestTemplate.search(query, Article.class, IndexCoordinates.of("blog"));
        //将对象转为Json字符串
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(articles);
        System.out.println(json);
    }

    //取出所有内容
    @Test
    void getAll(){
        Iterable<Article> articles = articleRepository.findAll();
        for  (Article article:articles) {
            System.out.println(article);
        }

    }

    //更新操作
    @Test
    void update() throws JsonProcessingException {
        Page<Article> articles = articleRepository.findByTitle("Spring Data Elasticsearch",PageRequest.of(0,10));
        //将对象转为Json字符串
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = objectWriter.writeValueAsString(articles);
        System.out.println(json);

        Article article = articles.getContent().get(0);
        System.out.println(article);
        article.setAuthors(null);
        articleRepository.save(article);
    }

    //删除操作
    @Test
    void delete(){
        Page<Article> articles = articleRepository.findByTitle("Spring Data Elasticsearch",PageRequest.of(0,10));
        Article article = articles.getContent().get(0);
        articleRepository.delete(article);
    }
}