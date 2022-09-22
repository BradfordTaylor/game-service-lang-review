.PHONY: all build

all:	build

build:
	docker build -f ./Dockerfile.mq . -t "rabbitmq:3-websocket"
	docker build -t game-service . --build-arg RUN_ENV=test
