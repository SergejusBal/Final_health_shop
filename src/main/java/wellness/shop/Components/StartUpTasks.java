package wellness.shop.Components;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wellness.shop.Integration.Redis;
import wellness.shop.Repositories.IPRepository;

import java.util.List;

@Component
public class StartUpTasks {

    @Autowired
    private IPRepository ipRepository;
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;
    private Redis redis;

    @PostConstruct
    public void initResourcesAndDoTasks() {
        initResources();

        cacheBannedList();

    }

    private void initResources(){
        this.redis = new Redis(redisHost,redisPort);

    }

    private void cacheBannedList(){
        List<String> bannedIPList = ipRepository.getBannedIPList();
        for(String ip : bannedIPList){
            redis.putPermanently(ip,"");
        }
    }



}
