package com.br.linkedrh.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContaCorrente {

	private int id;
	private Correntista correntista;
	private Agencia agencia;
	private BigDecimal limite;
	private BigDecimal saldo;
	private char ativa;

	public ContaCorrente(Correntista correntista, Agencia agencia, BigDecimal limite, BigDecimal saldo) {
		this.correntista = correntista;
		this.agencia = agencia;
		this.limite = limite;
		this.saldo = saldo;
		this.ativa = 't';
	}
	
	public boolean isAtiva() {
		if(ativa == 't' || ativa == 'T') {
			return true;
		}
		return false;
	}
}
