package broker.controller;


import broker.service.ActiveMQCustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActiveMQController {

    @Autowired
    ActiveMQCustomService brokerCustomService;


    @GetMapping("/create")
    public ResponseEntity<String> createQueue(@RequestParam(name = "name", required = true) String queueName){
        brokerCustomService.createQueue(queueName);
        return new ResponseEntity<>(queueName,HttpStatus.OK);
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<String>> getQueues(){
        return new ResponseEntity<>(  brokerCustomService.getAllQeueus(), HttpStatus.OK);
    }

}
