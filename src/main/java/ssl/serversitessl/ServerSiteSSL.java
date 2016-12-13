package ssl.serversitessl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ServerSiteSSL {
	
	//Change this to match your config
	private static String HOST_IP = "localhost";
	private static int HOST_PORT = 5671;
	
	private static String USERNAME = "test";
	private static String PASSWORD = "test";
	
	private static String QUEUE_NAME = "hello";

	public ServerSiteSSL() throws IOException, KeyManagementException, NoSuchAlgorithmException {
		
		//Setting up connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_IP);
		factory.setPort(HOST_PORT);
		factory.setUsername(USERNAME);
		factory.setPassword(PASSWORD);
		
		/*
		 * This is where you set the SSL protocol version.
		 * The most recent version now (Dec 16) is TLSv1.2.
		 * 
		 * The version specified here has to match the activated 
		 * version in the /etc/rabbitmq/rabbitmq.conf.
		 */
		factory.useSslProtocol("TLSv1.2");
		
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		channel.queueDeclare(QUEUE_NAME, false, true, true, null);
	    String message = "Hello World!";
	    channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
	    System.out.println(" [x] Sent '" + message + "'");
	    
	    //Consume it. Must be a second connection to 
	    //Catch the previous message
	    Channel second = connection.createChannel();
	    Consumer consumer = new DefaultConsumer(second) {
	        @Override
	        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
	            throws IOException {
	          String message = new String(body, "UTF-8");
	          System.out.println(" [x] Received '" + message + "'");
	        }
	      };
	      second.basicConsume(QUEUE_NAME, true, consumer);
	    
	    channel.close();
	    second.close();
	    connection.close();
	}

}
