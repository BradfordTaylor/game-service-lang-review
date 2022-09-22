package com.arcade.vp.gameservice.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.arcade.vp.gameservice.domain.State;
import com.arcade.vp.gameservice.domain.StateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/states/bay")
public class StateController {

  private static final Logger log = LoggerFactory.getLogger(StateController.class);

  private final StateRepository stateRepository;
  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final String keyString;
  private final String topicName;

  public StateController(final StateRepository stateRepository, final RabbitTemplate rabbitTemplate,   @Value("${gameservice.message.key.update}") final String keyString, @Value("${gameservice.message.topic.game}") final String topicName){
    this.stateRepository = stateRepository;
    this.rabbitTemplate = rabbitTemplate;
    this.keyString = keyString;
    this.topicName = topicName;
  }

  @GetMapping(path = "/{bay}/games/current")
  public ResponseEntity get(@PathVariable final Integer bay){
    log.debug("Get request for state: {}", bay);
    State response = stateRepository.findByBay(bay);
    if(response == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(response);
  }

  @PostMapping(path = "/{bay}/games/current")
  public ResponseEntity create(@PathVariable final Integer bay, @RequestBody @Valid final State state) throws JsonProcessingException {
    log.debug("POST request to create state: {} in bay {}", state, bay);
    state.setBay(bay); // Have to manually set this one from the URL
    if(stateRepository.findByBay(bay) != null) return ResponseEntity.badRequest().build();
    State response = stateRepository.save(state);
    rabbitTemplate.convertAndSend(topicName,String.format(keyString, bay), objectMapper.writeValueAsString(response));
    return ResponseEntity.ok(response);
  }

  @PutMapping(path =  "/{bay}/games/current")
  public ResponseEntity update(@PathVariable final Integer bay, @RequestBody @Valid final State state) throws JsonProcessingException {
    log.debug("PUT request to update state: {} in bay {}", state, bay);
    State dbState = stateRepository.findByBay(bay);
    if(state == null) return ResponseEntity.notFound().build();
    if(state.getAvailableGameTypes() != null) dbState.setAvailableGameTypes(state.getAvailableGameTypes());
    if(state.getCurrentGameState() != null) dbState.setCurrentGameState(state.getCurrentGameState());
    if(state.getPassive() != null) dbState.setPassive(state.getPassive());
    if(state.getCurrentGameType()!= null) dbState.setCurrentGameType(state.getCurrentGameType());
    State response = stateRepository.save(dbState);
    rabbitTemplate.convertAndSend(topicName,String.format(keyString, bay), objectMapper.writeValueAsString(dbState));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping(path =  "/{bay}/games/current")
  public ResponseEntity delete(@PathVariable final Integer bay){
    log.debug("DELETE request to delete state in bay {}", bay);
    Long id = stateRepository.findByBay(bay).getId();
    stateRepository.delete(id);
    return ResponseEntity.ok().build();
  }
}
