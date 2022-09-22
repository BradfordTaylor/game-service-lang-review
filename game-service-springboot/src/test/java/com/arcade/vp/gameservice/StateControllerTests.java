package com.arcade.vp.gameservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.arcade.vp.gameservice.domain.State;
import com.arcade.vp.gameservice.domain.StateRepository;
import com.arcade.vp.gameservice.web.rest.StateController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StateControllerTests {

  @Mock
  private StateRepository stateRepositoryMock;

  @Mock
  private RabbitTemplate rabbitTemplateMock;

  private ObjectMapper omm = new ObjectMapper();

  private String keyString = "key.%s.more.key";

  private String topicName = "topic";

  private StateController stateController;

  private final Logger log = LoggerFactory.getLogger(StateControllerTests.class);

  @Before
  public void initTests(){
    stateController = new StateController(stateRepositoryMock, rabbitTemplateMock, keyString, topicName);
  }

  @Test
  public void testGet404() {
    ResponseEntity re = stateController.get(104);
    assertTrue(re.getStatusCode().is4xxClientError());
  }

  @Test
  public void testGet200() {
    int bay = 104;
    when(stateRepositoryMock.findByBay(bay)).thenReturn(createState(bay));
    ResponseEntity re = stateController.get(104);
    assertTrue(re.getStatusCode().is2xxSuccessful());
  }

  @Test
  public void testCreate400() throws Exception {
    int bay = 104;
    when(stateRepositoryMock.findByBay(bay)).thenReturn(createState(bay));
    ResponseEntity re = stateController.create(104, createState(bay));
    assertTrue(re.getStatusCode().is4xxClientError());
  }

  @Test
  public void testCreate200() throws Exception {
    int bay = 104;
    State state = createStateWithoutBay();
    State expectedState = createState(bay);
    String expectedJsonResponse = omm.writeValueAsString(expectedState);
    String expectedKey = String.format(keyString, bay);

    when(stateRepositoryMock.save(any(State.class))).thenReturn(expectedState);
    ResponseEntity re = stateController.create(bay, expectedState);

    verify(rabbitTemplateMock, times(1)).convertAndSend(eq(topicName), eq(expectedKey), eq(expectedJsonResponse));
    assertTrue(re.getStatusCode().is2xxSuccessful());
  }

  @Test
  public void testUpdate400() throws Exception{
    int bay = 104;
    State state = null;
    State expectedState = createState(bay);
    when(stateRepositoryMock.findByBay((bay))).thenReturn(expectedState);
    when(stateRepositoryMock.save(any(State.class))).thenReturn(expectedState);
    ResponseEntity re = stateController.update(bay,state);
    assertTrue(re.getStatusCode().is4xxClientError());
  }

  @Test
  public void testUpdate200() throws Exception{
    int bay = 104;
    State state = createPartialState();
    State expectedState = createState(bay);
    when(stateRepositoryMock.findByBay(bay)).thenReturn(expectedState);
    when(stateRepositoryMock.save(any(State.class))).thenReturn(expectedState);
    ResponseEntity re = stateController.update(bay, state);
    assertTrue(re.getStatusCode().is2xxSuccessful());
    // test to make sure all state fields are returned
    assertEquals(expectedState, re.getBody());
  }

  @Test
  public void testDelete200() throws Exception{
    int bay = 104;
    State state = createPartialState();
    when(stateRepositoryMock.findByBay(anyInt())).thenReturn(state);
    ResponseEntity re = stateController.delete(bay);
    assertTrue(re.getStatusCode().is2xxSuccessful());
  }

  private State createState(int bay){
    State s = createStateWithoutBay();
    s.setBay(bay);

    return s;
  }

  private State createStateWithoutBay(){
    ArrayList<String> cgt = new ArrayList<String>();
    cgt.add("g1");
    cgt.add("g2");

    State s = createPartialState();
    s.setId(1L);
    s.setAvailableGameTypes(cgt);
    s.setCurrentGameType("currentGameType");
    s.setPassive(false);

    return s;
  }

  private State createPartialState(){
    Map<String,Object> cgs = new HashMap<>();
    cgs.put("field1", "value2");
    cgs.put("field2", "value2");

    State s = new State();
    s.setCurrentGameState(cgs);

    return s;
  }
}
