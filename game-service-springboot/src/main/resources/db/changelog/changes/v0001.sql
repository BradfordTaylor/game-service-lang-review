create table states (
  id bigserial not null,
  bay bigint not null,
  passive boolean,
  current_game_type varchar(64) not null,
  current_game_state json not null,
  primary key (id)
);

create table game_types (
  id bigserial not null,
  state_id bigserial not null,
  available_game_types varchar(255),
  primary key (id)
);
