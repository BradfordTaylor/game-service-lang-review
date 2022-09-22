package com.arcade.vp.gameservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.arcade.vp.gameservice.postgres.JsonDataUserType;
import lombok.Data;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@Table(name = "states")
@JsonRootName(value="state")
@TypeDef(name = "JsonDataUserType", typeClass = JsonDataUserType.class, parameters = {
    @Parameter(name = JsonDataUserType.CLASS, value = "java.util.Map")})
public class State implements Serializable{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="bay")
  private Integer bay;

  @Column(name="available_game_types")
  @ElementCollection
  @CollectionTable(
      name="game_types",
      joinColumns=@JoinColumn(name="state_id")
  )
  @JsonProperty("available_game_types")
  private List<String> availableGameTypes;

  @Column(name="passive")
  @JsonProperty("passive")
  private Boolean passive;

  @Column(name="current_game_type")
  @JsonProperty("current_game_type")
  private String currentGameType;

  @Column(name="current_game_state")
  @JsonProperty("current_game_state")
  @Type(type = "JsonDataUserType")
  private Map<String, Object> currentGameState;

}
