defmodule Game.Repo.Migrations.AddBayIndexToStatesTable do
  use Ecto.Migration

  def change do
    create index(:states, [:bay])
  end
end
