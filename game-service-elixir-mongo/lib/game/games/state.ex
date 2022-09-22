defmodule Game.Games.State do
  use Ecto.Schema

  import Ecto.Changeset
  import Ecto.Query

  alias Game.Games.State
  alias Ecto.Changeset

  @primary_key {:id, :id, autogenerate: false}
#  @foreign_key_type :binary_id

  @derive {Phoenix.Param, key: :bay}

  schema "states" do
    field :uuid, Ecto.UUID, read_after_writes: true
    field :available_game_types, {:array, :string}
    field :bay, :integer
    field :passive, :boolean
    field :current_game_type, :string
    field :current_game_state, :map
    timestamps()
  end

  @doc false
  def changeset(%State{} = state, attrs) do
    state
    |> cast(attrs, [:available_game_types, :bay, :passive, :current_game_type, :current_game_state])
    |> validate_required([:bay])
    |> validate_bay_unique
  end

  defp validate_bay_unique(%Changeset{valid?: true} = changeset) do
    with bay_number <- get_field(changeset, :bay, nil),
                 uid <- get_field(changeset, :uid, nil)
    do
      if bay_state_exists?({bay_number, uid}) do
        add_error(changeset, :bay, "already exists")
      else
        changeset
      end
    end
  end
  defp validate_bay_unique(%Changeset{valid?: false} = changeset), do: changeset

  defp bay_state_exists?({bay_number, nil}) do
    query = from s in State,
            where: s.bay == ^bay_number

#    Game.Repo.aggregate(query, :count, :bay) > 0
  end

  defp bay_state_exists?({bay_number, id}) do
    query = from s in State,
            where: s.bay == ^bay_number and s.id != ^id

#    Game.Repo.aggregate(query, :count, :bay) > 0
  end
end
