package com.kishorThirdApp;

import com.amazonaws.regions.Regions;

import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;


import java.util.*;


@RestController
@CrossOrigin
//@RequestMapping("")
public class TaskInfoController {
    @Autowired
    private S3Client s3Client;

    @Autowired
    TaskManagerRepo taskManagerRepo;

    @CrossOrigin
    @GetMapping("/task")
    public List<TaskInfo> getTask() {
        List<TaskInfo> all = (List) taskManagerRepo.findAll();
        return all;

    }

    @CrossOrigin
    @GetMapping("/users/{name}/tasks")
    public Iterable<TaskInfo> getAssignee(@PathVariable String name) {
        Iterable<TaskInfo> userAll = taskManagerRepo.findByAssignee(name);
        return userAll;
    }

    @CrossOrigin
    @PostMapping("/tasks")
    public TaskInfo postTask(@RequestParam String title, @RequestParam String description,
                             @RequestParam(required = false, defaultValue = "") String assignee) {
        TaskInfo newUser;
        if (assignee.equals("")) {
            newUser = new TaskInfo(title, description);
            taskManagerRepo.save(newUser);
        } else {
            newUser = new TaskInfo(title, description, assignee);
            taskManagerRepo.save(newUser);
        }
        return newUser;
    }

    @CrossOrigin
    @PutMapping("/tasks/{id}/state")
    public void putTask(@PathVariable String id) {
        TaskInfo task = taskManagerRepo.findById(id).get();
        if (task.getStatus().equals("Available")) {
            task.setStatus("Assigned");
            sendMessege(task);
        } else if (task.getStatus().equals("Assigned")) {
            task.setStatus("Accepted");

        } else if (task.getStatus().equals("Accepted")) {
            task.setStatus("Completed");
            sendEmail(task);
        }
        taskManagerRepo.save(task);

    }

    @CrossOrigin
    @PutMapping("/tasks/{id}/assign/{assignee}")
    public void putAssignee(@PathVariable String id, @PathVariable String assignee) {
        TaskInfo task = taskManagerRepo.findById(id).get();
        task.setAssignee(assignee);
        task.setStatus("Assigned");
        taskManagerRepo.save(task);
    }

    @CrossOrigin
    @PostMapping("/tasks/{id}/images")
    public RedirectView addImages(@PathVariable String id, @RequestPart(value = "file") MultipartFile file) {
        TaskInfo selectedTask = taskManagerRepo.findById(id).get();
        ArrayList<String> picList = this.s3Client.uploadFile(file);
        selectedTask.setImageUrl(picList.get(0));
        selectedTask.setResizedImage(picList.get(1));
        taskManagerRepo.save(selectedTask);
        List<TaskInfo> allTask = (List) taskManagerRepo.findAll();
        return new RedirectView("localhost:3000");
        //return new RedirectView("http://imagebucketer.s3-website-us-east-1.amazonaws.com");
    }

    public void sendMessege(TaskInfo task) {

        AmazonSNSClient snsClient = new AmazonSNSClient();
        String message = task + " has completed.";
        String phoneNumber = "+12182097063";
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        //<set SMS attributes>
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result); // Prints the message ID.
    }

    public void sendEmail(TaskInfo taskName) {
        String FROM = "Kishor.jpandey@gmail.com";
        String TO = "Kishor_young@yahoo.com";
        String CONFIGSET = "ConfigSet";
        // The subject line for the email.
        String SUBJECT = " Task: " + taskName + " is completed";
        String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>";
        String TEXTBODY = taskName + " is completed";

        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            // Replace US_WEST_2 with the AWS Region you're using for
                            // Amazon SES.
                            .withRegion(Regions.US_EAST_1).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(TO))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(HTMLBODY))
                                    .withText(new Content()
                                            .withCharset("UTF-8").withData(TEXTBODY)))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(SUBJECT)))
                    .withSource(FROM);
            // Comment or remove the next line if you are not using a
            // configuration set
            //.withConfigurationSetName(CONFIGSET);
            client.sendEmail(request);
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: "
                    + ex.getMessage());
        }
    }
}
