package wellness.shop.Integration;

@FunctionalInterface
public interface RabbitMQMessageProcessor <T> {
    void popMessageLogic(T genericMessage);
}