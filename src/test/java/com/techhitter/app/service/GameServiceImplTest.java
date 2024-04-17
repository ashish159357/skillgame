package com.techhitter.app.service;

import com.techhitter.app.dto.GameConfigDto;
import com.techhitter.app.registry.GameConfigurationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameConfigurationRegistry gameConfigurationRegistry;

    @Mock
    private SimpMessagingTemplate template;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGame() {
        // Given
        GameConfigDto gameConfigDto = new GameConfigDto();
        gameConfigDto.setNo_of_question(5);
        gameConfigDto.setTime_for_each_question(10);
        gameConfigDto.setSubject("Java");
        gameConfigDto.setNo_of_player(15);

        GameConfigDto result = gameService.createGame(gameConfigDto);

        // Then
        assertEquals(gameConfigDto, result);
        assertNotEquals(gameConfigDto.getKey(),null);
        verify(gameConfigurationRegistry, times(1)).addMap(any(), any());
    }

    @Test
    void testCreateGame1() {
        // Given
        GameConfigDto gameConfigDto = new GameConfigDto();
        gameConfigDto.setNo_of_question(5);
        gameConfigDto.setTime_for_each_question(10);
        gameConfigDto.setSubject(null);
        gameConfigDto.setNo_of_player(15);

        // Then
        assertThrows(NullPointerException.class,()->{
            gameService.createGame(gameConfigDto);
        });
        verifyNoInteractions(gameConfigurationRegistry);
    }

    @Test
    void testStartGame() {
        // Given
        String key = "testKey";
        GameConfigDto gameConfigDto = new GameConfigDto();
        gameConfigDto.setKey(key);
        gameConfigDto.setNo_of_question(5);
        gameConfigDto.setTime_for_each_question(5); // Assuming 2 seconds for each question
        gameConfigDto.setSubject("Java");
        gameConfigDto.setNo_of_player(15);

        // Mock findById method
        when(gameConfigurationRegistry.findById(key)).thenReturn(gameConfigDto);

        // When
        gameService.startGame(key);
        var timeout = gameConfigDto.getNo_of_question() * gameConfigDto.getTime_for_each_question() * 1000;

        // Then
        verify(template, timeout(timeout).atLeast(5)).convertAndSend(anyString(), anyString()); // Verify at least 5 messages sent
    }

}