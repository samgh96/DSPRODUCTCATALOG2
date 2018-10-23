#!/bin/bash

# Compile sources
cd /catalog
mvn install

# Create database if needed
MYSQL_HOST=mysql.docker
MYSQL_ROOT_PASSWORD=toor
MYSQL_URL="jdbc\:mysql\://${MYSQL_HOST}\:3306/DSPRODUCTCATALOG2"

mysql -u root --password=${MYSQL_ROOT_PASSWORD} -h ${MYSQL_HOST} -e "CREATE DATABASE IF NOT EXISTS DSPRODUCTCATALOG2;"

# Create database jdbc conection and deploy the war

export PATH=$PATH:/glassfish4/glassfish/bin
asadmin start-domain

asadmin create-jdbc-connection-pool --restype java.sql.Driver --driverclassname com.mysql.jdbc.Driver --property user=root:password=${MYSQL_ROOT_PASSWORD}:URL=${MYSQL_URL} DSPRODUCTCATALOG2

asadmin create-jdbc-resource --connectionpoolid DSPRODUCTCATALOG2 jdbc/pcatv2

asadmin deploy --force true --contextroot DSProductCatalog --name DSProductCatalog /catalog/target/DSProductCatalog.war
