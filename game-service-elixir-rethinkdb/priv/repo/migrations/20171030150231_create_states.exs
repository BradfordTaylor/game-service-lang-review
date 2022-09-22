defmodule Game.Repo.Migrations.CreateStates do
  use Ecto.Migration

  def change do
    create table(:states)
  end
end
