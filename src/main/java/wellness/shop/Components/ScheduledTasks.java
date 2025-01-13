package wellness.shop.Components;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wellness.shop.Components.TheadTask.FoodListManager;
import wellness.shop.Integration.RabbitMQ;
import wellness.shop.Integration.RabbitMQMessageProcessor;
import wellness.shop.Integration.Redis;
import wellness.shop.Models.FoodItem;
import wellness.shop.Repositories.DateRegistrationRepository;
import wellness.shop.Repositories.FoodRepository;
import wellness.shop.Repositories.IPRepository;
import wellness.shop.Services.ChatAIService;
import wellness.shop.Services.FoodService;
import wellness.shop.Utilities.UtilitiesGeneral;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @Autowired
    private DateRegistrationRepository dateRegistrationRepository;
    @Autowired
    private ChatAIService chatAIService;
    @Autowired
    private FoodService foodService;

    private RabbitMQ rabbitMQ;

    private Redis redis;

    @PostConstruct
    public void init(){
        this.rabbitMQ = new RabbitMQ(rabbitHost);
        this.redis = new Redis(redisHost, redisPort);

    }
//Generates 100 food items then uses Manager to mage threads and then safe ask items from AI and then writes them.
    @Scheduled(fixedRate = 86400000)
    public void fillFoodTable(){
        List<String> foodList = chatAIService.getRandomFoodList();
        FoodListManager foodListManager = new FoodListManager(foodList);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        while(!foodListManager.isEmpty()){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String foodString = foodListManager.getFoodItem();
                    if(foodString == null) return;
                    FoodItem foodItem = chatAIService.getFoodItemFromAI(foodString);
                    if(foodItem == null) return;
                    else foodService.registerFoodItemIA(foodItem);
                }
            });
        };

        executor.shutdown();
    }

   @Async
   @Scheduled(fixedRate = 60000)
    public void IPChecks(){
        HashMap<String,Integer> ipMap = getIPMap();
        List<String> bannedIpsList = getBanList(ipMap);
        banIps(bannedIpsList);
    }


    //Registers each day a new date. Before registration program checks if it was registered.
    @Scheduled(fixedRate = 86400000)
    public void createRegistrationNewDates(){

        List<String> webSocketKeys = dateRegistrationRepository.getUniqueWebsocketKeys();
        List<LocalDateTime> dateTimes = UtilitiesGeneral.generateFutureTimeSlots(30);
        if(dateTimes == null) return;

        for(String key: webSocketKeys){
            if(dateRegistrationRepository.doesDateExist(dateTimes.getFirst(),key)) continue;
            for(LocalDateTime localDateTime: dateTimes){
                dateRegistrationRepository.registerRegistrationDate(localDateTime,key);
            }
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
