package com.techhitter.app.controller;

import com.techhitter.app.dto.GameConfigDto;
import com.techhitter.app.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/v1/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping(value = "/create/")
    public ResponseEntity<String> createGame(@RequestBody GameConfigDto gameConfigDto){
        try{
            gameConfigDto = gameService.createGame(gameConfigDto);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(gameConfigDto.getKey());
    }

    @GetMapping(value = "/start/{gameKey}")
    public ResponseEntity<String> startGame(@PathVariable String gameKey){
        gameService.startGame(gameKey);
        return ResponseEntity.ok().body("Game has Started");
    }
}
