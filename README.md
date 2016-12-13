# How to secure RabbitMQ messages with SSL encryption  
This Repository shows an example configuration about how to use RabbitMQ-Server with SSL-Encryption.

There are two examples which can be find under `src/main/java/ssl`:

+ Use SSL with server-site certificate only
+ Use SSL with additional client-certificate. 

Each package provides an example implementation of the RabbitMQ-Java-Client
and the used server configuration file. 

The functionality is explained via in-line comments rather than writing a big article here.
Even the configuration files are commented properly.

git 

# Troubleshooting
Make sure
- to check the rabbitmq-server logfiles under /var/log/rabbitmq/. 
- to define the connection details like HOST, PORT, USERNAME, PASSWORD correctly
- to create valid certificates and CA and provide them in the right format[1]
- to check paths permissions (must be readable by the rabbitmq user)
