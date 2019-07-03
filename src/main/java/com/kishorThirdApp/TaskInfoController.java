package com.kishorThirdApp;

import com.amazonaws.services.dynamodbv2.xspec.L;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TaskInfoController {

    @Autowired
    TaskManagerRepo taskManagerRepo;

    @GetMapping("/task")
    public List<TaskInfo> getTask(){
        List<TaskInfo> all = (List)taskManagerRepo.findAll();
        return all;

    }
    @PostMapping("/tasks")
    public void postTask(String title, String description, String status){
        TaskInfo newUser = new TaskInfo(title, description, status);
        System.out.println(status + " " + title);
        System.out.println("******************** " + newUser.getStatus());
        taskManagerRepo.save(newUser);
    }
    @PutMapping("/tasks/{id}/state")
    public void putTask(@PathVariable String id) {
        TaskInfo task = taskManagerRepo.findById(id).get();
        if (task.getStatus().equals("Available")) {
            task.setStatus("Assigned"); }
        else if (task.getStatus().equals("Assigned")) {
            task.setStatus("Accepted"); }
        else if (task.getStatus().equals("Accepted")) {
            task.setStatus("Completed");
        }
        taskManagerRepo.save(task);


    }
}
