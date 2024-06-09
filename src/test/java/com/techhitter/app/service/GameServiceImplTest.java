package com.techhitter.app.service;

import com.techhitter.app.dto.GameConfigDto;
import com.techhitter.app.dto.QueObject;
import com.techhitter.app.registry.GameConfigurationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameConfigurationRegistry gameConfigurationRegistry;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.createServer(restTemplate);
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

    @Test
    void testGetQuestions(){
        mockServer.expect(requestTo("http://localhost:8081/api/v1/quetions/Java"))
                .andRespond(withSuccess("mockResponse", MediaType.APPLICATION_JSON));
        gameService.getQuestions();
        mockServer.verify();
    }
}