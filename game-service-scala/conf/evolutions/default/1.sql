# --- !Ups

create table "people" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar not null,
  "age" int not null
);

create table "states" (
  "id" BIGSERIAL PRIMARY KEY,
  "bay" int not null,
  "game_guid" varchar not null,
  "status" varchar not null,
  "current_game_type" varchar not null,
  "current_game_state" json not null
);

# --- !Downs

drop table "people" if exists;
drop table "states" if exists;
