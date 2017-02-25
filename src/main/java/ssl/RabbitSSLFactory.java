

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import com.rabbitmq.client.ConnectionFactory;

/**
 * All RabbitMQ-Connections will be established
 * using the ConnectionFactory. This class is 
 * a wrapper to enable SSL to a given ConnectionFactory.
 * 
 * Defining the Connection Credentials and other 
 * Settings is still task of the calling class. 
 * @author Phillip Ortiz
 *
 */
public class RabbitSSLFactory {
	
	//Standard Values
	private String keyPassphrase = "super";
	private String trustPassphrase = "supersecret";
	private String clientCertPath = "/etc/pki/CA/gwclient1.p12";
	private String keystorePath = "/etc/pki/CA/keystore";
	private String tlsVersion = "TLSv1.2";
	
	/**
	 * Stub. Shall change the Class' Attributes.
	 */
	public void init() {
		//TODO Implement DeliveryService
		//Make use of the DeliveryService
		//and set up all of the Attributes
	}
	
	/**
	 * Alters an existing connection factory to make use of server-site SSL. 
	 * The connection then works like a HTTPS secured website, i.e. the client
	 * encryptes the data with the public key provided by the server's cert
	 * and it will decrypt it using its private key.
	 *  
	 * @param con The existing connection factory
	 * @return The altered connection factory
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public ConnectionFactory enableServerSiteSSL(ConnectionFactory con) throws KeyManagementException, NoSuchAlgorithmException {
		//Directly altering ConnectionFactory
		con.useSslProtocol(this.tlsVersion);
		return con;
	}
	
	/**
	 * Alters an existing connection factory to make use of SSL with Client-Site 
	 * certificates. This way the server can also trust the client.
	 * @param con The existing connection factory
	 * @return The altered connection factory
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 */
	public ConnectionFactory enableClientSiteSSL(ConnectionFactory con) throws NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(this.clientCertPath), this.keyPassphrase.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, this.keyPassphrase.toCharArray());

        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(new FileInputStream(this.keystorePath), this.trustPassphrase.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        SSLContext c = SSLContext.getInstance(this.tlsVersion);
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		
        //All SSL configuration gets passed via a "SSLContext"-Parameter, which is defined above.
        con.useSslProtocol(c);
        
		return con;
	}
}