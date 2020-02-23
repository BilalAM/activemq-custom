package broker;


import org.apache.activemq.broker.BrokerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args){
        SpringApplication.run(Main.class,args);
    }
}
