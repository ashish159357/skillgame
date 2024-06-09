package com.techhitter.app.service;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.techhitter.app.dto.GameConfigDto;
import com.techhitter.app.dto.QueObject;
import com.techhitter.app.registry.GameConfigurationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class GameServiceImpl implements GameService{

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private GameConfigurationRegistry gameConfigurationRegistry;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaClient discoveryClient;

    private String topicPrefix = "/game/";

    @Override
    public GameConfigDto createGame(GameConfigDto gameConfigDto) {
        if(gameConfigDto.getSubject() == null || gameConfigDto.getSubject() == ""){
            throw new NullPointerException("Subject can't be Null, please check!");
        }
        var key = generateUniqueKeyForGame();
        gameConfigDto.setKey(key);
        gameConfigurationRegistry.addMap(key,gameConfigDto);
        return gameConfigDto;
    }

    @Override
    public void startGame(String key) {
        GameConfigDto gameConfigDto = gameConfigurationRegistry.findById(key);
        if(gameConfigDto == null){
            throw new NullPointerException("Game Not Found for key : " + key);
        }

        var topic = topicPrefix + gameConfigDto.getKey();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Counter to track number of executions
        final int[] executionCount = {0};
        List<QueObject> queObjects = this.getQuestions();
        Collections.shuffle(queObjects);

        // Schedule a task to run every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            executionCount[0]++;
            QueObject queObject = queObjects.remove(0);
            this.template.convertAndSend(topic,queObject);

            // Stop the scheduler after 5 executions
            if (executionCount[0] >= gameConfigDto.getNo_of_question()) {
                scheduler.shutdown();
                log.info("Sending Quetion is stop for topic : {}",topic);
            }
        }, 0, gameConfigDto.getTime_for_each_question(), TimeUnit.SECONDS);
    }

    @Override
    public String generateUniqueKeyForGame() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public List<QueObject> getQuestions(){
        InstanceInfo serviceInstanceInfo = discoveryClient.getApplication("skillapi")
                .getInstances()
                .get(0);

        String homePageUrl = serviceInstanceInfo.getHomePageUrl();

        List<QueObject> queObjects = restTemplate.getForObject(serviceInstanceInfo.getHomePageUrl()+"/api/v1/quetions/Java",List.class);
        return queObjects;
    }
}
