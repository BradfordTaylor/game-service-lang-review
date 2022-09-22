class CreateStates < ActiveRecord::Migration[5.1]

    require 'pg'
    require 'active_record'

    def initialize
        super
    end 

    def change
        puts 'About to create table'
        create_table (:states) do |t|
            t.string :available_game_types, array: true, default: []
            t.integer :bay
            t.boolean :passive
            t.string :current_game_type
            t.map :current_game_state

            t.timestamps
        end
        puts 'Table created'
    end

    def save 
        puts 'About to save data'
        conn = PG.connect(:dbname => 'postgres', :user => 'postgres',
        :password => 'postgres')
        conn.exec('insert into states(available_game_types, bay, passive,
        current_game_type, current_game_state) values(#{@available_game_types}, #{@bay}, #{@passive},
        #{@current_game_type}, #{@current_game_state})')
    end
        

    def down
        drop_table :states
    end
end