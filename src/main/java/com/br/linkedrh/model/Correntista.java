package com.br.linkedrh.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Correntista {

	private int id;
	private String nome;
	private String cpf;
	private Date nascimento;
	
	public Correntista(String nome, String cpf, Date nascimento) {
		this.nome = nome;
		this.cpf = cpf;
		this.nascimento = nascimento;
	}
	
}
