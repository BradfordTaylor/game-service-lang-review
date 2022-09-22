FROM elixir:1.5-alpine

RUN apk update && \
    apk --no-cache --update add \
    git wget curl inotify-tools \
    nodejs \
    nodejs-npm \
    yarn \
    bash && \
    rm -rf /var/cache/apk/*

RUN mix local.hex --force
RUN mix local.rebar --force

RUN mix archive.install https://github.com/phoenixframework/archives/raw/master/phx_new.ez --force

WORKDIR /var/api

ADD . /var/api

RUN mix do deps.get, deps.compile

RUN cd /var/api/assets && yarn && cd -

EXPOSE 4000

CMD ["mix", "phx.server"]
