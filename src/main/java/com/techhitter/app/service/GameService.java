package com.techhitter.app.service;

import com.techhitter.app.dto.GameConfigDto;

public interface GameService {

    public GameConfigDto createGame(GameConfigDto gameConfigDto);

    public void startGame(String key);

    public String generateUniqueKeyForGame();
}
