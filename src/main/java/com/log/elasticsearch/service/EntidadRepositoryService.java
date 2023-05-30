package com.log.elasticsearch.service;

import com.log.elasticsearch.entity.Entidad;
import com.log.elasticsearch.repository.EntidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EntidadRepositoryService {

    private final EntidadRepository entidadRepository;

    public List<Entidad> findEntidads() {

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(entidadRepository.findAll().iterator(), 0), false)
                .collect(Collectors.toList());
    }

    public Entidad findEntidadByName(String message) {

        return entidadRepository.findByMessage(message);
    }

    public Entidad save(final Entidad entidad) {
        return entidadRepository.save(entidad);
    }

    public Optional<Entidad> findById(final String id) {
        return entidadRepository.findById(id);
    }
}
