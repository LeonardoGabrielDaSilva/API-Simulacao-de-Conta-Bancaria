package com.br.linkedrh.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import com.br.linkedrh.ContaBancariaApplication;
import com.br.linkedrh.model.Correntista;

@Repository
public class CorrentistaRepository {

	public static Connection connection = ContaBancariaApplication.getConnection();

	public static Correntista getCorrentistaById(int id) {
		try {
			String sql = "SELECT id, nome, cpf, nascimento FROM linkedrh.correntista WHERE id=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet query = ps.executeQuery();
			if (query.next()) {
				return new Correntista(id, query.getString("nome"), query.getString("cpf"), query.getDate("nascimento"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Correntista getCorrentistaByCpf(String cpf) {
		try {
			String sql = "SELECT id, nome, cpf, nascimento FROM linkedrh.correntista WHERE cpf=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, cpf);
			ResultSet query = ps.executeQuery();
			if (query.next()) {
				return new Correntista(query.getInt("id"), query.getString("nome"), cpf, query.getDate("nascimento"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static boolean createCorrentista(Correntista correntista) {
		try {
			String sql = "INSERT INTO linkedrh.correntista (nome, cpf, nascimento)"
					+ " VALUES (?,?,?)";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, correntista.getNome());
			ps.setString(2, correntista.getCpf());
			ps.setDate(3, correntista.getNascimento());
			int executeUpdate = ps.executeUpdate();
			if (executeUpdate > 0) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static boolean existsCorrentistaByCpf(String cpf) {
		try {
			String sql = "SELECT * FROM linkedrh.correntista WHERE cpf=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, cpf);
			ResultSet executeQuery = ps.executeQuery();
			return executeQuery.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	public static boolean existsCorrentistaById(int id) {
		try {
			String sql = "SELECT * 0 FROM linkedrh.correntista WHERE id=?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet executeQuery = ps.executeQuery();
			return executeQuery.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
