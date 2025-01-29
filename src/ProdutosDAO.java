import java.awt.List;
import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ProdutosDAO {

    // Conexão, Statement e ResultSet para interação com o banco de dados
    private Connection conn;
    private PreparedStatement prep;
    private ResultSet resultset;
    
    // Método para cadastrar um produto no banco de dados
    public void cadastrarProduto(ProdutosDTO produto) throws java.sql.SQLException {
        conn = new conectaDAO().connectDB(); // Conexão com o banco de dados
        String sql = "INSERT INTO produtos (nome, valor, status) VALUES (?, ?, ?)";

        prep = conn.prepareStatement(sql);
        prep.setString(1, produto.getNome());
        prep.setInt(2, produto.getValor());
        prep.setString(3, produto.getStatus());
        prep.executeUpdate();
        JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
        
        // Fechar recursos para evitar vazamentos
        if (prep != null) prep.close();
        if (conn != null) conn.close();
    }
    
    // Método para listar os produtos vendidos do banco de dados
    public ArrayList<ProdutosDTO> listarVendas() throws java.sql.SQLException {
        conn = new conectaDAO().connectDB();
        String sql = "SELECT * FROM produtos";

        ArrayList<ProdutosDTO> vendas = new ArrayList<>();
        prep = conn.prepareStatement(sql);
        resultset = prep.executeQuery();
        while (resultset.next()) {
            ProdutosDTO produto = new ProdutosDTO();
            produto.setId(resultset.getInt("id"));
            produto.setNome(resultset.getString("nome"));
            produto.setValor(resultset.getInt("valor"));
            produto.setStatus(resultset.getString("status"));
            vendas.add(produto);
        }
        
        // Fechar recursos
        if (resultset != null) resultset.close();
        if (prep != null) prep.close();
        if (conn != null) conn.close();

        return vendas;
    }

    // Método para listar todos os produtos (não vendidos) do banco de dados
    public ArrayList<ProdutosDTO> listarProdutos() throws java.sql.SQLException {
        conn = new conectaDAO().connectDB();
        ArrayList<ProdutosDTO> produtos = new ArrayList<>();
        
        String sql = "SELECT * FROM produtos WHERE status = 'Vendido'"; // SQL para listar produtos vendidos
        
        prep = conn.prepareStatement(sql);
        resultset = prep.executeQuery();
        while (resultset.next()) {
            ProdutosDTO produto = new ProdutosDTO();
            produto.setId(resultset.getInt("id"));
            produto.setNome(resultset.getString("nome"));
            produto.setValor(resultset.getInt("valor"));
            produto.setStatus(resultset.getString("status"));
            produtos.add(produto);
        }
        
        // Fechar recursos
        if (resultset != null) resultset.close();
        if (prep != null) prep.close();
        if (conn != null) conn.close();

        return produtos;
    }

    // Método para vender um produto, alterando seu status no banco de dados
    public boolean venderProduto(int id) throws java.sql.SQLException {
        conn = new conectaDAO().connectDB();
        String sqlVerificaStatus = "SELECT status FROM produtos WHERE id = ?";

        try (PreparedStatement pstVerifica = conn.prepareStatement(sqlVerificaStatus)) {
            pstVerifica.setInt(1, id);
            ResultSet rs = pstVerifica.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("Vendido".equals(status)) {
                    JOptionPane.showMessageDialog(null, "Produto já foi vendido!");
                    return false; // Impede a venda se o produto já foi vendido
                }
            }

            // Atualiza o status do produto para "Vendido"
            String sqlAtualizaStatus = "UPDATE produtos SET status = 'Vendido' WHERE id = ?";
            try (PreparedStatement pstAtualiza = conn.prepareStatement(sqlAtualizaStatus)) {
                pstAtualiza.setInt(1, id);
                int rowsAffected = pstAtualiza.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Produto vendido com sucesso!");
                    return true;
                } else {
                    return false;
                }
            }
        } finally {
            if (conn != null) conn.close(); // Fechando a conexão
        }
    }

}
