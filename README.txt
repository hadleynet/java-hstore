Java hStore
-----------

A set of JAX-RS resource classes that implement the hData RESTful API. The resource classes make 
use of a provider interface to access backend data.

The java-hstore-sample project shows how to integrate this project into a Web application. The key
points are to:

(i) Include the following in the project's web.xml

    <servlet>
        <servlet-name>Jersey Web Application</servlet-name>
        <servlet-class>com.sun.jersey.spi.container.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>com.sun.jersey.config.property.packages</param-name>
            <param-value>org.projecthdata.javahstore.resources;org.projecthdata.javahstore.representations</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

(ii) Implement the HDRProvider interface and identify the implementation class in

	WEB-INF/classes/META-INF/services/org.projecthdata.javahstore.hdr.HDRProvider
	
See java.util.ServiceLoader for complete details.

(iii) Include a dependency:

        <dependency>
            <groupId>org.projecthdata</groupId>
            <artifactId>java-hstore</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

Non-Maven projects will also need to include jersey-server-1.3, jersey-atom-abdera-1.3, 
jersey-multipart-1.3 and all of their required dependencies.