package com.lifeofbees.monitor.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MonitoringEventRepository
        extends MongoRepository<MonitoringEvent, String> {
}