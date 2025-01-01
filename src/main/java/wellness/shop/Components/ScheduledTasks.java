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
import wellness.shop.Utilities.UtilitiesGeneral;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Scheduled(fixedRate = 100000)
    public void createRegistrationNewDates(){

        List<LocalDateTime> dateTimes = UtilitiesGeneral.generateFutureTimeSlots(30);

        for (LocalDateTime l : dateTimes) {
            System.out.println(l);
        }

    }





    /**
     * This method calculates how many HTTP request each IP had. This is done using RabbitMQ.
     * A custom interface is written to have a call back to rabbit, but ipMap is in scope of method.
     *
     */
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

    /**
     * Check if IP exceeded the limit of calls with "requestBanLimit"
     *
     */
    private List<String> getBanList(HashMap<String, Integer> ipMap){

        if(ipMap == null || ipMap.isEmpty()) return Collections.emptyList();
        List<String> bannedIpsList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ipMap.entrySet()) {
            if(entry.getValue() > requestBanLimit) bannedIpsList.add(entry.getKey());
        }
        return bannedIpsList;
    }

    /**
     * Set banned ips in Redis and in repository.
     *
     */

    private void banIps(List<String> bannedIpsList ){
        if (bannedIpsList == null || bannedIpsList.isEmpty()) return;
        for(String ip : bannedIpsList) {
            redis.putPermanently(ip,"");
            ipRepository.registerBannedIP(ip);
            System.out.println(ip + " was banned.");
        }
    }



}
