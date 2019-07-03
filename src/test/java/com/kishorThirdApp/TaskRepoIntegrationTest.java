package com.kishorThirdApp;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskManagerApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
public class TaskRepoIntegrationTest {
    private DynamoDBMapper dynamoDBMapper;



    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    TaskManagerRepo repository;

    private static final String EXPECTED_TITLE = "LABS";
    private static final String EXPECTED_DESCRIPTION = "NOT EASY";
    private static final String EXPECTED_STATUS = "INPROGRESS";

    @Before
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(TaskInfo.class);

        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        dynamoDBMapper.batchDelete((List<TaskInfo>)repository.findAll());
    }

//    @Test
//    public void readWriteTestCase() {
//        TaskInfo dave = new TaskInfo(EXPECTED_TITLE, EXPECTED_DESCRIPTION, EXPECTED_STATUS);
//        repository.save(dave);
//
//        List<TaskInfo> result = (List<TaskInfo>) repository.findAll();
//
//        assertTrue("Not empty", result.size() > 0);
//        assertTrue("Contains item with expected TITLE", result.get(0).getTitle().equals(EXPECTED_TITLE));
//    }
}
