package com.kishorThirdApp;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


@EnableScan
public interface TaskManagerRepo extends CrudRepository<TaskInfo, String> {
    Optional<TaskInfo> findById(String id);
    Iterable<TaskInfo> findByAssignee(String assignee);
}
