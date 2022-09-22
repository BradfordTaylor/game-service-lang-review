defmodule Game.Repo.Migrations.CreateStates do
  use Ecto.Migration

  def change do
    create table(:states) do
      add :available_game_types, {:array,:string}
      add :bay, :integer
      add :passive, :boolean
      add :current_game_type, :string
      add :current_game_state, :json
      timestamps()
    end
  end
end
