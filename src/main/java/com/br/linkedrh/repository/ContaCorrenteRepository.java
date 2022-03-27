package com.br.linkedrh.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import com.br.linkedrh.ContaBancariaApplication;
import com.br.linkedrh.dto.ContaDTO;
import com.br.linkedrh.dto.ContasCorrentistaDTO;
import com.br.linkedrh.model.ContaCorrente;
import com.br.linkedrh.model.Correntista;

@Repository
public class ContaCorrenteRepository {

	// Por algum motivo que não encontrei solução, o Autowired fazendo a injeção de
	// dependência do connection definido na classe principal não estava
	// funcionando.

	public static Connection connection = ContaBancariaApplication.getConnection();

	public static boolean createContaCorrente(ContaCorrente contaCorrente) {
		try {
			String sql = "INSERT INTO linkedrh.contacorrente (ID_Correntista, ID_Agencia, Limite, Saldo, Ativa)"
					+ " VALUES (?,?,?,?,'T')";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, contaCorrente.getCorrentista().getId());
			ps.setInt(2, contaCorrente.getAgencia().getId());
			ps.setBigDecimal(3, contaCorrente.getLimite());
			ps.setBigDecimal(4, contaCorrente.getSaldo());
			int executeUpdate = ps.executeUpdate();
			if (executeUpdate > 0) {
				return true;
			}
		} catch (SQLIntegrityConstraintViolationException ex) {
			System.out.println(ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static ContaCorrente getContaCorrente(int numAgencia, String cpf) {
		int idCorrentista = CorrentistaRepository.getCorrentistaByCpf(cpf).getId();
		try {
			String sql = "SELECT ID, ID_Correntista, ID_Agencia, Limite, Saldo, Ativa "
					+ "FROM linkedrh.contacorrente WHERE ID_Agencia = ? AND ID_Correntista = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, numAgencia);
			ps.setInt(2, idCorrentista);
			ResultSet result = ps.executeQuery();
			result.next();
			ContaCorrente conta = new ContaCorrente(result.getInt("id"), CorrentistaRepository.getCorrentistaByCpf(cpf),
					AgenciaRepository.getSelectAgencia(numAgencia), result.getBigDecimal("limite"),
					result.getBigDecimal("saldo"), result.getString("ativa").toCharArray()[0]);
			return conta;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ContaCorrente getContaCorrente(int numAgencia, int numContaCorrente) {
		try {
			String sql = "SELECT ID, ID_Correntista, ID_Agencia, Limite, Saldo, Ativa "
					+ "FROM linkedrh.contacorrente WHERE ID_Agencia = ? AND ID = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, numAgencia);
			ps.setInt(2, numContaCorrente);
			ResultSet result = ps.executeQuery();
			result.next();
			ContaCorrente conta = new ContaCorrente(numContaCorrente,
					CorrentistaRepository.getCorrentistaById(result.getInt("ID_Correntista")),
					AgenciaRepository.getSelectAgencia(numAgencia), result.getBigDecimal("limite"),
					result.getBigDecimal("saldo"), result.getString("ativa").toCharArray()[0]);
			return conta;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static BigDecimal getLimiteSaqueContaCorrente(int numAgencia, int numContaCorrente) {
		try {
			String sql = "SELECT limite + saldo AS limite FROM linkedrh.contacorrente WHERE id_agencia = ? AND id = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, numAgencia);
			ps.setInt(2, numContaCorrente);
			ResultSet result = ps.executeQuery();
			result.next();
			return result.getBigDecimal("limite");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static BigDecimal getSaldo(int numAgencia, int numContaCorrente) {
		try {
			String sql = "SELECT saldo FROM linkedrh.contacorrente WHERE id = ? AND id_agencia = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, numContaCorrente);
			ps.setInt(2, numAgencia);
			ResultSet result = ps.executeQuery();
			if (result.next()) {
				return result.getBigDecimal("saldo");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean doDeposito(int numAgencia, int numContaCorrente, BigDecimal valor) {
		try {
			String sql = "UPDATE linkedrh.contacorrente SET saldo = saldo + ? WHERE id = ? AND id_agencia = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setBigDecimal(1, valor);
			ps.setInt(2, numContaCorrente);
			ps.setInt(3, numAgencia);
			int update = ps.executeUpdate();
			if (update > 0) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static BigDecimal doSaque(int numAgencia, int numContaCorrente, BigDecimal valor) {
		if (ContaCorrenteRepository.getContaCorrente(numAgencia, numContaCorrente).isAtiva()) {
			try {
				String sql = "UPDATE linkedrh.contacorrente"
						+ "   SET saldo = CASE WHEN saldo + limite - ? >= 0 THEN saldo - ?" + "   ELSE saldo" + "   END"
						+ " WHERE id = ? AND id_agencia = ?";
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setBigDecimal(1, valor);
				ps.setBigDecimal(2, valor);
				ps.setInt(3, numContaCorrente);
				ps.setInt(4, numAgencia);
				int update = ps.executeUpdate();
				if (update > 0) {
					return getSaldo(numAgencia, numContaCorrente);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public static BigDecimal doTransferencia(int numAgenciaOrig, int numContaCorrenteOrig, int numAgenciaDest,
			int numContaCorrenteDest, BigDecimal valor) {
		doDeposito(numAgenciaDest, numContaCorrenteDest, valor);
		return doSaque(numAgenciaOrig, numContaCorrenteOrig, valor);
	}

	public static ContaCorrente doDesativar(int numAgencia, int numContaCorrente) {
		try {
			String sql = "UPDATE linkedrh.contacorrente SET ativa = ? WHERE id = ? AND id_agencia = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, "f");
			ps.setInt(2, numAgencia);
			ps.setInt(3, numContaCorrente);
			int update = ps.executeUpdate();
			if (update > 0) {
				return getContaCorrente(numAgencia, numContaCorrente);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static ContasCorrentistaDTO getContasCorrentistaDTO(String cpf) {
		try {
			String sql = "SELECT correntista.nome," + "       correntista.cpf," + "       correntista.nascimento,"
					+ "       contacorrente.id_agencia," + "       contacorrente.id,"
					+ "       CASE WHEN contacorrente.Limite > 0" + "			THEN 'especial'"
					+ "            ELSE 'comum'" + "		END AS tipo" + "  FROM correntista"
					+ "  LEFT JOIN contacorrente" + "	ON contacorrente.ID_Correntista = correntista.id"
					+ " WHERE correntista.cpf = ?";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, cpf);
			ResultSet result = ps.executeQuery();
			result.next();
			Correntista correntista = new Correntista(result.getString(1), result.getString(2), result.getDate(3));
			ContasCorrentistaDTO contaCorrentista = new ContasCorrentistaDTO(correntista, new ArrayList<ContaDTO>());
			do {
				contaCorrentista.getListaContas()
						.add(new ContaDTO(result.getInt("id_agencia"), result.getInt("id"), result.getString("tipo")));
			} while (result.next() == true);
			return contaCorrentista;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean existsContaCorrente(int numAgencia) {
		try {
			String sql = "SELECT * FROM linkedrh.agencia WHERE id=?";
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
