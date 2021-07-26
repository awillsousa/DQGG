
package dqgg;

import java.sql.*;
import java.text.Collator;
import java.text.Normalizer;
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
 * Classe de Sinal
 * @author Will
 */
public class Sinal implements Comparable{    
    private final String tabela = "REPRESENTACAO";  //nome da tabela no banco de dados 
    private int    id;
    private String nome;
    private String movimentos;
    private String descricao;
    private String arquivo;
    private String arquivoSinais;
    private Comparator<Sinal> comparador;       // utilizado para ordenacao
    private String nomeNormalizado;             // esse campo é utilizado para realizar ordenacao da mesma forma que o sistema antigo
        
    /**
     * 
     * @param id
     * @param nome
     * @param movimentos
     * @param descricao
     * @param arquivo
     * @param arquivoSinais 
     */
    public Sinal(int id, String nome, String movimentos, String descricao, String arquivo, String arquivoSinais){        
        this.id            = id;
        this.nome          = nome;
        this.movimentos    = movimentos;
        this.descricao     = descricao;
        this.arquivo       = arquivo;
        this.arquivoSinais = arquivoSinais; 
                
    }
    
    /**
     * 
     * Construtor vazio. 
     * Utilizado para algumas situações onde precisa recuperar dados sem criar um objeto definitivo. 
     */
    public Sinal()
    {    
        
    }
    
    /**
     * 
     * @return 
     */
    public int getId()
    {
        return (this.id);
    }
    
    /**
     * 
     * @return 
     */
    public String getNome()
    {
        return (this.nome);
    }
    
    
    /**
     * 
     * @return 
     */
    public String getNomeNormalizado()
    {
        return (this.nomeNormalizado);
    }
    
    /**
     * 
     * @return 
     */
    public String getMovimentos()
    {
        return (this.movimentos);
    }
    
    /**
     * 
     * @return 
     */
    public String getDescricao()
    {
        return (this.descricao);
    }
    
    /**
     * 
     * @return 
     */
    public String getArquivo()
    {
        return (this.arquivo);
    }
    
    /**
     * 
     * @return 
     */
    public String getArquivoSinais()
    {
        return (this.arquivoSinais);
    }
    
    public String getTabela()
    {
        return (this.tabela);
    }
    
    /**
     * 
     * @param id 
     */
    public void setId(int id)
    {
        this.id = id;
    }
    
    /**
     * 
     * @param nome 
     */
    public void setNome(String nome)
    {
        this.nome = nome;
    }
    
    /**
     * 
     * @param movimentos 
     */
    public void setMovimentos(String movimentos)
    {
        this.movimentos = movimentos;
    }
    
    /**
     * 
     * @param descricao 
     */
    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }
    
    /**
     * 
     * @param arquivo 
     */
    public void setArquivo(String arquivo)
    {
        this.arquivo = arquivo;
    }
    
    /**
     * 
     * @param arquivo 
     */
    public void setArquivoSinais(String arquivo)
    {
        this.arquivoSinais = arquivo;
    }
    
    /**     * 
     * @param Nenhum
     * @return Resultado da operação de inserção  
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
     * Update na base de dados dos campos passados
     * @param HashMap contendo os campos a serem alterados com os respectivos valores
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
    
    /**
     * Troca uma abreviatura da lista de movimentos do Sinal
     * @param abrevAnt  - abreviação anterior
     * @param abrevNova - nova abreviação
     * @return booleano Resultado da operação de inserção  
     */
    public boolean trocaAbrevBD(String abrevAnt, String abrevNova)
    {
        String sql = "UPDATE " + this.tabela + " SET ";        
        String valores = "MOVIMENTOS = replace(MOVIMENTOS,'"+ abrevAnt +" ','"+abrevNova+" ')";      
        sql += valores + " WHERE MOVIMENTOS LIKE '% " + abrevAnt + " %' OR MOVIMENTOS LIKE '" + abrevAnt + " %'";       
        return (DQGG.bd.atualiza(sql));
    }
    
    /**
     * Realiza uma consulta com os campos passados 
     * @param campos
     * @return ArrayList<Sinal>
     */
    public ArrayList<Sinal> consultaBD(HashMap campos)
    {
        ArrayList<Sinal> registros = new ArrayList<Sinal>();
        try {
            String valores = "";
            String colunaOrdenacao = "";
            //String sql = "SELECT * FROM " + this.tabela + " WHERE ";            
            
          // Pega o conjuntos da entrada do hash
          Set set = campos.entrySet();
          // Pega um iterator
          Iterator i = set.iterator();
          // Monta lista de colunas e valores
          while(i.hasNext()) {
             Map.Entry me = (Map.Entry)i.next();         
             colunaOrdenacao += "instr(" + me.getKey().toString().toLowerCase() + ", '" + me.getValue().toString().toLowerCase() +"'), ";
             //valores += me.getKey().toString() + " LIKE '%"+ me.getValue().toString() + "%' OR " + me.getKey().toString() + " LIKE '"+ me.getValue().toString() + "%'";
             valores += me.getKey().toString() + " LIKE '%"+ me.getValue().toString() + "%'"; // OR " + me.getKey().toString() + " LIKE '"+ me.getValue().toString() + "%'";
             if (i.hasNext())
                 valores += " AND ";         
          }
          
          String sql = "SELECT "+ colunaOrdenacao + "* FROM " + this.tabela + " WHERE ";
          sql += valores;       
          sql +=  " ORDER BY 1, NOME ASC";        
          
          ResultSet rs = DQGG.bd.consulta(sql);          
          while (rs.next())
          {
            Sinal r = new Sinal();
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
            Logger.getLogger(Sinal.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    /**
     * Busca o sinal pelo id passado
     * @param id
     * @return Sinal
     */
    public Sinal buscaPorId(int id)
    {        
        try {   
                Sinal r = new Sinal();
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
            Logger.getLogger(Sinal.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    /**
     * Busca os sinais que contêm os aloquiros passados.
     * @param aloquiros
     * @return ArrayList<Sinal>
     */   
    public ArrayList<Sinal> buscaPorAloquiros(ArrayList<Aloquiro> aloquiros)
    {
        ArrayList<Sinal> registros = new ArrayList<Sinal>();
        try {
            String valores = "";                        
            String sql = "SELECT UPPER(NOME) AS STR,* FROM " + this.tabela + " WHERE ";
            Iterator i = aloquiros.listIterator();
            while (i.hasNext())
            {
                Aloquiro a = (Aloquiro) i.next();                
                valores += "(MOVIMENTOS LIKE '"+ a.getAbreviatura() + " %' OR ";
                valores += "MOVIMENTOS LIKE '% "+ a.getAbreviatura() + " %' OR ";
                valores += "MOVIMENTOS LIKE '% "+ a.getAbreviatura() + "')";
                if (i.hasNext())
                   valores += " AND "; 
            }

          sql += valores;       
          sql +=  " ORDER BY STR ASC";        
          
          ResultSet rs = DQGG.bd.consulta(sql);          
          while (rs.next())
          {
            Sinal r = new Sinal();
            r.setId(rs.getInt("ID"));
            r.setNome(rs.getString("NOME"));
            r.nomeNormalizado = AsciiUtils.convertNonAscii(r.getNome());
            r.setMovimentos(rs.getString("MOVIMENTOS"));
            r.setDescricao(rs.getString("DESCRICAO"));
            r.setArquivo(rs.getString("IMG_REPRES"));
            r.setArquivoSinais(rs.getString("IMG_SINAL"));
            registros.add(r);            
          }
          /* Ordena os registros 
            * Essa ordenação foi necessária para que se utilizasse a mesma ordenação das tabelas 
            * utilizadas na interface. 
            */                
            Collections.sort(registros, new Comparator<Sinal>() {
                                                        @Override
                                                        public int compare(Sinal s1, Sinal s2) { 
                                                            Collator collator = Collator.getInstance(new Locale("pt", "BR"));
                                                            collator.setStrength(Collator.PRIMARY);

                                                            String[] as1 = s1.getNomeNormalizado().split(" ");
                                                            String[] as2 = s2.getNomeNormalizado().split(" ");
                                                            return collator.compare(as1[0], as2[0]); 
                                                        }
            });

            return (registros);          
        } catch (SQLException ex) {
            Logger.getLogger(Sinal.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    /**
     * Busca todos os sinais da base de dados
     * @return ArrayList<Sinal>
     */
    public ArrayList<Sinal> buscaTodos()
    {
        ArrayList<Sinal> registros = new ArrayList<Sinal>();
        try {            
                String sql = "SELECT UPPER(NOME) AS STR,* FROM " + this.tabela + " ORDER BY STR ASC";                                    
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Sinal r = new Sinal();
                  r.setId(rs.getInt("ID"));
                  r.setNome(rs.getString("NOME"));
                  r.nomeNormalizado = AsciiUtils.convertNonAscii(r.getNome());
                  r.setMovimentos(rs.getString("MOVIMENTOS"));
                  r.setDescricao(rs.getString("DESCRICAO"));
                  r.setArquivo(rs.getString("IMG_REPRES"));
                  r.setArquivoSinais(rs.getString("IMG_SINAL"));                
                  registros.add(r);            
                }               
                
                /* Ordena os registros 
                 * Essa ordenação foi necessária para que se utilizasse a mesma ordenação das tabelas 
                 * utilizadas na interface. 
                 */                
                Collections.sort(registros, new Comparator<Sinal>() {
                                                            @Override
                                                            public int compare(Sinal s1, Sinal s2) { 
                                                                Collator collator = Collator.getInstance(new Locale("pt", "BR"));
                                                                collator.setStrength(Collator.PRIMARY);
                                                                
                                                                String[] as1 = s1.getNomeNormalizado().split(" ");
                                                                String[] as2 = s2.getNomeNormalizado().split(" ");
                                                                return collator.compare(as1[0], as2[0]); 
                                                            }
                });
               
                return (registros);
                
        } catch (SQLException ex) {
            Logger.getLogger(Sinal.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    
    /**
     * Busca apenas o primeiro sinal, no conjunto ordenado de todos os sinais.
     * @return Sinal
     */
    public Sinal buscaPrimeiro()
    {        
        ArrayList<Sinal> lista = buscaTodos();
        return (lista.get(0));
    }
    
    /**
     * Busca apenas o último sinal, no conjunto ordenado de todos os sinais.
     * @return Sinal
     */        
    public Sinal buscaUltimo()
    {
        ArrayList<Sinal> lista = buscaTodos();
        return (lista.get(lista.size()-1));        
    }
    
    /**
     * Busca o sinal anterior ao sinal passado
     * @param atual
     * @return Sinal
     */
    public Sinal buscaAnterior(Sinal atual)
    { 
        ArrayList<Sinal> lista = buscaTodos();        
        int indxAtual = -1;
        
        for (Sinal s : lista) {
            if (s.getId() == atual.getId())
                indxAtual = lista.indexOf(s);                
        }
        
        if (indxAtual < 0)
            return (null);
        else {
            if (indxAtual == 0)
               return (lista.get(indxAtual));
            else
                return (lista.get(indxAtual-1));
        }        
    }
    
    
    
    /**
     * Busca o próximo sinal em relação ao sinal passado
     * @param atual
     * @return Sinal
     */
    public Sinal buscaProximo(Sinal atual)
    {
        ArrayList<Sinal> lista = buscaTodos();        
        int indxAtual = -1;
        
        for (Sinal s : lista) {
            if (s.getId() == atual.getId())
                indxAtual = lista.indexOf(s);                
        }
        
        if (indxAtual < 0)
            return (null);
        else {
            if (indxAtual == lista.size())
               return (lista.get(indxAtual));
            else
                return (lista.get(indxAtual+1));
        }        
    }
    
    /**
     * Busca através do nome do Sinal 
     * @param s - Um texto que se deseja buscar a partir do nome do Sinal
     * @return Lista de Sinais que o nome contenham parte da string s
     */
    public ArrayList<Sinal> buscaAlfabetica(String s)
    {
        ArrayList<Sinal> registros = new ArrayList<Sinal>();
        try {              
            String sql = "SELECT UPPER(NOME) AS STR,*, 0 AS ID_QUERY FROM " + this.tabela + " WHERE NOME LIKE '" + s + "%'"
                           + " UNION "
                           + "SELECT UPPER(NOME) AS STR,*, 1 AS ID_QUERY FROM " + this.tabela + " WHERE NOME LIKE '_%" + s + "%'"
                           + " ORDER BY ID_QUERY,STR ASC";  
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {
                  Sinal r = new Sinal();
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
            Logger.getLogger(Sinal.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
        
    /**
     * Busca através do nome do Sinal e retorna apenas a primeira ocorrência 
     * @param s - Um texto que se deseja buscar a partir do nome do Sinal
     * @return Lista de Sinais que o nome contenham parte da string s
     */
    public Sinal buscaAlfabeticaOcorr1(String s)
    {
        Sinal r = new Sinal();
        
        try {              
            String sql = "SELECT UPPER(NOME) AS STR,*, 0 AS ID_QUERY FROM " + this.tabela + " WHERE NOME LIKE '" + s + "%'"
                           + " UNION "
                           + "SELECT UPPER(NOME) AS STR,*, 1 AS ID_QUERY FROM " + this.tabela + " WHERE NOME LIKE '_%" + s + "%'"
                           + " ORDER BY ID_QUERY,STR ASC";  
                ResultSet rs = DQGG.bd.consulta(sql);          
                while (rs.next())
                {                  
                  r.setId(rs.getInt("ID"));
                  r.setNome(rs.getString("NOME"));
                  r.setMovimentos(rs.getString("MOVIMENTOS"));
                  r.setDescricao(rs.getString("DESCRICAO"));
                  r.setArquivo(rs.getString("IMG_REPRES"));
                  r.setArquivoSinais(rs.getString("IMG_SINAL"));                
                  
                  break;    // pega apenas o primeiro
                }
                return (r);
        } catch (SQLException ex) {
            Logger.getLogger(Sinal.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return (null);
    }
    
    /**
     * Exclui o sinal passado
     * @return boolean
     */
    public boolean exclui()
    {
        String sql = "DELETE FROM "+ this.tabela + " WHERE ID = "+ this.getId();
        return (DQGG.bd.exclui(sql));
    }

    @Override
    public int compareTo(Object o) {
        Sinal s = (Sinal) o;
        String nomeOutro = s.getNome();
        
        return (this.nome.compareToIgnoreCase(nomeOutro));        
    }
}
