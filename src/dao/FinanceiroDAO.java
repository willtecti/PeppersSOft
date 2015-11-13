package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Financeiro;
import modelo.Produto;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FinanceiroDAO {

	private String path; // Caminho base
	private String pathToReportPackage; // Caminho para o package onde est�o
										// armazenados os relatorios Jarper

	// Recupera os caminhos para que a classe possa encontrar os relat�rios
	public FinanceiroDAO() {
		this.path = this.getClass().getClassLoader().getResource("").getPath();
		this.pathToReportPackage = this.path + "jr/";
	}

	public boolean cadastrar(Financeiro financeiro) {

		String sql = "INSERT INTO financeiro (id_codigo,discriminacao,valor,id_conta,tipo_lanc,data) values(null,?,?,?,?,?)";

		boolean retorno = false;
		try {
			Connection connection;
			PreparedStatement stmt;
			connection = ConnectionFactory.getConnection();
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, financeiro.getDiscriminacao());
			stmt.setFloat(2, financeiro.getValor());
			stmt.setInt(3, financeiro.getConta().getCod_Conta());
			stmt.setString(4, financeiro.getTipo_lanc());
			stmt.setDate(5, financeiro.getData());

			stmt.executeUpdate();
			stmt.close();
			ConnectionFactory.closeConnection(connection);
			retorno = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retorno;
	}

	public ResultSet consultar() throws Exception {
		Date data = new Date(System.currentTimeMillis());

		String sql = "SELECT * FROM financeiro where data = ? ";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.setDate(1, data);

		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	public ResultSet consultaData(String data) throws Exception {
		String valor = "DEVMEDIA - Java - Engenharia - Software";
		String[] valorComSplit = valor.split("-", 2);

		String sql = "SELECT * FROM financeiro where data = ? ";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);

		// stmt.setDate(1, data);

		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	public ResultSet consultaSQL(String sql) throws Exception {

		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}

	public Financeiro consultar(int id) throws Exception {
		Financeiro financeiro = null;
		try {
			financeiro = new Financeiro();
			String sql = "SELECT * FROM financeiro WHERE id_codigo = ?";
			Connection connection = ConnectionFactory.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			ConnectionFactory.closeConnection(connection);
			while (rs.next()) {
				financeiro.setDiscriminacao(rs.getString("discriminacao"));
				financeiro.setTipo_lanc(rs.getString("tipo_lanc"));
				financeiro.setValor(rs.getFloat("valor"));
			}
		} catch (Exception e) {

		}
		return financeiro;
	}

	public Financeiro consultar(Date data) throws Exception {
		Financeiro financeiro = null;

		try {
			financeiro = new Financeiro();
			String sql = "SELECT * FROM financeiro WHERE data = ?";
			Connection connection = ConnectionFactory.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setDate(1, data);

			ResultSet rs = stmt.executeQuery();
			ConnectionFactory.closeConnection(connection);
			while (rs.next()) {
				financeiro.setDiscriminacao(rs.getString("discriminacao"));
				financeiro.setTipo_lanc(rs.getString("tipo_lanc"));
				financeiro.setValor(rs.getFloat("valor"));
			}
		} catch (Exception e) {

		}
		return financeiro;
	}

	public boolean excluir(int i) throws Exception {

		String sql = "DELETE FROM financeiro WHERE id_codigo = ?";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, i);
		int linhasAfetadas = stmt.executeUpdate();
		stmt.close();
		ConnectionFactory.closeConnection(connection);

		if (linhasAfetadas > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<Produto> listar() throws Exception {
		List<Produto> produtos = new ArrayList<>();

		String sql = "SELECT * FROM financeiro";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		rs.close();
		stmt.close();
		ConnectionFactory.closeConnection(connection);
		return produtos;

	}

	// Imprime/gera uma lista Financeiro
	public void gerarRelatorio() throws Exception {

		List<Financeiro> financeiros = new ArrayList<>();

		String sql = "SELECT * FROM financeiro";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			financeiros.add(new Financeiro(rs.getInt("id_codigo"), rs.getString("discriminacao"),
					rs.getString("tipo_lanc"), rs.getFloat("valor")));
		}

		JasperReport report = JasperCompileManager.compileReport(this.getPathToReportPackage() + "FinanceiroRel.jrxml");
		JasperPrint print = JasperFillManager.fillReport(report, null, new JRBeanCollectionDataSource(financeiros));
		JasperExportManager.exportReportToPdfFile(print, "relatorios/Relatorio_Financeiro.pdf");

		rs.close();
		stmt.close();
		ConnectionFactory.closeConnection(connection);

	}

	public String getPathToReportPackage() {
		return this.pathToReportPackage;
	}

	public String getPath() {
		return this.path;
	}

}
