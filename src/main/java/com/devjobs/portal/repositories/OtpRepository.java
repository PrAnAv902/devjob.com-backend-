package com.devjobs.portal.repositories;

import com.devjobs.portal.entities.Otp;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpRepository extends MongoRepository<Otp, ObjectId> {
}
