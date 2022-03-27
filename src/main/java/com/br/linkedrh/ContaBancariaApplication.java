package com.br.linkedrh;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ContaBancariaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaBancariaApplication.class, args);
	}

	@Bean
	public static Connection getConnection() {
		try {
			String driverName = "com.mysql.cj.jdbc.Driver"; 
			Class.forName(driverName);
			String serverName = "localhost";
			String mydatabase = "linkedrh";
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; 
			String username = "root";
			String password = "admin";
			return DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException();
		}
	}
}
