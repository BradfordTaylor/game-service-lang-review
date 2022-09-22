defmodule GameWeb.Games.StateControllerTest do
  use GameWeb.ConnCase

  alias Game.Games
  alias Game.Games.State

  require Logger

  @bay 123

  @create_attrs %{
    "available_game_types" => ["board", "shooter", "strategy", "simulation", "classic"],
    "passive" => true,
    "current_game_type" => "",
    "current_game_state" => %{}
  }

  @update_attrs %{
    "passive" => false,
    "current_game_type" => "classic",
    "current_game_state" => %{
      "players" => %{
        "3f86a559-28a3-451e-be02-2e3723d4ddc4" => %{},
        "e5654d29-e160-4197-912e-b0d11917be87" => %{},
        "10264304-1b0c-4e61-83ea-8836e055217c" => %{}
      }
    }
  }

  @invalid_attrs %{
    "passive" => "asdf",
    "current_game_type" => 12
  }

  def fixture(:state) do
    {:ok, state} = Games.create_state(Map.merge(%{"bay" => @bay}, @create_attrs))
    state
  end

  setup do
    "states"
    |> ReQL.table()
    |> ReQL.delete()
    |> Game.Database.run()
    :ok
  end

  setup %{conn: conn} do
    {:ok, conn: put_req_header(conn, "accept", "application/json")}
  end

  describe "create state" do
    test "renders state when data is valid", %{conn: conn} do
      conn = post conn, bays_games_state_path(conn, :create, %State{bay: @bay}), state: Map.merge(%{"bay" => "#{@bay}"}, @create_attrs)
      assert %{"bay" => bay} = json_response(conn, 201)

      conn = get conn, bays_games_state_path(conn, :show, bay)

      response = json_response(conn, 200)

      Map.merge(%{"bay" => @bay}, @create_attrs)
      |> Enum.reject(fn {_, v} -> if is_map(v), do: map_size(v) == 0, else: false end)
      |> Enum.reject(fn {_, v} -> if is_list(v), do: length(v) == 0, else: false end)
      |> Enum.reject(fn {_, v} -> if is_bitstring(v), do: String.length(v) == 0, else: false end)
      |> Enum.all?(fn {k, v} -> response[k] == v end)
      |> assert
    end

    test "renders errors when data is invalid", %{conn: conn} do
      conn = post conn, bays_games_state_path(conn, :create, %State{bay: "a"}), state: @invalid_attrs
      assert json_response(conn, 422)["errors"] != %{}
    end
  end

  describe "update state" do
    setup [:create_state]

    test "renders state when data is valid", %{conn: conn, state: %State{bay: bay} = state} do
      conn = put conn, bays_games_state_path(conn, :update, state), state: @update_attrs
      assert %{"bay" => ^bay} = json_response(conn, 200)

      conn = get conn, bays_games_state_path(conn, :show, bay)

      response = json_response(conn, 200)

      Map.merge(%{"bay" => @bay}, @update_attrs)
      |> Enum.reject(fn {_, v} -> if is_map(v), do: map_size(v) == 0, else: false end)
      |> Enum.reject(fn {_, v} -> if is_list(v), do: length(v) == 0, else: false end)
      |> Enum.reject(fn {_, v} -> if is_bitstring(v), do: String.length(v) == 0, else: false end)
      |> Enum.all?(fn {k, v} -> response[k] == v end)
      |> assert
    end

    test "renders errors when data is invalid", %{conn: conn, state: state} do
      conn = put conn, bays_games_state_path(conn, :update, state), state: @invalid_attrs
      assert json_response(conn, 422)["errors"] != %{}
    end
  end

  describe "delete state" do
    setup [:create_state]

    test "deletes chosen state", %{conn: conn, state: state} do
      conn = delete conn, bays_games_state_path(conn, :delete, state)
      assert response(conn, 204)
      assert_error_sent 404, fn ->
        get conn, bays_games_state_path(conn, :show, state)
      end
    end
  end

  defp create_state(_) do
    state = fixture(:state)
    {:ok, state: state}
  end
end
