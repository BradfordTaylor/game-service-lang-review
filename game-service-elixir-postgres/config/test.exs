use Mix.Config

# We don't run a server during test. If one is required,
# you can enable the server option below.
config :game, GameWeb.Endpoint,
  http: [port: 4000]

# Print only warnings and errors during test
config :logger, level: :warn

# Configure your database
config :game, Game.Repo,
  adapter: Ecto.Adapters.Postgres,
  port: 5432,
  username: "postgres",
  password: "postgres",
  database: "game_service_repo",
  hostname: "db",
  pool_size: 10
