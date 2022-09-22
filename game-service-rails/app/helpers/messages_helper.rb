module MessagesHelper
  require 'bunny'

  STDOUT.sync = true

  class MessageConnection
    @@url
    @@channel
    @@topics = {}

    def initialize(url)
      puts "Bunny connecting to #{url}"
      @@url =  url
      conn = Bunny.new(url)
      conn.start
      puts "Bunny connection established to #{url}"
      @@channel = conn.create_channel
    end

    def publish(topic, key, message)
      exchange  = @@topics[topic]
      if(exchange == nil) then
        exchange = @@channel.topic(topic, :durable => true)
         @@topics[topic] = exchange
      end
      puts "Publishing to topic: #{topic}"
      puts "                key: #{key}"
      puts "            message: #{message}"
      exchange.publish(message, :routing_key => key)
    end
  end
end