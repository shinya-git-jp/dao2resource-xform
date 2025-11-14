FROM tomcat:10.1-jdk17

COPY dao2jdbc-code-xform-sql-extractor-service/target/dao2jdbc-code-xform-sql-extractor-service-25.10.20-SNAPSHOT.jar dao2jdbc-code-xform-sql-extractor-service.jar
COPY dao2jdbc-entity-xform-entity-extractor-service/target/dao2jdbc-entity-xform-entity-extractor-service-25.10.20-SNAPSHOT.jar dao2jdbc-entity-xform-entity-extractor-service.jar

COPY dao2jdbc-code-xform-sql2code-service/target/dao2jdbc-code-xform-sql2code-service-25.10.20-SNAPSHOT.war /usr/local/tomcat/webapps/dao2jdbc-xform-sql2code.war
COPY dao2jdbc-entity-xform-entity2xml-service/target/dao2jdbc-entity-xform-entity2xml-service-25.10.20-SNAPSHOT.war /usr/local/tomcat/webapps/dao2jdbc-xform-entity2xml.war

EXPOSE 8080 8081 8082

CMD ["sh", "-c", "\
  java -jar dao2jdbc-code-xform-sql-extractor-service.jar --server.port=8081 & \
  java -jar dao2jdbc-entity-xform-entity-extractor-service.jar --server.port=8082 & \
  catalina.sh run"]
