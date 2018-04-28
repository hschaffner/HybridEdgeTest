# HybridEdge Starter Project

## Introduction

[HybridEdge](https://solace.com/) is a Solace Inc. software product 
that makes it easy to connect existing systems, and in particular messaging systems, to Solace Messaging.
It requires a minimum of setup and configuration.

The HybridEdge Starter Project allows developers to write more advanced integration products
when more custom logic is required. It is based on Spring boot and already contains the dependencies
required to connect to a Solace messaging product.

Internally, it uses [Apache Camel](http://camel.apache.org/) to connect with the integration points.

## Usage

* Update permissions to make gradlew executable:
    <pre><code>chmod 755 gradlew</code></pre>

* Edit src/main/resources/application.properties, adding the Solace host, username, password and vpn name as required.
The file should then look something like this:

    ```# Solace credentials```

    ```solace.jms.host=mr-xxxxxx.messaging.solace.cloud:20000```

    ```solace.jms.msgVpn=myvpn```

    ```solace.jms.clientUsername=my-client-username```

    ```solace.jms.clientPassword=a3i5gm9r1n3s05sc3384k9mlhs```

    ```# Camel/Spring config files should be listed here.```

    ```spring.main.sources=hybrid-edge.xml```

    ```# Required so that Camel will keep running```

    ```camel.springboot.main-run-controller=true```

* A sample configuration file is provided in src/main/resources/hybrid-edge.xml. You can test with that, or else copy a sample Camel configuration file from the samples directory into the src/main/resources directory, and edit it as required (see below.)
You can either rename it to hybrid-edge.xml, or change the spring.main.sources property in application.properties.

* Edit the build.gradle file, adding any required dependencies to Camel connectors such as ActiveMQ or RabbitMQ.

* Make sure the gradlew script is executable and then build the application by running
    <pre><code>./gradlew assemble</code></pre>

* Start the application by running
    <pre><code>java -jar build/libs/hybrid-edge-starter.jar</code></pre>

## Solace JMS Endpoint Properties

A Solace Camel JMS endpoint has the following structure:

```solace-jms: topic|queue : topicOrQueueName [? property=value [& property=value] ... ]```

For example:

```solace-jms:topic:/my/topic?deliveryMode=2&amp;timeToLive=60000```

Note that in an xml file, ampersands need to be written as ```&amp;``` and the greater-than symbol needs to be written as ```&gt;```

Because the Solace JMS component reuses the standard Camel JMS component, it
supports the same set of properties.

Please see the [Camel JMS component documentation](http://camel.apache.org/jms.html) for details.


## Solace JMS Connection Factory Properties

Besides the properties on the JMS endpoint, it is necessary to configure the Solace JMS Connection Factory properties.

This is done by editing the Spring application.properties file. An example was given at the top of this document,
but for reference here is the list of supported properties.

Required:

<pre>
solace.jms.host
solace.jms.msgVpn
solace.jms.clientUsername 
solace.jms.clientPassword
</pre>

Optional:

<pre>
solace.jms.directTransport
solace.jms.clientDescription
</pre>


Please see the [Solace JMS API documentation](https://docs.solace.com/API-Developer-Online-Ref-Documentation/jms/com/solacesystems/jms/SolConnectionFactory.html)
for details on these properties.

N.B. The clientDescription property defaults to showing the component and underlying camel versions, e.g.

```CamelSolaceJMS version 1.0.0 Camel version: 2.21.0```


## Connection Factory Session Caching

This component uses Spring's CachingConnectionFactory to cache connections. By default it the cache size is 1.

## Methods of setting properties

Because the component uses Spring, these properties can be set using Java system properties or environment variables rather than use the application.properties file.

### Java system properties

On the command line you can set properties like this:
```java -Dsolace.jms.host=myhost.com ...```

### Environment Variables

You can set the properties as environment variables in this form:

<pre>
SOLACE_JMS_HOST=myhost.com
SOLACE_JMS_MSG_VPN=vpn1
</pre>

## Properties for specific use cases

### Guaranteed Messaging Acknowledgements

Suppose you are subscribing from a Solace instance and publishing to another JMS broker, and you 
don't want to acknowledge a message from Solace until you are sure that the message was received on the other broker.

In this case, you want to ensure that the directTransport property is false (which is the default anyway)

```solace.jms.directTransport=false```

Further, you need to ensure that the ```transacted``` property is set on the Solace endpoint, e.g.

```<c:from uri="solace-jms:queue:testQueue?transacted=true"/>```

## Encrypting passwords

HybridEdge supports the [Camel method of encrypting passwords.](http://camel.apache.org/jasypt.html) which uses the jasypt library.

Here are instructions on how to do this. Please see the examples in samples/encryption.

1. Download these jars from [Maven Central](https://search.maven.org/)

    [camel-jasypt-2.20.2.jar](https://search.maven.org/#search%7Cga%7C1%7Cg%3Aorg.apache.camel%20AND%20a%3Acamel-jasypt)

    [jasypt-1.9.2.jar](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.jasypt%22%20AND%20a%3A%22jasypt%22)

2. Pick a password that will be used to encrypt and decrypt your Solace password, e.g. 'myCamelPassword'

3. Run this command to encrypt your Solace password, for example 'mySolacePassword':

    ```java -cp "camel-jasypt-2.20.2.jar;jasypt-1.9.2.jar" org.apache.camel.component.jasypt.Main -c encrypt -p myCamelPassword -i mySolacePassword```

    jasypt will respond with something like:

    ```Encrypted text: mCdmWUhQSQu+1AYUGq48R75WfUanyOf3lV6i89IKZt0=```

    Take the encrypted password and put it into your application.properties file like this:

    ```my.encrypted.password=ENC(mCdmWUhQSQu+1AYUGq48R75WfUanyOf3lV6i89IKZt0=)```

    and use it in the endpoing configuration like this:

    ```uri:solace:topic:myTopic?password={{my.encrypted.password}}```

4. Add this to your config file:

    ```<bean id="jasypt" class="org.apache.camel.component.jasypt.JasyptPropertiesParser">```

	```<property name="password" value="${jasypt.password}"/>```

	```</bean>```

   ```<c:camelContext>```
   
	```<c:propertyPlaceholder id="properties" location="classpath:application.properties" propertiesParserRef="jasypt"/>```


5. Finally, provide the jasypt password as a system property or environment variable:

    ```java ... -Djasypt.password=myCamelPassword```

    or

    ```export JASPYT_PASSWORD=myCamelPassword```

## Third-Party Components

Here are some product-specific instructions.

### RabbitMQ 
Uncomment the RabbitMQ dependencies in build.gradle

Use the file samples/rabbitmq.xml as a reference.

Configuration instructions are here: http://camel.apache.org/rabbitmq.html


### ActiveMQ
Uncomment the ActiveMQ dependencies in build.gradle

Use the file samples/activemq.xml as a reference. In particular, note the bean configuration:

<bean id="activemq" class="org.apache.activemq.camel.component.ActiveMQComponent">
    <property name="brokerURL" value="tcp://hostname:61616"/>
</bean>

Change the brokerURL value as appropriate.

Configuration instructions are here: http://camel.apache.org/activemq.html


