package ssl.verifyclient;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * To use client site authentication via SSL
 * you need to provide a keyfile in PKCS#12 Format,
 * initialize the Keymanager, Keystore, and TrustManager.
 * 
 *  A short explanation can be found here (https://www.rabbitmq.com/ssl.html#trust-levels)
 *  For a more in-depth discussion please refer 
 *  	https://www.digitalocean.com/community/tutorials/java-keytool-essentials-working-with-java-keystores
 *  	https://docs.oracle.com/cd/E19509-01/820-3503/ggfen/index.html
 *  	https://lmgtfy.com/?q=java+keystore
 * 
 * Unfortunately, all these Exceptions are necessary. Handle them properly.
 * @throws KeyManagementException
 * @throws NoSuchAlgorithmException
 * @throws IOException
 * @throws UnrecoverableKeyException
 * @throws KeyStoreException
 * @throws CertificateException
 */
public class VerifyClient {

	//Change to match your config
	private static String HOST_IP = "localhost";
	private static int HOST_PORT = 5671;
	
	private static String USERNAME = "test";
	private static String PASSWORD = "test";
	
	private static String QUEUE_NAME = "hello";
	
	public VerifyClient() throws KeyManagementException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyStoreException, CertificateException {
		
		char[] keyPassphrase = "supersecretpassword".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream("/path/to/clientcert.p12"), keyPassphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "supersecretpassword".toCharArray());

        
        /**
         * You need to create a keystore beforehand.
         * 
         * To import an exsisting keyfile, you can use the keytool:
         * $~> keytool -import -alias server1 -file /path/to/server/cert.pem -keystore /path/to/keystore
         */
        char[] trustPassphrase = "supersecretpassword".toCharArray();
        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(new FileInputStream("/path/to/keystore"), trustPassphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        //Like in the server site ssl example, you define the used SSL version here. 
        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		
		//Setting up the Connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST_IP);
		factory.setPort(HOST_PORT);
		factory.setUsername(USERNAME);
		factory.setPassword(PASSWORD);
		
		//All SSL configuration gets passed via a "SSLContext"-Parameter, which is defined above.
		factory.useSslProtocol(c);
		
		//Establish a connection
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		//Publish
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
