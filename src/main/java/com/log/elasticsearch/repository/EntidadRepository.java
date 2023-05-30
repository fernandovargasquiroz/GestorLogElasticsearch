package com.log.elasticsearch.repository;

import com.log.elasticsearch.entity.Entidad;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EntidadRepository extends ElasticsearchRepository<Entidad, String> {

    Entidad findByMessage(String message);

}
