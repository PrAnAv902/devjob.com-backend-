package com.devjobs.portal.repositories;


import com.devjobs.portal.entities.Company;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, ObjectId> {
}

