class StatesController < ApplicationController

  require 'pg'
  require 'bunny'
  require 'json'

  @@topic = "game"

  @@mc = MessagesHelper::MessageConnection.new(Rails.configuration.rabbitmq_url)

  def create
    puts "Creating new State Record for bay #{params[:bay]}"
    raise "bay state already exists" if State.where(bay: params[:bay]).exists?
    bay_state = state_params.to_h
    bay_state[:bay] = params[:bay]
    puts bay_state
    @state = State.new(bay_state)
    @state.save
    @@mc.publish(@@topic, "bay.#{params[:bay]}.game.event.state.update", JSON.pretty_generate(state_params.as_json))
  end


  def show
    @state = State.find_by(bay: params[:bay])
  end

  def update
    puts "Updating State Record for bay #{params[:bay]}"
    @state = State.find_by(bay: params[:bay])
    @state.update(state_params)
    @state.save
    # Publish payload to queues
    @@mc.publish(@@topic, "bay.#{params[:bay]}.game.event.state.update", JSON.pretty_generate(state_params.as_json))
  end

  def delete
    state = State.find_by(bay: params[:bay])
    state.destroy
  end

  private
    def state_params
      params.require(:state).permit(:available_game_types, :bay,
      :passive, :current_game_type, :current_game_state)
    end
end