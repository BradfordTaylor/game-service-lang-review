.PHONY: all build test run doc

all:	build

build:
	mix do deps.get, deps.compile, ecto.setup

test:	build
	MIX_ENV=test mix test

run:	build
	mix phx.server
