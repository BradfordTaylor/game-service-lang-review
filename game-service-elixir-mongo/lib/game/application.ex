defmodule Game.Application do
  use Application

  # See https://hexdocs.pm/elixir/Application.html
  # for more information on OTP Applications
  def start(_type, _args) do
    import Supervisor.Spec

    # Define workers and child supervisors to be supervised
    children = [
#      supervisor(Game.Repo, []),
      supervisor(GameWeb.Endpoint, []),
      worker(Mongo, [[name: :mongo, database: "test", hostname: "db", port: "27017", pool: DBConnection.Poolboy]])
  ]

    # See https://hexdocs.pm/elixir/Supervisor.html
    # for other strategies and supported options
    opts = [strategy: :one_for_one, name: Game.Supervisor]
    result = Supervisor.start_link(children, opts)
    Game.Startup.ensure_indexes
    result
  end

  # Tell Phoenix to update the endpoint configuration
  # whenever the application is updated.
  def config_change(changed, _new, removed) do
    GameWeb.Endpoint.config_change(changed, removed)
    :ok
  end
end
