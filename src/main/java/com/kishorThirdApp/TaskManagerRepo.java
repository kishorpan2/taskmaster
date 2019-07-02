package com.kishorThirdApp;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@EnableScan
public interface TaskManagerRepo extends CrudRepository<TaskInfo, String> {
    Optional<TaskInfo> findById(String id);
}
