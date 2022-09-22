class CreateStates < ActiveRecord::Migration[5.1]
  def change
    create_table :states do |t|
      t.string :available_game_types
      t.integer :bay
      t.boolean :passive
      t.string :current_game_type
      t.string :current_game_state
    end
  end
end
