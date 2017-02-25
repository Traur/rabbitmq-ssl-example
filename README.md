# How to secure RabbitMQ with TLS  
This Repository shows an example configuration about how to use RabbitMQ-Server with SSL-Encryption.

There are two examples which can be find under `src/main/java/ssl`:

+ Use SSL with server-site certificate only
+ Use SSL with additional client-certificate.

Each package provides an example implementation of the RabbitMQ-Java-Client
and the used server configuration file.

The functionality is explained via in-line comments rather than writing a big article here.
Even the configuration files are commented properly.

# Create certificates on your own
OpenSSL ships directly with a Perl-Script which helps you to create your own Certificate Authority and use it to create new Certs which are signed by this very CA.

The Script can be found in
> `/usr/lib/ssl/misc/CA.pl`

For this tutorial I use the following version:
> ```
$~/> openssl version
`OpenSSL 1.0.2g  1 Mar 2016```

Please note that CA.pl uses the standard configuration from `/etc/ssl/openssl.conf`. For example changing the period of validity for certificates or using 4096 rather than 2048 bit keys, this can be done there.

## Step 1: Create the root cert
There will be a dialog asking for details about the certificate. Afterwards the CA folder structure will be located under `./demoCA` (which can be changed in the `openssl.conf`). Examples are in {brackets}.

```
$~/> /usr/lib/ssl/misc/CA.pl -newca
CA certificate filename (or enter to create)

Making CA certificate ...
Generating a 2048 bit RSA private key
..................................................+++
.......................................................................................................................+++
writing new private key to './demoCA/private/cakey.pem'
Enter PEM pass phrase:
Verifying - Enter PEM pass phrase:
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]: {DE}
State or Province Name (full name) [Some-State]: {BW}
Locality Name (eg, city) []: {Esslingen}
Organization Name (eg, company) [Internet Widgits Pty Ltd]: {Awesome Company}
Organizational Unit Name (eg, section) []:
Common Name (e.g. server FQDN or YOUR name) []: {awesomecompany.com}
Email Address []: {awesomemail@provider.tld}

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []: {This can be empty}
An optional company name []: {This can be empty}
Using configuration from /usr/lib/ssl/openssl.cnf
Enter pass phrase for ./demoCA/private/cakey.pem: {same as defined above}
```
Congratulations! You sucessfully created your own certificate authority! Next, let's create certificates. But first...
## Step 2: Permissions
Little but important step to make sure that root only can read the keys
> ```
$~/> chmod -R 400 ./demoCA
$~/> chown -R root:root ./demoCA```

## Step 3: Create a new cert

Zertifikat ersellen und anschließend so konvertieren, damit man es in den Clientcode einbinden kann
`openssl pkcs12 -export -in my.cert.pem -inkey my.key.pem -out my.p12`


# Troubleshooting
Make sure
- to check the rabbitmq-server logfiles under `/var/log/rabbitmq/` on the server.
- to define the connection details like `HOST, PORT, USERNAME, PASSWORD` correctly
- to create valid certificates and CA and provide them in the right format [1]. You can verify the certs and the chain-of-trust with openssl.
- to check paths permissions (must be readable by the rabbitmq user e.g. `chown -R rabbitmq:rabbitmq demoCA/ && chmod 400 demoCA/`)

# Notes and References
[1] https://wiki.ubuntuusers.de/CA/ (German)

[2] https://workaround.org/certificate-authority/

[3] https://www.openssl.org/docs/man1.0.1/apps/CA.pl.html

[3] https://www.rabbitmq.com/ssl.html

[4] https://www.rabbitmq.com/configure.html
