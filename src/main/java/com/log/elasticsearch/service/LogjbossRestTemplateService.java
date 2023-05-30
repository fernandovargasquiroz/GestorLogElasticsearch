package com.log.elasticsearch.service;

import com.log.elasticsearch.entity.Logjboss;

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
public class LogjbossRestTemplateService {

    private static final String INDEX = "logjboss";

    private final  ElasticsearchOperations elasticsearchOperations;

    public List<String> createLogjbossIndexBulk(final List<Logjboss> logjboss) {

        List<IndexQuery> queries;
        queries = logjboss.stream()
                .map(logjbos-> new IndexQueryBuilder()
                        .withId(logjbos.getId())
                        .withObject(logjbos).build())
                .collect(Collectors.toList());

        return elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of(INDEX))
                .stream()
                .map(object->object.getId())
                .collect(Collectors.toList());
    }

    public String createLogjbossIndex(Logjboss logjboss) {

        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(logjboss.getId())
                .withObject(logjboss).build();

        String documentId = elasticsearchOperations.index(indexQuery, IndexCoordinates.of(INDEX));

        return documentId;
    }

    public List<Logjboss> findLogjbossByMessage(final String message) {

        QueryBuilder queryBuilder = QueryBuilders.matchQuery("message", message);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();

        SearchHits<Logjboss> hits = elasticsearchOperations.search(searchQuery, Logjboss.class, IndexCoordinates.of(INDEX));

         return hits.stream()
                 .map(hit->hit.getContent())
                 .collect(Collectors.toList());

    }

    public List<Logjboss> findByMessage(final String message) {
        Query searchQuery = new StringQuery(
                "get _search\n" +
                "{\n" +
                "  \"track_total_hits\": false,\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"_score\": {\n" +
                "        \"order\": \"desc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"field\": \"*\",\n" +
                "      \"include_unmapped\": \"true\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"size\": 500,\n" +
                "  \"version\": true,\n" +
                "  \"script_fields\": {},\n" +
                "  \"stored_fields\": [\n" +
                "    \"*\"\n" +
                "  ],\n" +
                "  \"runtime_mappings\": {},\n" +
                "  \"_source\": false,\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"bool\": {\n" +
                "            \"should\": [\n" +
                "              {\n" +
                "                \"query_string\": {\n" +
                "                  \"fields\": [\n" +
                "                    \"message\"\n" +
                "                  ],\n" +
                "                  \"query\": \"*Error*\"\n" +
                "                }\n" +
                "              }\n" +
                "            ],\n" +
                "            \"minimum_should_match\": 1\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"filter\": [\n" +
                "        {\n" +
                "          \"bool\": {\n" +
                "            \"must\": [],\n" +
                "            \"filter\": [\n" +
                "              {\n" +
                "                \"match_phrase\": {\n" +
                "                  \"_index\": \"logjboss\"\n" +
                "                }\n" +
                "              },\n" +
                "              {\n" +
                "                \"exists\": {\n" +
                "                  \"field\": \"message\"\n" +
                "                }\n" +
                "              }\n" +
                "            ],\n" +
                "            \"should\": [],\n" +
                "            \"must_not\": []\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"should\": [],\n" +
                "      \"must_not\": []\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\": {\n" +
                "    \"pre_tags\": [\n" +
                "      \"@kibana-highlighted-field@\"\n" +
                "    ],\n" +
                "    \"post_tags\": [\n" +
                "      \"@/kibana-highlighted-field@\"\n" +
                "    ],\n" +
                "    \"fields\": {\n" +
                "      \"*\": {}\n" +
                "    },\n" +
                "    \"fragment_size\": 2147483647\n" +
                "  }\n" +
                "}");

        SearchHits<Logjboss> logjboss = elasticsearchOperations.search(
                searchQuery,
                Logjboss.class,
                IndexCoordinates.of(INDEX));

        return logjboss.stream()
                .map(hit->hit.getContent())
                .collect(Collectors.toList());

    }

    public List<Logjboss> findByUser(final String user) {
        Criteria criteria = new Criteria("name")
                .greaterThan(10.0)
                .lessThan(100.0);

        Query searchQuery = new CriteriaQuery(criteria);

        SearchHits<Logjboss> logjboss = elasticsearchOperations
                .search(searchQuery,
                        Logjboss.class,
                        IndexCoordinates.of(INDEX));

        return logjboss.stream()
                .map(hit->hit.getContent())
                .collect(Collectors.toList());
    }

    public List<Logjboss> processSearch(final String query) {
        log.info("Search with query {}", query);

        QueryBuilder queryBuilder =
                QueryBuilders
                        .multiMatchQuery(query, "message")
                        .fuzziness(Fuzziness.AUTO); //crea todas las posibles variaciones

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(queryBuilder)
                .build();

        SearchHits<Logjboss> logjboss =
                elasticsearchOperations
                        .search(searchQuery, Logjboss.class,
                                IndexCoordinates.of(INDEX));

        return logjboss.stream()
                .map(hit->hit.getContent())
                .collect(Collectors.toList());
    }
}
