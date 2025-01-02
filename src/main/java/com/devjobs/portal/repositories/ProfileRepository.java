package com.devjobs.portal.repositories;

import com.devjobs.portal.entities.Profile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProfileRepository extends MongoRepository<Profile, ObjectId>{
}
