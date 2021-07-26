
package dqgg;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe Categoria
 * @author Will
 */
public class Categoria {    
    private final String tabela = "CATEGORIA";
    private int    id;    
    private String nome;
   
    public Categoria(int id, String nome){        
        this.id            = id;        
        this.nome     = nome;           
    }
    
    public Categoria()
    {        
    }
    
    public int getId()
    {
        return (this.id);
    }    
        
    public String getDescricao()
    {
        return (this.nome);
    }
        
    public void setId(int id)
    {
        this.id = id;
    }
           
    public void setDescricao(String nome)
    {
        this.nome = nome;
    }
                
    public boolean insereBD()
    {   
        String sql = "INSERT INTO " + this.tabela + "(NOME) VALUES (" +                     
                     "\"" + this.nome +  "\")";
        
        return (DQGG.bd.insere(sql));        
    }
    
    /**
     * Atualiza a base de dados
     * @param campos
     * @return boolean
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
    
    
    public ArrayList<Categoria> consultaBD(HashMap campos)
    {
        ArrayList<Categoria> registros = new ArrayList<Categoria>();
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
            Categoria r = new Categoria();
            r.setId(rs.getInt("ID"));            
            r.setDescricao(rs.getString("NOME"));            
            registros.add(r);            
          }
          return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Categoria buscaPorId(int id)
    {        
        try {   
                Categoria r = new Categoria();
                String sql = "SELECT * FROM " + this.tabela + " WHERE ID = "+ id;                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {                  
                  r.setId(rs.getInt("ID"));            
                  r.setDescricao(rs.getString("NOME"));                             
                }
                return (r);
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public ArrayList<Categoria> buscaTodos()
    {
        ArrayList<Categoria> registros = new ArrayList<Categoria>();
        try {            
                String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC";                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Categoria r = new Categoria();
                  r.setId(rs.getInt("ID"));            
                  r.setDescricao(rs.getString("NOME"));            
                  registros.add(r);            
                }
                return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Categoria buscaPrimeiro()
    {
        try {
            String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Categoria r = new Categoria();
                r.setId(rs.getInt("ID"));                
                r.setDescricao(rs.getString("NOME"));                
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
            
    public Categoria buscaUltimo()
    {
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID = (SELECT MAX(ID)  FROM "+ this.tabela + ")";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Categoria r = new Categoria();
                r.setId(rs.getInt("ID"));                
                r.setDescricao(rs.getString("NOME"));                        
                return (r);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Categoria buscaAnterior(Categoria atual)
    {        
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID < "+ atual.getId() + " ORDER BY ID DESC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Categoria r = new Categoria();
                r.setId(rs.getInt("ID"));                
                r.setDescricao(rs.getString("NOME"));                
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Categoria buscaProximo(Categoria atual)
    {
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID > "+ atual.getId() + " LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Categoria r = new Categoria();
                r.setId(rs.getInt("ID"));                
                r.setDescricao(rs.getString("NOME"));                
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public boolean exclui()
    {
        String sql = "DELETE FROM "+ this.tabela + " WHERE ID = "+ this.getId();
        return (DQGG.bd.exclui(sql));
    }
}
