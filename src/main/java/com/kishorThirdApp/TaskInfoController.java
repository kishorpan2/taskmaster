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

    @GetMapping("/users/{name}/tasks")
    public Iterable<TaskInfo> getAssignee(@PathVariable String name){
        Iterable<TaskInfo> userAll = taskManagerRepo.findByAssignee(name);
        return userAll;
    }


    @PostMapping("/tasks")
    public TaskInfo postTask(@RequestParam String title, @RequestParam String description,
                             @RequestParam(required =false, defaultValue = "") String assignee){
        TaskInfo newUser;
        if (assignee.equals("")){
             newUser = new TaskInfo(title, description);
            taskManagerRepo.save(newUser);
        }else{
            newUser = new TaskInfo(title, description,assignee);
            taskManagerRepo.save(newUser);
        }
      return newUser;
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
    @PutMapping("/tasks/{id}/assign/{assignee}")
    public void putAssignee(@PathVariable String id, @PathVariable String assignee){
        TaskInfo task = taskManagerRepo.findById(id).get();
        task.setAssignee(assignee);
        task.setStatus("Assigned");
        taskManagerRepo.save(task);
    }
}
