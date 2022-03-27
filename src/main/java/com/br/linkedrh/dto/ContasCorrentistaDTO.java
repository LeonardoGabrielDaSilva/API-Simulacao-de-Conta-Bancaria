package com.br.linkedrh.dto;

import java.util.List;

import com.br.linkedrh.model.Correntista;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContasCorrentistaDTO {

	private Correntista correntista;
	private List<ContaDTO> listaContas;
	
}
