package com.br.linkedrh.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.br.linkedrh.dto.ContasCorrentistaDTO;
import com.br.linkedrh.model.ContaCorrente;
import com.br.linkedrh.model.Correntista;
import com.br.linkedrh.repository.AgenciaRepository;
import com.br.linkedrh.repository.ContaCorrenteRepository;
import com.br.linkedrh.repository.CorrentistaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/conta")
public class ControllerContaCorrente {

	@GetMapping("/criar")
	public ResponseEntity<ContaCorrente> criarContaCorrente(int numAgencia,
			@RequestParam(value = "Correntista") String correntistaDTO, BigDecimal limite, BigDecimal saldoInicial)
			throws JsonMappingException, JsonProcessingException {
		
		Correntista correntista = new ObjectMapper().setDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
				.readValue(correntistaDTO, Correntista.class);
		if (!AgenciaRepository.existsAgencia(numAgencia)) {
			return ResponseEntity.status(404).body(null);
		}
		if (saldoInicial.compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.status(403).body(null);
		}
		ContaCorrente conta = new ContaCorrente(correntista, AgenciaRepository.getSelectAgencia(numAgencia), limite,
				saldoInicial);
		if (!CorrentistaRepository.existsCorrentistaByCpf(correntista.getCpf())) {
			CorrentistaRepository.createCorrentista(correntista);
		}
		conta.setCorrentista(CorrentistaRepository.getCorrentistaByCpf(correntista.getCpf()));
		if (!ContaCorrenteRepository.createContaCorrente(conta)) {
			return ResponseEntity.status(403).body(null);
		}
		return ResponseEntity.status(201)
				.body(ContaCorrenteRepository.getContaCorrente(numAgencia, correntista.getCpf()));
	}
	
	@GetMapping("/consultarSaldo")
	public ResponseEntity<BigDecimal> consultarSaldo(int numAgencia, int numConta) {
		if (!AgenciaRepository.existsAgencia(numAgencia)) {
			return ResponseEntity.status(404).body(null);
		}
		return ResponseEntity.status(200).body(ContaCorrenteRepository.getSaldo(numAgencia, numConta));
	}

	@GetMapping("/depositar")
	public ResponseEntity<BigDecimal> depositarSaldo(int numAgencia, int numConta, BigDecimal valor) {
		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.status(403).body(null);
		}
		if (!AgenciaRepository.existsAgencia(numAgencia)) {
			return ResponseEntity.status(404).body(null);
		}
		ContaCorrenteRepository.doDeposito(numAgencia, numConta, valor);
		return ResponseEntity.status(200).body(ContaCorrenteRepository.getSaldo(numAgencia, numConta));
	}

	@GetMapping("/sacar")
	public ResponseEntity<BigDecimal> sacarSaldo(int numAgencia, int numConta, BigDecimal valor) {
		if (!AgenciaRepository.existsAgencia(numAgencia)) {
			return ResponseEntity.status(404).body(null);
		}
		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.status(403).body(null);
		}
		return ResponseEntity.status(200).body(ContaCorrenteRepository.doSaque(numAgencia, numConta, valor));
	}

	@GetMapping("/transferir")
	public ResponseEntity<BigDecimal> transferirSaldo(int numAgenciaOrig, int numContaOrig, int numAgenciaDest,
			int numContaDest, BigDecimal valor) {
		if (!AgenciaRepository.existsAgencia(numAgenciaOrig) || !AgenciaRepository.existsAgencia(numAgenciaDest)) {
			return ResponseEntity.status(404).body(null);
		}
		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.status(403).body(null);
		}
		if (ContaCorrenteRepository.getLimiteSaqueContaCorrente(numAgenciaOrig, numContaOrig).compareTo(valor) < 0) {
			return ResponseEntity.status(403).body(null);
		}
		ContaCorrenteRepository.doTransferencia(numAgenciaOrig, numContaOrig, numAgenciaDest, numContaDest, valor);
		return ResponseEntity.status(200).body(ContaCorrenteRepository.getSaldo(numAgenciaOrig, numContaOrig));
	}

	@GetMapping("/consultarCorrentista")
	public ResponseEntity<ContasCorrentistaDTO> consultarCorrentista(String cpf) {
		if (!CorrentistaRepository.existsCorrentistaByCpf(cpf)) {
			return ResponseEntity.status(404).body(null);
		} else {
			return ResponseEntity.status(200).body(ContaCorrenteRepository.getContasCorrentistaDTO(cpf)); 
		}
	}
	
	@GetMapping("/desativar")
	public ResponseEntity<ContaCorrente> desativarConta(int numAgencia, int numConta) {
		if (!AgenciaRepository.existsAgencia(numAgencia)) {
			return ResponseEntity.status(403).body(null);
		}
		if (!ContaCorrenteRepository.getContaCorrente(numAgencia, numConta).isAtiva()) {
			return ResponseEntity.status(304).body(null);
		}
		return ResponseEntity.status(200).body(ContaCorrenteRepository.doDesativar(numAgencia, numConta));
	}

}
