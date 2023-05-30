package com.log.elasticsearch.repository;

import com.log.elasticsearch.entity.Logjboss;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LogjbossRepository extends ElasticsearchRepository<Logjboss, String> {

    Logjboss findByMessage(String message);

}
