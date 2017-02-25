
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ssl.RabbitSSLFactory;
import ssl.serversitessl.ServerSiteSSL;
import ssl.verifyclient.VerifyClient;

public class MainSSL {
	
	//Change this to match your config
		private static String HOST_IP = "localhost";
		private static int HOST_PORT = 5671;
		
		private static String USERNAME = "test";
		private static String PASSWORD = "test";
		
		private static String QUEUE_NAME = "hello";

	public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException, InterruptedException, KeyStoreException, CertificateException, UnrecoverableKeyException {
		
		
		//Setting up connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_IP);
		factory.setPort(HOST_PORT);
		factory.setUsername(USERNAME);
		factory.setPassword(PASSWORD);
	
		RabbitSSLFactory ssl = new RabbitSSLFactory();
		factory = ssl.enableClientSiteSSL(factory);
	
		//Establish connection
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		//Publish "Hello World" message
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
	    
	      //Clean up
	    channel.close();
	    second.close();
	    connection.close();
	    
	    
	}

}
