/*
 * Esta classe foi substituida pela classe Sinal, pois o nome indicava melhor a 
 * representacao a ser feita. O arquivo foi mantido apenas para histórico e
 * provavelmente será apagado no momento da geração do aplicativo final
 * 
 */
package dqgg;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Willian
 */
public class Representacao {    
    private final String tabela = "REPRESENTACAO";
    private int    id;
    private String nome;
    private String movimentos;
    private String descricao;
    private String arquivo;
    private String arquivoSinais;
    
    public Representacao(int id, String nome, String movimentos, String descricao, String arquivo, String arquivoSinais){        
        this.id            = id;
        this.nome          = nome;
        this.movimentos    = movimentos;
        this.descricao     = descricao;
        this.arquivo       = arquivo;
        this.arquivoSinais = arquivoSinais;        
    }
    
    public Representacao()
    {        
    }
    
    public int getId()
    {
        return (this.id);
    }
    
    public String getNome()
    {
        return (this.nome);
    }
    
    public String getMovimentos()
    {
        return (this.movimentos);
    }
    
    public String getDescricao()
    {
        return (this.descricao);
    }
    
    public String getArquivo()
    {
        return (this.arquivo);
    }
    
    public String getArquivoSinais()
    {
        return (this.arquivoSinais);
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setNome(String nome)
    {
        this.nome = nome;
    }
    
    public void setMovimentos(String movimentos)
    {
        this.movimentos = movimentos;
    }
    
    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }
    
    public void setArquivo(String arquivo)
    {
        this.arquivo = arquivo;
    }
    
    public void setArquivoSinais(String arquivo)
    {
        this.arquivoSinais = arquivo;
    }
        
    /**
     *
     * @return True ou false indicando se a operação foi executada com sucesso
     */
    public boolean insereBD()
    {   
        String sql = "INSERT INTO " + this.tabela + "(NOME,MOVIMENTOS,DESCRICAO,IMG_REPRES,IMG_SINAL) VALUES (" + 
                     "\"" + this.nome + "\"," +
                     "\"" + this.movimentos +  "\"," +
                     "\"" + this.descricao +  "\"," +
                     "\"" + this.arquivo +  "\"," +
                     "\"" + this.arquivoSinais + "\")";
        
        return (DQGG.bd.insere(sql));        
    }
    
    /**
     *
     * @param campos 
     * @return True ou false indicando se a operação foi executada com sucesso
     */
    public boolean atualizaBD(HashMap campos)
    {
        String sql = "UPDATE " + this.tabela + " SET ";        
        String valores = "";
        
      // Pega o conjuntos da entrada do hash
      Set set = campos.entrySet();
      // Pega um iterator
      Iterator i = set.iterator();
      // Monta lista de colunas e valores
      while(i.hasNext()) {
         Map.Entry me = (Map.Entry)i.next();         
         valores += me.getKey().toString() + " = '"+ me.getValue().toString() + "'";
         if (i.hasNext())
             valores += ",";         
      }
      
      sql += valores + " WHERE ID = " + this.getId();       
      return (DQGG.bd.atualiza(sql));
    }
    
    /**
     *
     * @param campos
     * @return ArrayList contendo instâncias de Sina
     */
    public ArrayList<Representacao> consultaBD(HashMap campos)
    {
        ArrayList<Representacao> registros = new ArrayList<Representacao>();
        try {
            String valores = "";
            String sql = "SELECT * FROM " + this.tabela + " WHERE ";
            
          // Pega o conjuntos da entrada do hash
          Set set = campos.entrySet();
          // Pega um iterator
          Iterator i = set.iterator();
          // Monta lista de colunas e valores
          while(i.hasNext()) {
             Map.Entry me = (Map.Entry)i.next();         
             valores += me.getKey().toString() + " LIKE '%"+ me.getValue().toString() + "%'";
             if (i.hasNext())
                 valores += " AND ";         
          }
          
          sql += valores;       
          sql +=  " ORDER BY ROWID ASC";        
          
          ResultSet rs = DQGG.bd.consulta(sql);          
          while (rs.next())
          {
            Representacao r = new Representacao();
            r.setId(rs.getInt("ID"));
            r.setNome(rs.getString("NOME"));
            r.setMovimentos(rs.getString("MOVIMENTOS"));
            r.setDescricao(rs.getString("DESCRICAO"));
            r.setArquivo(rs.getString("IMG_REPRES"));
            r.setArquivoSinais(rs.getString("IMG_SINAL"));
            registros.add(r);            
          }
          return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Representacao buscaPorId(int id)
    {        
        try {   
                Representacao r = new Representacao();
                String sql = "SELECT * FROM " + this.tabela + " WHERE ID = "+ id;                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {                  
                    r.setId(rs.getInt("ID"));
                    r.setNome(rs.getString("NOME"));
                    r.setMovimentos(rs.getString("MOVIMENTOS"));
                    r.setDescricao(rs.getString("DESCRICAO"));
                    r.setArquivo(rs.getString("IMG_REPRES"));
                    r.setArquivoSinais(rs.getString("IMG_SINAL"));                          
                }
                return (r);
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public ArrayList<Representacao> buscaTodos()
    {
        ArrayList<Representacao> registros = new ArrayList<Representacao>();
        try {            
                String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC";                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Representacao r = new Representacao();
                  r.setId(rs.getInt("ID"));
                  r.setNome(rs.getString("NOME"));
                  r.setMovimentos(rs.getString("MOVIMENTOS"));
                  r.setDescricao(rs.getString("DESCRICAO"));
                  r.setArquivo(rs.getString("IMG_REPRES"));
                  r.setArquivoSinais(rs.getString("IMG_SINAL"));                
                  registros.add(r);            
                }
                return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Representacao buscaPrimeiro()
    {
        try {
            //String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC LIMIT 1";
            String sql = "SELECT * FROM "+ this.tabela + " ORDER BY NOME LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Representacao r = new Representacao();
                r.setId(rs.getInt("ID"));
                r.setNome(rs.getString("NOME"));
                r.setMovimentos(rs.getString("MOVIMENTOS"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setArquivo(rs.getString("IMG_REPRES"));
                r.setArquivoSinais(rs.getString("IMG_SINAL"));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
            
    public Representacao buscaUltimo()
    {
        try {
            //String sql = "SELECT * FROM "+ this.tabela + " WHERE ID = (SELECT MAX(ID)  FROM "+ this.tabela + ")";
            String sql = "SELECT * FROM "+ this.tabela + " ORDER BY NOME DESC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Representacao r = new Representacao();
                r.setId(rs.getInt("ID"));
                r.setNome(rs.getString("NOME"));
                r.setMovimentos(rs.getString("MOVIMENTOS"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setArquivo(rs.getString("IMG_REPRES"));
                r.setArquivoSinais(rs.getString("IMG_SINAL"));            
                return (r);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Representacao buscaAnterior(Representacao atual)
    {        
        try {
            //String sql = "SELECT * FROM "+ this.tabela + " WHERE ID < "+ atual.getId() + " ORDER BY ID DESC LIMIT 1";
            String sql = "SELECT * FROM "+ this.tabela + " WHERE NOME < '"+ atual.getNome() + "' ORDER BY NOME DESC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Representacao r = new Representacao();
                r.setId(rs.getInt("ID"));
                r.setNome(rs.getString("NOME"));
                r.setMovimentos(rs.getString("MOVIMENTOS"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setArquivo(rs.getString("IMG_REPRES"));
                r.setArquivoSinais(rs.getString("IMG_SINAL"));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Representacao buscaProximo(Representacao atual)
    {
        try {
            //String sql = "SELECT * FROM "+ this.tabela + " WHERE ID > "+ atual.getId() + " LIMIT 1";
            String sql = "SELECT * FROM "+ this.tabela + " WHERE NOME > '"+ atual.getNome() + "' ORDER BY NOME ASC LIMIT 1";
            
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Representacao r = new Representacao();
                r.setId(rs.getInt("ID"));
                r.setNome(rs.getString("NOME"));
                r.setMovimentos(rs.getString("MOVIMENTOS"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setArquivo(rs.getString("IMG_REPRES"));
                r.setArquivoSinais(rs.getString("IMG_SINAL"));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Representacao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    
    
    public boolean exclui()
    {
        String sql = "DELETE FROM "+ this.tabela + " WHERE ID = "+ this.getId();
        return (DQGG.bd.exclui(sql));
    }
}
