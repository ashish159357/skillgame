package com.techhitter.app.registry;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import com.techhitter.app.dto.GameConfigDto;

@Component
public class GameConfigurationRegistry {

    private final ConcurrentHashMap<String, GameConfigDto> gameConfigDtoConcurrentHashMap = new ConcurrentHashMap<>();

    public boolean isSchedulerExecutorPresent(String key) {
        return gameConfigDtoConcurrentHashMap.containsKey(key);
    }

    public GameConfigDto findById(String id) {
        return gameConfigDtoConcurrentHashMap.get(id);
    }

    public GameConfigDto addMap(String key, GameConfigDto executorService) {
        return gameConfigDtoConcurrentHashMap.put(key, executorService);
    }

    public void remove(String key) {
        if (gameConfigDtoConcurrentHashMap.containsKey(key)) {
            gameConfigDtoConcurrentHashMap.remove(key);
        }
    }
}
