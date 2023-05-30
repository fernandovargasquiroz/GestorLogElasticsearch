package com.log.elasticsearch.service;

import com.log.elasticsearch.entity.Logjboss;
import com.log.elasticsearch.repository.LogjbossRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class LogjbossRepositoryService {

    private final LogjbossRepository logjbossRepository;

    public void createLogjbossIndexBulk(final List<Logjboss> logjboss) {
        logjbossRepository.saveAll(logjboss);
    }

    public Logjboss createLogjbossIndex(final Logjboss logjboss) {
        return logjbossRepository.save(logjboss);
    }

    public List<Logjboss> findLogjboss() {

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(logjbossRepository.findAll().iterator(), 0), false)
                .collect(Collectors.toList());
    }

    public Logjboss findLogjbossByMessage(String message) {

        return logjbossRepository.findByMessage(message);
    }
}
