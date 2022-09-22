defmodule Game.Games.States.Changefeed do
  use RethinkDB.Changefeed
  import RethinkDB.Query

  require Logger

  @table_name "states"
  @topic "states"

  def start_link(db, gen_server_opts \\ [name: Game.Games.States.Changefeed]) do
    RethinkDB.Changefeed.start_link(__MODULE__, db, gen_server_opts)
  end

  def init(db) do
    query = table(@table_name)

    %{data: data} = RethinkDB.run(query, db)

    states = Enum.map(data, fn (state) ->
      {state["id"], state}
    end) |> Enum.into(%{})

    {:subscribe, changes(query), db, {db, states}}
  end

  def handle_update(data, {db, states}) do
    states = Enum.reduce(data, states, fn
      %{"new_val" => new_value, "old_val" => old_value}, acc ->
        case new_value do
          nil ->
            Map.delete(acc, old_value["id"])
          %{"id" => id} ->
            Map.put(acc, id, new_value)
        end
    end)
    GameWeb.Endpoint.broadcast!(@topic, @table_name, %{states: Map.values(states)})

    {:next, {db, states}}
  end
end
