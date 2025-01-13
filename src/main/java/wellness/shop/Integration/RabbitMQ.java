package wellness.shop.Integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {

    private final ConnectionFactory factory;
    ObjectMapper objectMapper;

    public RabbitMQ(String HOST) {

        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Send any object to rabbitMQ as json to selected destination.
     *
     */
    public <T> void sendObjectToQueue(T t, String destinationQueueName) {
        String jsonMessage = generateJson(t);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(destinationQueueName, false, false, false, null);

            channel.basicPublish("", destinationQueueName, null, jsonMessage.getBytes());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Takes all messages in queue and pops them one by one. New messages are kept in queue. This method requires RabbitMQMessageProcessor interfaces, where popMessageLogic need to be described.
     *
     */

    public <T> void popALlMessages(RabbitMQMessageProcessor<T> rabbitMQMessageProcessor, String destinationQueueName, Class<T> clazz) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(destinationQueueName, false, false, false, null);

            int messageCount = channel.queueDeclarePassive(destinationQueueName).getMessageCount();

            GetResponse response;
            while(messageCount > 0 && (response = channel.basicGet(destinationQueueName, true)) != null) {
                try {
                    String jSONMessage = new String(response.getBody(), StandardCharsets.UTF_8);

                    rabbitMQMessageProcessor.popMessageLogic(generateObjectFromJSon(jSONMessage, clazz));
                    messageCount--;

                }catch (Exception e){
                    System.out.println("Message popping failed: " + e.getMessage());
                }
            }

        }catch (IOException | TimeoutException e){
            System.out.println(e.getMessage());
        }

    }


    private <T> String generateJson(T t)   {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            return "{}";
        }
    }

    private  <T> T generateObjectFromJSon(String json, Class<T> clazz){
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



}
