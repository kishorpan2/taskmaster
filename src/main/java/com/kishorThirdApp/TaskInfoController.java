package com.kishorThirdApp;

import com.amazonaws.services.dynamodbv2.xspec.L;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("")
public class TaskInfoController {
    @Autowired
    private S3Client s3Client;

    @Autowired
    TaskManagerRepo taskManagerRepo;

    @CrossOrigin
    @GetMapping("/task")
    public List<TaskInfo> getTask(){
        List<TaskInfo> all = (List)taskManagerRepo.findAll();
        return all;

    }
    @CrossOrigin
    @GetMapping("/users/{name}/tasks")
    public Iterable<TaskInfo> getAssignee(@PathVariable String name){
        Iterable<TaskInfo> userAll = taskManagerRepo.findByAssignee(name);
        return userAll;
    }

    @CrossOrigin
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
    @CrossOrigin
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
    @CrossOrigin
    @PutMapping("/tasks/{id}/assign/{assignee}")
    public void putAssignee(@PathVariable String id, @PathVariable String assignee){
        TaskInfo task = taskManagerRepo.findById(id).get();
        task.setAssignee(assignee);
        task.setStatus("Assigned");
        taskManagerRepo.save(task);
    }
    @CrossOrigin
    @PostMapping("/tasks/{id}/images")
    public RedirectView addImages(@PathVariable String id, @RequestPart(value="file")MultipartFile file ){
        TaskInfo selectedTask = taskManagerRepo.findById(id).get();
        ArrayList<String> picList = this.s3Client.uploadFile(file);
        selectedTask.setImageUrl(picList.get(0));
        selectedTask.setResizedImage(picList.get(1));
        taskManagerRepo.save(selectedTask);
        List<TaskInfo> allTask = (List)taskManagerRepo.findAll();
       // return new RedirectView("localhost:5000/task");
        return new RedirectView("http://imagebucketer.s3-website-us-east-1.amazonaws.com");
    }
}
