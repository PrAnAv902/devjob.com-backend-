package com.devjobs.portal.repositories;

import com.devjobs.portal.entities.Job;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRepository extends MongoRepository<Job, ObjectId> {
}
