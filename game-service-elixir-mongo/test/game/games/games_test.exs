defmodule Game.GamesTest do
  use Game.DataCase

  alias Game.Games

  describe "states" do
    alias Game.Games.State

    @valid_attrs %{
      "available_game_types" => ["board", "shooter", "strategy", "simulation", "classic"],
      "bay" => 123,
      "passive" => true,
      "current_game_type" => "",
      "current_game_state" => %{}
    }

    @update_attrs %{}

    @invalid_attrs %{
      "available_game_types" => "['board', 'shooter', 'strategy', 'simulation', 'classic']",
      "bay" => "a",
      "passive" => "asdf",
      "current_game_type" => 12,
      "current_game_state" => %{}
    }

    setup do
      "states"
      |> ReQL.table()
      |> ReQL.delete()
      |> Game.Database.run()
      :ok
    end

    def state_fixture(attrs \\ %{}) do
      {:ok, state} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Games.create_state()

      state
    end

    test "list_states/0 returns all states" do
      state = state_fixture()
      assert Games.list_states() == [state]
    end

    test "get_state!/1 returns the state with given id" do
      state = state_fixture()
      assert Games.get_state!(state.id) == state
    end

    test "create_state/1 with valid data creates a state" do
      assert {:ok, %State{} = _state} = Games.create_state(@valid_attrs)
    end

    test "create_state/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Games.create_state(@invalid_attrs)
    end

    test "update_state/2 with valid data updates the state" do
      state = state_fixture()
      assert {:ok, state} = Games.update_state(state, @update_attrs)
      assert %State{} = state
    end

    test "update_state/2 with invalid data returns error changeset" do
      state = state_fixture()
      assert {:error, %Ecto.Changeset{}} = Games.update_state(state, @invalid_attrs)
      assert state == Games.get_state!(state.id)
    end

    test "delete_state/1 deletes the state" do
      state = state_fixture()
      assert {:ok, %State{}} = Games.delete_state(state)
      assert_raise Ecto.NoResultsError, fn -> Games.get_state!(state.id) end
    end

    test "change_state/1 returns a state changeset" do
      state = state_fixture()
      assert %Ecto.Changeset{} = Games.change_state(state)
    end
  end
end
