package broker.service;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationFactory;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.virtual.CompositeQueue;
import org.apache.activemq.broker.region.virtual.VirtualDestination;
import org.apache.activemq.broker.region.virtual.VirtualDestinationInterceptor;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * A custom service class that has different utility type methods of interacting with the broker .
 * Like getting all the queues or creating queues / virtual destination at runtime .
 */
@Service
public class ActiveMQCustomService {

    private static final Logger LOGGER = LoggerFactory.getLogger("ActiveMQCustomService");

    private static final String MASTER_QUEUE_NAME = "MASTER-QUEUE";

    @Autowired
    private ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    private BrokerService brokerService;



    public void createQueue(String queueName) {
        LOGGER.info("Creating a physical queue [ " + queueName + " ] and linking it to master queue [ " + MASTER_QUEUE_NAME  + " ] ");
        try {

            VirtualDestinationInterceptor virtualDestinationInterceptor = new VirtualDestinationInterceptor();
            CompositeQueue compositeQueue = new CompositeQueue();
            Queue queue = new ActiveMQQueue(queueName);
            compositeQueue.setName(MASTER_QUEUE_NAME);
            compositeQueue.setForwardTo(Arrays.asList(queue));
            virtualDestinationInterceptor.setVirtualDestinations(new VirtualDestination[]{compositeQueue});
            DestinationInterceptor[] destinationInterceptors = new DestinationInterceptor[]{virtualDestinationInterceptor};
            brokerService.setDestinationInterceptors(destinationInterceptors);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<String> getAllQeueus(){
        List<String> queueList = null;
        try {
            ActiveMQConnection connection = (ActiveMQConnection) activeMQConnectionFactory.createConnection();
            connection.start();
            DestinationSource source = connection.getDestinationSource();
            Set<ActiveMQQueue> queues = source.getQueues();
            queueList = new ArrayList<>();


            for (ActiveMQQueue queue : queues) {
                LOGGER.info(queue.getPhysicalName());
                queueList.add(queue.getPhysicalName());
            }
            connection.stop();

        }catch (Exception e){
            e.printStackTrace();
        }
        return queueList;
    }

}
