defmodule GameWeb.Games.StateController do
  use GameWeb, :controller

  alias Game.Games
  alias Game.Games.State

  import Logger

  action_fallback GameWeb.FallbackController

  def create(conn, %{"bay" => bay, "state" => state_params}) do
    state_params = Map.put_new(state_params, "bay", bay)
    with {:ok, %State{} = state} <- Games.create_state(state_params) do
      conn
      |> put_status(:created)
      |> put_resp_header("location", bays_games_state_path(conn, :show, state))
      |> render("show.json", state: state)
    end
  end

  def show(conn, %{"bay" => bay}) do
    state = Games.get_state_by_bay!(bay)
    case state do
      n when n in [nil, :ok] ->
        conn
        |> put_status(:not_found)
        |> render(GameWeb.ErrorView, "404.json")
      state ->
        render(conn, "show.json", state: state)
    end
  end

  def update(conn, %{"bay" => bay, "state" => state_params}) do
    case Games.get_state_by_bay(bay) do
      n when n in [nil, :ok] ->
        conn
        |> put_status(:not_found)
        |> render(GameWeb.ErrorView, "404.json")
      state ->
        with {ok, %State{} = state} <- Games.update_state(state, state_params) do
          render(conn, "show.json", state: state)
        end
    end

  end

  def delete(conn, %{"bay" => bay}) do
        with {:ok, %State{}} <- Games.delete_state(String.to_integer(bay)) do
          send_resp(conn, :no_content, "")
        end
  end
end
