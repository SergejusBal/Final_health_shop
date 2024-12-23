package wellness.shop.Components;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wellness.shop.Integration.RabbitMQ;
import wellness.shop.Integration.RabbitMQMessageProcessor;
import wellness.shop.Integration.Redis;
import wellness.shop.Repositories.IPRepository;

import java.util.*;

@Component
public class ScheduledTasks {

    @Value("${ip.request.maxsize.ban}")
    private int requestBanLimit;
    @Value("${rabbit.host}")
    String rabbitHost;
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;

    @Autowired
    private IPRepository ipRepository;

    private RabbitMQ rabbitMQ;

    private Redis redis;

    @PostConstruct
    public void init(){
        this.rabbitMQ = new RabbitMQ(rabbitHost);
        this.redis = new Redis(redisHost, redisPort);

    }

    @Scheduled(fixedRate = 60000)
    public void IPChecks(){

        HashMap<String,Integer> ipMap = getIPMap();
        List<String> bannedIpsList = getBanList(ipMap);
        banIps(bannedIpsList);

    }

    private synchronized HashMap<String, Integer> getIPMap(){

        HashMap<String,Integer> ipMap = new HashMap<>();

        RabbitMQMessageProcessor <String> rabbitMQMessageProcessor = (genericMessage) -> {
            if(ipMap.containsKey(genericMessage)){
                Integer count =  ipMap.get(genericMessage);
                ipMap.put(genericMessage, (count + 1));
            }else {
                ipMap.put(genericMessage, 1);
            }
        };

        rabbitMQ.popALlMessages(rabbitMQMessageProcessor,"IP", String.class);

        return ipMap;
    }

    private List<String> getBanList(HashMap<String, Integer> ipMap){

        if(ipMap == null || ipMap.isEmpty()) return Collections.emptyList();
        List<String> bannedIpsList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ipMap.entrySet()) {
            if(entry.getValue() > requestBanLimit) bannedIpsList.add(entry.getKey());
        }
        return bannedIpsList;
    }

    private void banIps(List<String> bannedIpsList ){
        if (bannedIpsList == null || bannedIpsList.isEmpty()) return;
        for(String ip : bannedIpsList) {
            redis.putPermanently(ip,"");
            ipRepository.registerBannedIP(ip);
            System.out.println(ip + " was banned.");
        }
    }



}
