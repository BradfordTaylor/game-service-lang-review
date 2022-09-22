Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.htmls

  root 'welcome#index'
  get '/api/v1/states/bay/:bay/games/current' => 'states#show'
  post '/api/v1/states/bay/:bay/games/current' => 'states#create'
  put '/api/v1/states/bay/:bay/games/current' => 'states#update'
  delete '/api/v1/states/bay/:bay/games/current' => 'states#delete'

end
