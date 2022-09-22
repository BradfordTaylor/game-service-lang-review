package com.arcade.vp.gameservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
  State findByBay(Integer bayId);
}
