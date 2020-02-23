package broker.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerContext;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DestinationFactory;
import org.apache.activemq.broker.region.DestinationFactoryImpl;
import org.apache.activemq.broker.region.DestinationInterceptor;
import org.apache.activemq.broker.region.virtual.CompositeQueue;
import org.apache.activemq.broker.region.virtual.VirtualDestination;
import org.apache.activemq.broker.region.virtual.VirtualDestinationInterceptor;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.plugin.RuntimeConfigurationPlugin;
import org.apache.activemq.plugin.java.JavaRuntimeConfigurationBroker;
import org.apache.activemq.spring.SpringBrokerContext;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.xbean.XBeanBrokerService;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import java.util.Arrays;

/**
 * A Spring configuration class that is responsible for setting up the complete activemq broker .
 */
@Configuration
public class CustomActiveMQBrokerConfig {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * The BasicDataSource bean is responsible for setting up the persistence or non-persistence configuration
     * of our broker .
     * @return : The BasicDataSource bean geared up for mysql .
     */
     @Bean
    public BasicDataSource basicDataSource(){
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://localhost/ACTIVEMQ_DATA?relaxAutoCommit=true");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("mib");
        basicDataSource.setPoolPreparedStatements(true);
        return basicDataSource;
    }


    @Bean
    public BrokerContext brokerContext(){
         SpringBrokerContext brokerContext = new SpringBrokerContext();
         brokerContext.setApplicationContext(applicationContext);
         brokerContext.setConfigurationUrl("tcp://localhost:61616");
         return brokerContext;
    }

    /**
     * The BrokerService bean that is creating the actual ActiveMQ broker
     * @return : The BrokerService bean that is ready for injection  .
     * @throws Exception
     */
    @Bean
    public BrokerService brokerService() throws Exception {
        JDBCPersistenceAdapter adapter = new JDBCPersistenceAdapter();
        adapter.setDataDirectory("data-dir");
        adapter.setDataSource(basicDataSource());

        //RuntimeConfigurationPlugin runtimeConfigurationPlugin = new RuntimeConfigurationPlugin();
        //runtimeConfigurationPlugin.setCheckPeriod(1000);

        BrokerService brokerService = new BrokerService();
        brokerService.addConnector("tcp://localhost:61616");
        brokerService.setBrokerName("localhost");
        brokerService.setPersistenceAdapter(adapter);
      //  brokerService.setPlugins(new RuntimeConfigurationPlugin[]{runtimeConfigurationPlugin});
        brokerService.setBrokerContext(brokerContext());

        JavaRuntimeConfigurationBroker b = new JavaRuntimeConfigurationBroker(brokerService.getBroker());



        VirtualDestinationInterceptor virtualDestinationInterceptor = new VirtualDestinationInterceptor();
        CompositeQueue compositeQueue = new CompositeQueue();
        Queue queue = new ActiveMQQueue("DUTCH");
        compositeQueue.setName("ARTHUR");
        compositeQueue.setForwardTo(Arrays.asList(queue));
        virtualDestinationInterceptor.setVirtualDestinations(new VirtualDestination[]{compositeQueue});
        DestinationInterceptor[] destinationInterceptors = new DestinationInterceptor[]{virtualDestinationInterceptor};

        b.setVirtualDestinations(new VirtualDestination[]{compositeQueue},true);






        // end here

        return brokerService;
    }


    /**
     * The connectionfactory for connecting to the broker .
     * @return
     */
  @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setUserName("admin");
        factory.setPassword("admin");
        return factory;
    }


}

