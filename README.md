# _This code is provided for example purposes only_
_This is a small (unfinished) project I implemented in 2017 to compare the implementations of a simple service in various languages._
_Details are provided below of the functionality that was to be implemented under a fictional premise._
_(Yes, I should have used Kafka.  I had specific reasons at the time I wanted to test without Kafka)_

# Premise
A new "white glove", vip, style arcade business wants to cater a private arcade experience to clients.  
Their facility will create partitioned rooms ("Bays") that hold 1 to 4 individuals playing games.
As part of the experience the room contains monitors that show high scores and other competitive metrics of the players.
A service is needed to track the state of each player's game and notify other systems of state updates.

## Business Requirements
- As they are played, games will make REST API calls to an endpoint with JSON packages with game information.
- While some fields in the JSON message may be constant between them, the exact information will change based on the game being played.
- The service needs to keep track of the state of each player's game.
- The service needs to place a message on a queue to notify other systems of the change in state.
- THe service needs to be able to return the state of a game on an HTTP Rest call (Get).

## Functional Requirements
The projects have the following functionality
- Receive and respond to requests via REST for CRUD operations of "State"
  - The URL will contain the Bay ID which is an integer between 101 and 102
  - The payload will be the example JSON below
- Store the rest data in a Data repository
  - The attribute `current_game_state` should be stored in the respository in such a way that it can have any JSON data in any format (i.e. the service should not know or care about it's structure)
-  Put a message on a queue notifying other systems of the state change

# Game Service Language Review

This project contains the game service implemented in Elixir, Rails, Scala (incomplete) and Java.
The same functionality is reflected in all implementations.
The purpose is to review the differences in the implementation between the languages.
The test directory contains a JMeter project with rudimentary tests.

Each project implements Docker images and all should be compilable using a Docker image (I don't think the scala one is compilable in a Docker image).

## game-service-elixir-mongo
(Add information on how to compile/run project)

(Add review of technologies used in project)

## game-service-elixir-postgres
(Add information on how to compile/run project)

(Add review of technologies used in project)

## game-service-elixir-rethinkdb
(Add information on how to compile/run project)

(Add review of technologies used in project)

## game-service-rails
(Add information on how to compile/run project)

(Add review of technologies used in project)

## game-service-scala
(Add information on how to compile/run project)

(Add review of technologies used in project)

## game-service-springboot
(Add information on how to compile/run project)

(Add review of technologies used in project)


