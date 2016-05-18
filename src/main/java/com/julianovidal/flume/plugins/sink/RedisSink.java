package com.julianovidal.flume.plugins.sink;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.julianovidal.flume.plugins.redis.RedisConnector;

import redis.clients.jedis.Jedis;

public class RedisSink extends AbstractSink implements Configurable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSink.class);

	private String queueName;
	private Integer batchSize;
	private RedisConnector redisConnector;
	
	@Override
	public Status process() throws EventDeliveryException {

		Jedis jedis = redisConnector.getResource();
		
		Status result = Status.READY;
		
		Channel channel = getChannel();
		Transaction transaction = channel.getTransaction();
		Event event = null;

		try {
			
			transaction.begin();
			
			List<Event> events = new ArrayList<Event>(batchSize);
			
			for(int i = 0; i < batchSize; i++) {
				event = channel.take();
				if(event == null) {
					result = Status.BACKOFF;
					break;
				}
				events.add(event);
			}
			
			persistEvents(jedis, events);
			
			transaction.commit();
			
		} catch (Exception ex) {
			transaction.rollback();
			throw new EventDeliveryException("Failed to log event: " + event, ex);
			
		} finally {		
			transaction.close();
			if(jedis != null) jedis.close();
		}
		
		return result;
	}

	@Override
	public void configure(Context context) {
			
		String queueName = context.getString("sink.queue");
		Integer batchSize = context.getInteger("sink.batch", 1);
		String redisHost = context.getString("sink.host", "127.0.0.1");
		Integer redisPort = context.getInteger("sink.port", 6379);
		Integer redisTimeout = context.getInteger("sink.timout", 7000);
		
		this.queueName = queueName;
		this.batchSize = batchSize;
		redisConnector = new RedisConnector(redisHost, redisPort, redisTimeout);
	}
		
		
	private void persistEvents(Jedis jedis, List<Event> events) throws IOException {
		
		if(events.isEmpty())
			return;
		
		List<String> messages = events.stream().map(event -> {
			try {
				String message = new String(event.getBody(), "UTF-8");
				if(message != null && !message.trim().isEmpty()) {
					return message;
				}
			} catch(UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		
		jedis.rpush(queueName, messages.toArray(new String[]{}));
	}
}

