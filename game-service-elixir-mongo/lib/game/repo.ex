defmodule Game.Repo do

  require Logger

  alias Game.Games.State
  alias Ecto.UUID

  def all(param \\ %State{}) do
    Logger.info("all: #{inspect(param)}")
  end

  def get(param1, id) do
    result = Mongo.find(:mongo, "states", %{id: id}, pool: DBConnection.Poolboy)
             |> Enum.to_list()
  end

  def get!(param1, id) do #id #Repo.get!(State, id) end
    result = Mongo.find(:mongo, "states", %{id: id}, pool: DBConnection.Poolboy)
            |> Enum.to_list()
  end

  def get_by!(param1, bay: bay) do
    result = Mongo.find(:mongo, "states", %{bay: String.to_integer(bay)}, pool: DBConnection.Poolboy)
             |> Enum.to_list()
             |> List.first()
             |> transform()
  end

  def get_by(param1, bay: bay) do
    result = Mongo.find(:mongo, "states", %{bay: String.to_integer(bay)}, pool: DBConnection.Poolboy)
    |> Enum.to_list()
    |> List.first()
    |> transform()
  end

  def delete(bay) do
    Mongo.delete_many(:mongo, "states", %{bay: bay}, pool: DBConnection.Poolboy)
  end

  def insert(param \\ %State{}) do
    param.changes
    |> minsert()
  end

  def update(changeset) do
    {:ok, changeset.data
    |> Map.from_struct()
    |> Map.merge(changeset.changes)
    |> Map.take(Map.keys(changeset.types))
    |> mupdate(changeset.data.bay)
    |> transform()
    |> IO.inspect }
  end


  defp minsert(param, filter \\ %{}) do
    {:ok, result} = Mongo.insert_one(:mongo, "states", param, [return_document: :after, pool: DBConnection.Poolboy])
    {:ok, Map.merge(%State{}, param)
                |> Map.put(:id, BSON.ObjectId.encode!(result.inserted_id))}
  end

  defp mupdate(param, filter) do
    {:ok, result} = Mongo.find_one_and_update(:mongo, "states", %{"bay" => filter},%{"$set" => param},[return_document: :after, pool: DBConnection.Poolboy])
    result
  end

  # TODO: Probably a better way to do this inline above
  defp transform(result) when result == nil, do: Logger.info("Transform passed nil result")
  defp transform(result) do
    x = for {key, val} <- result, into: %{} do
      {String.to_existing_atom(key), val}
    end
    struct(State, x)
    |> Map.put(:id, BSON.ObjectId.encode!(result["_id"]))
  end


end
