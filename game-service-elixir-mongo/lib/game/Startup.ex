defmodule Game.Startup do
  def ensure_indexes do
    IO.puts "Using database blarg"
    Mongo.command(:mongo, %{createIndexes: "states",
      indexes: [ %{ key: %{ "bay": 1 },
        name: "bay_idx",
        unique: true} ] }, pool: DBConnection.Poolboy)
  end
end

