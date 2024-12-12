module rentalapp {
    requires spring.context;
    requires spring.beans;
    requires java.sql;
    requires org.slf4j;
    requires mysql.connector;


    opens rentalapp;
    opens rentalapp.entities;
    opens rentalapp.repositories;
    opens rentalapp.service;
    opens rentalapp.views;
    opens rentalapp.config;
}
