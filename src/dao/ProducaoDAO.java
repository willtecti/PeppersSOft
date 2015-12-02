package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import modelo.ItemProducao;
import modelo.Producao;
import modelo.Produto;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class ProducaoDAO {
	private String path; // Caminho base
	private String pathToReportPackage; // Caminho para o package onde est�o
										// armazenados os relatorios Jarper

	// Recupera os caminhos para que a classe possa encontrar os relat�rios
	public ProducaoDAO() {
		this.path = this.getClass().getClassLoader().getResource("").getPath();
		this.pathToReportPackage = this.path + "jr/";
	}

	public boolean cadastrar(Producao producao) {

		String sql = "INSERT INTO producao (id_produto,descricao,estoque,pr_custo,pr_venda,margem_lucro) values(null,?,?,?,?,?)";

		boolean retorno = false;

		try {
			Connection connection;

			PreparedStatement stmt;
			connection = ConnectionFactory.getConnection();
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, producao.getData());
			//stmt.setArray(2, producao.getListaitens());
			stmt.setString(3, producao.getResponsavel());

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

	public boolean alterar(Producao producao) throws Exception {
		String sqlSelect = "SELECT * FROM produto WHERE id_produto = ?";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect);
		//stmtSelect.setInt(1, producao.getData());

		ResultSet rs = stmtSelect.executeQuery();

		boolean encontrou = rs.next();

		if (encontrou) {

			String sqlUpdate = "UPDATE produto SET descricao = ?, estoque = ?, pr_custo = ?, pr_venda = ?, margem_lucro = ? where id_produto = ?";
			PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate);

			//stmtUpdate.setString(1, produto.getDescricao());
			//stmtUpdate.setFloat(2, produto.getEstoque());
			//stmtUpdate.setFloat(3, produto.getPr_custo());
			//stmtUpdate.setFloat(4, produto.getPr_venda());
			//stmtUpdate.setFloat(5, produto.getMargem_lucro());
			//stmtUpdate.setInt(6, produto.getId_produto());

			stmtUpdate.executeUpdate();
			stmtUpdate.close();
			ConnectionFactory.closeConnection(connection);

			return true;

		} else {

			rs.close();
			stmtSelect.close();
			ConnectionFactory.closeConnection(connection);

			return false;
		}

	}

	public ResultSet consultar() throws Exception {

		String sql = "SELECT * FROM produto";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		return rs;

	}
	
	public ResultSet Busca(String param) throws Exception {

		String sql = "SELECT * FROM produto where descricao like '%"+param+"%'";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		return rs;

	}

	public Producao consultar(int id) throws Exception {
		Producao producao = new Producao();
		ItemProducao iproducao = new ItemProducao();
		try {
			
			String sql = "SELECT * FROM produto WHERE id_produto = ?";
			Connection connection = ConnectionFactory.getConnection();
			PreparedStatement stmt = connection.prepareStatement(sql);

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			
			if(rs.next()) {
				//producao.setData(rs.getInt("id_produto"));
				//producao.setDescricao(rs.getString("descricao"));
				//producao.sesetEstoque(rs.getFloat("estoque"));
				//producao.setMargem_lucro(rs.getFloat("margem_lucro"));
			}else{
				JOptionPane.showMessageDialog(null, "Deu treta de novo");
			}
		} catch (Exception e) {

		}
		return producao;
	}

	public boolean excluir(int i) throws Exception {

		String sql = "DELETE FROM produto WHERE id_produto = ?";
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

	public List<Producao> listar() throws Exception {

		List<Producao> producao = new ArrayList<>();

		String sql = "SELECT * FROM producao";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);

		ResultSet rs = stmt.executeQuery();
		rs.close();
		stmt.close();
		ConnectionFactory.closeConnection(connection);

		return producao;
	}
	public ResultSet buscar(String valor){
		ResultSet resultSet = null;
		return resultSet;
	}

	// Imprime/gera uma lista de Produtos
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void gerarRelatorio() throws Exception {

		// estabelece conex�o
		String sql = "SELECT * FROM producao";
		Connection connection = ConnectionFactory.getConnection();
		PreparedStatement stmt = connection.prepareStatement(sql);

		ResultSet rs = stmt.executeQuery();

		// gerando o jasper design
		JasperDesign desenho = JRXmlLoader.load(this.getPathToReportPackage() + "ProdutosRel.jrxml");

		// compila o relat�rio
		JasperReport relatorio = JasperCompileManager.compileReport(desenho);

		// implementa��o da interface JRDataSource para DataSource ResultSet
		JRResultSetDataSource jrRS = new JRResultSetDataSource(rs);

		// executa o relat�rio
		@SuppressWarnings("rawtypes")
		Map parametros = new HashMap();
		parametros.put("nota", new Double(10));
		JasperPrint impressao = JasperFillManager.fillReport(relatorio, parametros, jrRS);

		// exibe o resultado
		JasperViewer viewer = new JasperViewer(impressao, false);
		viewer.show();

	}

	public String getPathToReportPackage() {
		return this.pathToReportPackage;
	}

	public String getPath() {
		return this.path;
	}
	
}