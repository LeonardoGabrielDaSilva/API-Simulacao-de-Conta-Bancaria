package com.br.linkedrh.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import com.br.linkedrh.ContaBancariaApplication;
import com.br.linkedrh.model.Agencia;

@Repository
public class AgenciaRepository {

	public static Connection connection = ContaBancariaApplication.getConnection();
	
	public static Agencia getSelectAgencia(int numAgencia) {
		try {
			String sql = "SELECT id, nome, endereco FROM linkedrh.agencia WHERE id=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, numAgencia);
			ResultSet query = ps.executeQuery();
			if (query.next()) {
				return new Agencia(numAgencia, query.getString("nome"), query.getString("endereco"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean existsAgencia(int numAgencia) {
		try {
			String sql = "SELECT * FROM linkedrh.agencia WHERE ID=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, numAgencia);
			ResultSet executeQuery = ps.executeQuery();
			return executeQuery.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
