
package dqgg;

import java.sql.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe Aloquiro
 * @author Will
 */
public class Aloquiro {    
    private final String tabela = "ALOQUIRO";   // nome da tabela na base de dados
    private int    id;
    private String descricao;
    private String abreviatura;
    private String imagem;
    private Quirema quirema;
    
    public Aloquiro(int id, String descricao, String abreviatura, String imagem, int idQuirema){        
        this.id            = id;
        this.descricao     = descricao;
        this.abreviatura   = abreviatura;
        this.imagem        = imagem; 
        
        this.quirema = new Quirema().buscaPorId(idQuirema);
    }
    
    public Aloquiro()
    {     
        this.quirema = new Quirema();
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
    
    public String getImagem()
    {
        return (this.imagem);
    }
    
    public Quirema getQuirema()
    {
        return (this.quirema);
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
    
    public void setImagem(String imagem)
    {
        this.imagem = imagem;
    }
        
    public void setQuirema(Quirema q)
    {
        this.quirema = q;
    }
    
    /**
     * Insere o Aloquiro na base de dados
     * @param campos
     * @return boolean
     */
    public boolean insereBD()
    {   
        String sql = "INSERT INTO " + this.tabela + "(DESCRICAO,ABREVIATURA,IMG_ALOQUIRO,ID_QUIREMA) VALUES (" + 
                     "\"" + this.descricao + "\"," +
                     "\"" + this.abreviatura +  "\"," +
                     "\"" + this.imagem +  "\"," +
                     "\"" + this.quirema.getId() + "\")";
        
        return (DQGG.bd.insere(sql));        
    }
    
    /**
     * Atualiza base de dados com os campos passados
     * @param campos
     * @return boolean
     */
    public boolean atualizaBD(HashMap campos)
    {
        boolean semFalha = true;  // indica se ocorreu falha em alguma operação do banco de dados requerida
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
         
         if (me.getKey().equals("ABREVIATURA"))
         {             
             String abrevAnt = this.getAbreviatura();
             String abrevNova = me.getValue().toString();
             
             Sinal s  = new Sinal();
             semFalha = s.trocaAbrevBD(abrevAnt, abrevNova);
         }         
      }
      
      sql += valores + " WHERE ID = " + this.getId();       
      //return (DQGG.bd.atualiza(sql));
      if (semFalha)
        return (DQGG.bd.atualiza(sql));
      else          
        return (false);
    }
    
    /**
     * Troca a abreviatura do Aloquiro que se refere ao seu Sematosema (Quirema)
     * @param abrevAnt  - abreviação anterior
     * @param abrevNova - nova abreviação
     * @return booleano Resultado da operação de inserção  
     */
    public boolean trocaAbrevBD(String abrevAnt, String abrevNova)
    {
        boolean semFalha = true;
        
        // Troca as abreviaturas nos Aloquiros
        String sql = "UPDATE " + this.tabela + " SET ";        
        String valores = "ABREVIATURA = replace(ABREVIATURA,'"+ abrevAnt +"','"+abrevNova+"')";      
        sql += valores + " WHERE ABREVIATURA LIKE '" + abrevAnt + "%'";                   
        semFalha = DQGG.bd.atualiza(sql);
        
        // Trocas as abreviaturas nos Sinais
        Sinal s = new Sinal();
        sql = "UPDATE " + s.getTabela() + " SET ";        
        valores = "MOVIMENTOS = replace(MOVIMENTOS,'"+ abrevAnt +"','"+abrevNova+"')";      
        sql += valores + " WHERE MOVIMENTOS LIKE '%" + abrevAnt + "%'";       
        
        if (semFalha)
            return (DQGG.bd.atualiza(sql));
        else          
            return (false);
    }
    
    
    public ArrayList<Aloquiro> consultaBD(HashMap campos)
    {
        ArrayList<Aloquiro> registros = new ArrayList<Aloquiro>();
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
             valores += me.getKey().toString() + " LIKE '"+ me.getValue().toString() + "%'";
             if (i.hasNext())
                 valores += " AND ";         
          }
          
          sql += valores;       
          sql +=  " ORDER BY ROWID ASC";        
          
          ResultSet rs = DQGG.bd.consulta(sql);          
          while (rs.next())
          {
            Aloquiro r = new Aloquiro();
            r.setId(rs.getInt("ID"));
            r.setDescricao(rs.getString("DESCRICAO"));
            r.setAbreviatura(rs.getString("ABREVIATURA"));
            r.setImagem(rs.getString("IMG_ALOQUIRO"));
            r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));
            
            registros.add(r);            
          }
          return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
   
    public Aloquiro buscaPorId(int id)
    {        
        try {   
                Aloquiro r = new Aloquiro();
                String sql = "SELECT * FROM " + this.tabela + " WHERE ID = "+ id;                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {                  
                    r.setId(rs.getInt("ID"));
                    r.setDescricao(rs.getString("DESCRICAO"));
                    r.setAbreviatura(rs.getString("ABREVIATURA"));
                    r.setImagem(rs.getString("IMG_ALOQUIRO"));
                    r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));                          
                }
                return (r);
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public ArrayList<Aloquiro> buscaPorQuirema(Quirema q)
    {   
        ArrayList<Aloquiro> registros = new ArrayList<Aloquiro>();
        try {   
                String sql = "SELECT * FROM " + this.tabela + " WHERE ID_QUIREMA = "+ q.getId();                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Aloquiro r = new Aloquiro();
                  r.setId(rs.getInt("ID"));
                  r.setDescricao(rs.getString("DESCRICAO"));
                  r.setAbreviatura(rs.getString("ABREVIATURA"));
                  r.setImagem(rs.getString("IMG_ALOQUIRO"));
                  r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));         
                  registros.add(r);            
                }
                return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public ArrayList<Aloquiro> buscaTodos()
    {
        ArrayList<Aloquiro> registros = new ArrayList<Aloquiro>();
        try {            
                String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC";                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Aloquiro r = new Aloquiro();
                  r.setId(rs.getInt("ID"));
                  r.setDescricao(rs.getString("DESCRICAO"));
                  r.setAbreviatura(rs.getString("ABREVIATURA"));
                  r.setImagem(rs.getString("IMG_ALOQUIRO"));
                  r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));         
                  registros.add(r);            
                }                
                return (registros);
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    public Aloquiro buscaPrimeiro()
    {
        try {
            String sql = "SELECT * FROM " + this.tabela + " ORDER BY ROWID ASC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Aloquiro r = new Aloquiro();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setImagem(rs.getString("IMG_ALOQUIRO"));
                r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
            
    public Aloquiro buscaUltimo()
    {
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID = (SELECT MAX(ID)  FROM "+ this.tabela + ")";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Aloquiro r = new Aloquiro();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setImagem(rs.getString("IMG_ALOQUIRO"));
                r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));         
                return (r);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Aloquiro buscaAnterior(Aloquiro atual)
    {        
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID < "+ atual.getId() + " ORDER BY ID DESC LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Aloquiro r = new Aloquiro();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setImagem(rs.getString("IMG_ALOQUIRO"));
                r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    public Aloquiro buscaProximo(Aloquiro atual)
    {
        try {
            String sql = "SELECT * FROM "+ this.tabela + " WHERE ID > "+ atual.getId() + " LIMIT 1";
            ResultSet rs = DQGG.bd.consulta(sql);
            
            if (rs.next())
            {
                Aloquiro r = new Aloquiro();
                r.setId(rs.getInt("ID"));
                r.setDescricao(rs.getString("DESCRICAO"));
                r.setAbreviatura(rs.getString("ABREVIATURA"));
                r.setImagem(rs.getString("IMG_ALOQUIRO"));
                r.setQuirema(this.quirema.buscaPorId(Integer.valueOf(rs.getString("ID_QUIREMA"))));
                return (r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Aloquiro.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (null);
    }
    
    
    
    public boolean exclui()
    {
        String sql = "DELETE FROM "+ this.tabela + " WHERE ID = "+ this.getId();
        return (DQGG.bd.exclui(sql));
    }
}
