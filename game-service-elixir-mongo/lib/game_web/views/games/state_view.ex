defmodule GameWeb.Games.StateView do
  use GameWeb, :view
  alias GameWeb.Games.StateView

  def render("index.json", %{states: states}) do
    %{data: render_many(states, StateView, "state.json")}
  end

  def render("show.json", %{state: state}) do
    render_one(state, StateView, "state.json")
  end

  def render("state.json", %{state: state}) do
    %{
      uid: state.id,
      available_game_types: state.available_game_types,
      bay: state.bay,
      passive: state.passive,
      current_game_type: state.current_game_type,
      current_game_state: state.current_game_state,
      inserted_at: state.inserted_at,
      updated_at: state.updated_at
    }
  end
end
