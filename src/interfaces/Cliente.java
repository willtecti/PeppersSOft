package interfaces;

public interface Cliente {
	
	public String getTipoPessoa();
	public int getNumCadNacional();
	public int getNumCadEstadual();
	public void setNumCadNacional(int num);
	public void setNumCadEstadual(int num);
	public int getCodigo();
	public String getNome();
	public String getEndereco();
	public String getTelefone();
	public void setNome(String nome);
	public void setEndereco(String endereco);
	public void setTelefone(String telefone);
	

}
