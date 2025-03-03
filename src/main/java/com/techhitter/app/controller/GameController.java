package com.techhitter.app.controller;

import com.techhitter.app.dto.GameConfigDto;
import com.techhitter.app.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/v1/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping(value = "/create/",produces = "application/json")
    public ResponseEntity<GameConfigDto> createGame(@RequestBody GameConfigDto gameConfigDto){
        try{
            gameConfigDto = gameService.createGame(gameConfigDto);
        }catch (Exception e){
            gameConfigDto.setKey(null);
            return ResponseEntity.badRequest().body(gameConfigDto);
        }
        return ResponseEntity.ok().body(gameConfigDto);
    }

    @GetMapping(value = "/start/{gameKey}")
    public ResponseEntity<String> startGame(@PathVariable String gameKey){
        try{
            gameService.startGame(gameKey);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("Game has Started");
    }
}
