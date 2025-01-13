package wellness.shop.Components;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import wellness.shop.Integration.Redis;
import wellness.shop.Repositories.IPRepository;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class StartUpTasks {

    @Autowired
    private IPRepository ipRepository;
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int redisPort;
    private Redis redis;
    WebDriver globalDriver;


    @PostConstruct
    public void initResourcesAndDoTasks() {
        initResources();
        cacheBannedList();
        runSeleniumTasks();


    }
 // Init all the resources after load, this is done in sequence as postConstruct is not in series.
    private void initResources(){
        this.redis = new Redis(redisHost,redisPort);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        globalDriver = new ChromeDriver();
        globalDriver.manage().window().maximize();
        globalDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }


    //SELENIUM TASK//
    public void runSeleniumTasks(){
       Runnable runnable = new Runnable() {
            @Override
            public void run() {
                waitFotToLoad(5000L);
                checkLogInAndFoodData();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    // Runs SELENIUM test for login in the calculator page
    public void checkLogInAndFoodData()  {
       globalDriver.get("http://127.0.0.1:5501/");
       //click on calculator
        globalDriver.findElement(By.xpath("/html/body/div[1]/nav[1]/ul/li[3]/a")).click();
        //click on signIn
        globalDriver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[1]/div[1]/button")).click();
        //write username
        globalDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div/form/input[1]")).sendKeys("admin");
        //write password
        globalDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div/form/input[2]")).sendKeys("test");
        //click signIn
        waitFotToLoad(2000L);
        globalDriver.findElement(By.xpath("/html/body/div[2]/div[2]/div/form/button[1]")).click();
        //setFood value
        globalDriver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[1]/div[2]/div[5]/div[1]/input")).sendKeys("Beef");
        // click check
        globalDriver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[1]/div[2]/div[7]/button[1]")).click();
        // check fat value
        waitFotToLoad(2000L);
        String checkValue = globalDriver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[1]/div[2]/div[6]/div[2]/div[3]/label[2]")).getText();

        if(checkValue.equals("17 g")) System.out.println("Test past");
        else System.out.println("Test failed");
        globalDriver.quit();
    }


    private void waitFotToLoad(Long sleepTime){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * After startup all ips in mysql database are added to redis cache.
     *
     */
    private void cacheBannedList(){
        List<String> bannedIPList = ipRepository.getBannedIPList();
        for(String ip : bannedIPList){
            redis.putPermanently(ip,"");
        }
    }



}
