package com.br.linkedrh.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContaDTO {

	private int numeroAgencia;
	private int numeroConta;
	private String tipo;

}
