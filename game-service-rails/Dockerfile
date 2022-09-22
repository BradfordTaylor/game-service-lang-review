FROM ruby:2.2.5
RUN apt-get update -qq && apt-get install -y build-essential libpq-dev nodejs
RUN mkdir /game-service
WORKDIR /game-service
COPY Gemfile /game-service/Gemfile
COPY Gemfile.lock /game-service/Gemfile.lock
RUN bundle install
COPY . /game-service