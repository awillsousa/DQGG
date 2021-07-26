
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
 * Classe de Quirema
 * @author Will
 *  
 */
public class Quirema {    
    private final String tabela = "SEMATOSEMA";
    private int    id;
    private String descricao;
    private String abreviatura;    
    private Categoria categoria;
    private String posColuna;
    private String posLinha;
    
    public Quirema(int id, String descricao, String abreviatura, int idCategoria, String posLinha, String posColuna){        
        this.id            = id;
        this.descricao     = descricao;
        this.abreviatura   = abreviatura;
        categoria = new Categoria().buscaPorId(idCategoria);        
        this.posLinha  = posLinha;
        this.posColuna = posColuna;
    }
    
    public Quirema()
    {
        this.categoria = new Categoria();
    }
    
    public int getId()
    {
        return (this.id);
    }    
    
    public String getDescricao()
    {
        return (this.descricao);
    }
    
    public String getAbreviatura()
    {
        return (this.abreviatura);
    }
    
    public Categoria getCategoria()
    {
        return (this.categoria);
    }
    
    public String getLinha()
    {
        return (this.posLinha);
    }
    
    public String getColuna()
    {        
        return (this.posColuna);
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
           
    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }
    
    public void setAbreviatura(String abreviatura)
    {
        this.abreviatura = abreviatura;
    }
    
    public void setCategoria(Categoria categoria)
    {
        this.categoria = categoria;
    }
        
    public void setLinha(String linha)
    {
        this.posLinha = linha;
    }
    
    public void setColuna(String coluna)
    {
        this.posColuna = coluna;
    }
    
    public boolean insereBD()
    {   
        String sql = "INSERT INTO " + this.tabela + "(NOME,ABREVIATURA,ID_CATEGORIA) VALUES (" +                    
                     "\"" + this.descricao +  "\"," +
                     "\"" + this.abreviatura +  "\"," +
                     "\"" + this.categoria.getId() + "\")";
        
        return (DQGG.bd.insere(sql));        
    }
    
    /**
     *
     * @param campos
     * @return
     */
    public boolean atualizaBD(HashMap campos)
    {    
        boolean semFalha = true;  // indica se ocorreu falha em alguma operação do banco de dados requerida
        String sql = "UPDATE " + this.tabela + " SET ";        
        String valores = "";
        
      // Pega o conjunto da entrada do hash
      Set set = campos.entrySet();
      // Pega um iterator
      Iterator i = set.iterator();
      // Monta lista de colunas e valores
      while(i.hasNext()) {
         Map.Entry me = (Map.Entry)i.next();         
         valores += me.getKey().toString() + " = '"+ me.getValue().toString() + "'";
                  
         if (i.hasNext())
             valores += ",";   
         
         if (me.getKey().equals("ABREVIATURA"))
         {             
             String abrevAnt  = this.getAbreviatura();
             String abrevNova = me.getValue().toString();
             
             Aloquiro a  = new Aloquiro();
             semFalha = a.trocaAbrevBD(abrevAnt, abrevNova);
         }         
      }
      
      sql += valores + " WHERE ID = " + this.getId(); 
      
      if (semFalha)
            return (DQGG.bd.atualiza(sql));
        else          
            return (false);
    }
    
    public ArrayList<Quirema> consultaBD(HashMap campos)
    {
        ArrayList<Quirema> registros = new ArrayList<Quirema>();
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
            Quirema r = new Quirema();
            r.setId(rs.getInt("ID"));            
            r.setDescricao(rs.getString("NOME"));
            r.setAbreviatura(rs.getString("ABREVIATURA"));
            r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));
            r.setLinha(rs.getString("POSLINHA"));
            r.setColuna((rs.getString("POSCOLUNA")));
            registros.add(r);            
          }
          return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Quirema buscaPorId(int id)
    {        
        try {   
                Quirema r = new Quirema();
                String sql = "SELECT * FROM " + this.tabela + " WHERE ID = "+ id;                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {                  
                    r.setId(rs.getInt("ID"));                
                    r.setDescricao(rs.getString("NOME"));
                    r.setAbreviatura(rs.getString("ABREVIATURA"));
                    r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));                          
                    r.setLinha(rs.getString("POSLINHA"));
                    r.setColuna((rs.getString("POSCOLUNA")));
                }
                return (r);
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Quirema buscaPorAbrev(String abrev)
    {        
        try {   
                Quirema r = new Quirema();
                String sql = "SELECT * FROM " + this.tabela + " WHERE ABREVIATURA = '"+ abrev +"'";                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {                  
                    r.setId(rs.getInt("ID"));                
                    r.setDescricao(rs.getString("NOME"));
                    r.setAbreviatura(rs.getString("ABREVIATURA"));
                    r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));    
                    r.setLinha(rs.getString("POSLINHA"));
                    r.setColuna((rs.getString("POSCOLUNA")));
                }
                return (r);
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public ArrayList<Quirema> buscaPorCategoria(Categoria q)
    {   
        ArrayList<Quirema> registros = new ArrayList<Quirema>();
        try {   
                String sql = "SELECT * FROM " + this.tabela + " WHERE ID_CATEGORIA = "+ q.getId() + " ORDER BY NOME DESC";                    
                //String sql = "SELECT instr(NOME, 'ESQUERD'), * FROM " + this.tabela + " WHERE ID_CATEGORIA = "+ q.getId() + " ORDER BY 1 DESC";                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Quirema r = new Quirema();
                  r.setId(rs.getInt("ID"));                
                  r.setDescricao(rs.getString("NOME"));
                  r.setAbreviatura(rs.getString("ABREVIATURA"));
                  r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));  
                  r.setLinha(rs.getString("POSLINHA"));
                  r.setColuna((rs.getString("POSCOLUNA")));
                  registros.add(r);            
                }
                return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public ArrayList<Quirema> buscaTodos()
    {
        ArrayList<Quirema> registros = new ArrayList<Quirema>();
        try {            
                String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC";                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Quirema r = new Quirema();
                  r.setId(rs.getInt("ID"));                
                  r.setDescricao(rs.getString("NOME"));
                  r.setAbreviatura(rs.getString("ABREVIATURA"));
                  r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA")))); 
                  r.setLinha(rs.getString("POSLINHA"));
                  r.setColuna((rs.getString("POSCOLUNA")));
                  registros.add(r);            
                }
                return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Quirema buscaPrimeiro()
    {
        try {
            String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Quirema r = new Quirema();
                r.setId(rs.getInt("ID"));                
                r.setDescricao(rs.getString("NOME"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));
                r.setLinha(rs.getString("POSLINHA"));
                r.setColuna((rs.getString("POSCOLUNA")));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
            
    public Quirema buscaUltimo()
    {
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID = (SELECT MAX(ID)  FROM "+ this.tabela + ")";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Quirema r = new Quirema();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("NOME"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));  
                r.setLinha(rs.getString("POSLINHA"));
                r.setColuna((rs.getString("POSCOLUNA")));
                return (r);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Quirema buscaAnterior(Quirema atual)
    {        
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID < "+ atual.getId() + " ORDER BY ID DESC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Quirema r = new Quirema();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("NOME"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));  
                r.setLinha(rs.getString("POSLINHA"));
                r.setColuna((rs.getString("POSCOLUNA")));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Quirema buscaProximo(Quirema atual)
    {
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID > "+ atual.getId() + " LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Quirema r = new Quirema();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("NOME"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setCategoria(this.categoria.buscaPorId(Integer.valueOf(rs.getString("ID_CATEGORIA"))));  
                r.setLinha(rs.getString("POSLINHA"));
                r.setColuna((rs.getString("POSCOLUNA")));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Quirema.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    
    
    public boolean exclui()
    {
        String sql = "DELETE FROM "+ this.tabela + " WHERE ID = "+ this.getId();
        return (DQGG.bd.exclui(sql));
    }
    
    public void insertInicial()
    {
        ArrayList<String> sqls = new ArrayList<String>();
        
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Articulação do Dedo Direito', 'ADD', 2)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Articulação do Dedo Esquerdo', 'ADE', 2)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Articulação da Mão Direita', 'AMD', 1)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Articulação da Mão Esquerda', 'AME', 1)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Cabeça/Pescoço', 'CP', 3)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Expressão Facial','EMF', 5)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Frequência/Intensidade','FI', 4)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Movimento do Corpo','MC', 4)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Movimento do Dedo Direito', 'MDD', 2)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Movimento do Dedo Esquerdo','MDE', 2)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Movimento de Mão Direita','MMD', 4)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Movimento de Mão Esquerda','MME', 4)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Orientação da Mão Direita','OMD', 1)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Orientação da Mão Esquerda','OME', 1)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Orientação da Palma Direita','OPD', 1)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Orientação da Palma Esquerda','OPE', 1)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Posição dos Braços','PB', 4)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Quantidade de Dedos Direito','QDD', 2)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Quantidade de Dedos Esquerdo','QDE', 2)");
        sqls.add("INSERT INTO QUIREMA(NOME, ABREVIATURA, ID_CATEGORIA) VALUES ('Relacionado à Face','RF', 3)");
        
        Iterator<String> i = sqls.iterator();
        
        while (i.hasNext())
        {
            String s = i.next();
            DQGG.bd.insere(s);            
        }
        
    }
}
