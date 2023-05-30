package com.log.elasticsearch.service;

import com.log.elasticsearch.entity.Entidad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntidadRestTemplateService {

    private static final String INDEX = ".ds-filebeat-8.7.1-2023.05.17-000001";

    private final ElasticsearchOperations elasticsearchOperations;

    public List<String> createEntidadIndexBulk(final List<Entidad> entidads) {

        List<IndexQuery> queries = entidads.stream()
                .map(entidad
                        -> new IndexQueryBuilder()
                        .withId(entidad.getId())
                        .withObject(entidad).build())
                .collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(INDEX))
                .stream()
                .map(object -> object.getId())
                .collect(Collectors.toList());
    }

    public String createEntidadIndex(Entidad entidad) {

        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(entidad.getId())
                .withObject(entidad).build();

        String documentId = elasticsearchOperations.index(indexQuery, IndexCoordinates.of(INDEX));

        return documentId;
    }

    public List<Entidad> findEntidadByMessage(final String message) {

        QueryBuilder queryBuilder = QueryBuilders.matchQuery("message", message);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<Entidad> hits = elasticsearchOperations.search(searchQuery, Entidad.class, IndexCoordinates.of(INDEX));

        return hits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

    }

    public List<Entidad> findByMessage(final String message) {
        Query searchQuery = new StringQuery(
                "{\"match\":{\"message\":{\"query\":\"" + message + "\"}}}\"");

        SearchHits<Entidad> entidads = elasticsearchOperations.search(
                searchQuery,
                Entidad.class,
                IndexCoordinates.of(INDEX));

        return entidads.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

    }

    public List<Entidad> findByEntidad(final String entidad) {
        Criteria criteria = new Criteria("name")
                .greaterThan(10.0)
                .lessThan(100.0);

        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<Entidad> entidads = elasticsearchOperations
                .search(searchQuery,
                        Entidad.class,
                        IndexCoordinates.of(INDEX));

        return entidads.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    public List<Entidad> processSearch(final String query) {
        log.info("Search with query {}", query);

        QueryBuilder queryBuilder
                = QueryBuilders
                        .multiMatchQuery(query, "message")
                        .fuzziness(Fuzziness.AUTO); //crea todas las posibles variaciones

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();

        SearchHits<Entidad> entidads
                = elasticsearchOperations
                        .search(searchQuery, Entidad.class,
                                IndexCoordinates.of(INDEX));

        return entidads.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }
}
