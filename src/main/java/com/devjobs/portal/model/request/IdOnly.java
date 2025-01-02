package com.devjobs.portal.model.request;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class IdOnly {
    private ObjectId jobId;
}
