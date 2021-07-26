package dqgg;

import java.io.File;
import java.io.BufferedReader;  
import java.io.FileNotFoundException;  
import java.io.FileReader;  
import java.io.IOException; 
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Realiza as operações diretas na base de dados
 * @author AntonioSousa
 */
public class BD {
    Connection conexao = null;
    Statement  stmt = null;
  
  /* Construtor - realiza a conexão ao banco */
  public BD(String arquivobd)
  {    
    try {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
        }
      conexao = DriverManager.getConnection("jdbc:sqlite::resource:dqgg" + arquivobd);      
    } catch ( java.sql.SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    } 
  }
  
  
  public void criaTabela(String sql)
  { 
    try { 
      stmt = conexao.createStatement();      
      stmt.executeUpdate(sql);
      stmt.close();
      //conexao.close();
    } catch ( java.sql.SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
    System.out.println("Tabela criada com sucesso!");
  }
  
  public boolean insere(String sql)
  {       
    try {  
      stmt = conexao.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
      return (true);     
    } catch ( java.sql.SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      return (false);      
    }      
  }
  
  public ResultSet consulta(String sql)
  {
    ResultSet rs = null;  
    try {    
      stmt = conexao.createStatement();
      rs = stmt.executeQuery( sql );      
    } catch ( java.sql.SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    } catch ( java.lang.Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }     
    return rs;
  }
  
  public boolean atualiza(String sql)
  {   
    int linhasAlteradas;
    
    try {    
      stmt = conexao.createStatement();
      linhasAlteradas = stmt.executeUpdate( sql );              
      stmt.close();
      /*if (linhasAlteradas == 0)
        return (false);
      else*/
        return (true);  
    } catch ( java.sql.SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );      
      return (false);
    }     
  }
  
  public boolean exclui(String sql)
  {  
    int linhasAlteradas;  
    try {    
      stmt = conexao.createStatement();
      linhasAlteradas = stmt.executeUpdate( sql );               
      stmt.close();
      if (linhasAlteradas == 0)
        return (false);
      else
        return (true);  
    } catch ( java.sql.SQLException e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      return (false);      
    }           
  }
  
  /* Inserção em lote */
  public void batchInsSinais(File arq)
  {
        //String sql = "INSERT INTO SINAL (NOME,MOVIMENTOS,IMG_REPRES,IMG_SINAL,DESCRICAO) VALUES (?, ?, ?, ?, ?)";
      String sql = "INSERT INTO REPRESENTACAO (NOME,MOVIMENTOS,IMG_REPRES,IMG_SINAL,DESCRICAO) VALUES (?, ?, ?, ?, ?)";
        String linha;
        String separador = "§";
        BufferedReader br = null; 
        final int batchSize = 50;
        PreparedStatement ps;
        int count = 0;      
      
        try { 
            ps = this.conexao.prepareStatement(sql);
            br = new BufferedReader(new FileReader(arq));  
            while ((linha = br.readLine()) != null) 
            {
                String[] valores = linha.split(separador);
                if (valores.length == 6)
                {   /*
                    System.out.println("Processando linha " + valores[0]); 
                    System.out.println(valores[1] + " $#$ " + valores[2] + " $#$ " + valores[3] + " $#$ "+ valores[4] + " $#$ " + valores[5]);
                    */                
                    ps.setString(1, valores[1]);
                    ps.setString(2, valores[2]);
                    ps.setString(3, "/img/representacao/"+ (count+1) +valores[4]);
                    ps.setString(4, "/img/sinais/"+(count+1) + valores[3]);
                    ps.setString(5, valores[5]);
                    ps.addBatch();                
                }
                else
                {
                    for (int i = valores.length-1; i >= 0; i--)
                        System.out.print(valores[i] + " || ");
                    System.out.println("");
                }
                if(++count % batchSize == 0) 
                   ps.executeBatch();  
            }          
              ps.executeBatch(); // insert remaining records
              ps.close();          
        } catch (FileNotFoundException e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
        } catch (IOException e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );          
        } catch ( java.sql.SQLException e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        } finally {  
            if (br != null) 
            {  
                try {  
                    br.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }         
  }
  
  /* Lista arquivo de Insercao em lote */
  public void listInsAloquiros(File arq)
  {
        String sql = "INSERT INTO ALOQUIRO (ABREVIATURA,DESCRICAO,IMG_ALOQUIRO,ID_QUIREMA) VALUES (?, ?, ?, ?)";
        String linhaSQL = "INSERT INTO ALOQUIRO (ABREVIATURA,DESCRICAO,IMG_ALOQUIRO,ID_QUIREMA) VALUES (";
        String linha;
        String separador = "@";
        BufferedReader br = null; 
        final int batchSize = 200;
        PreparedStatement ps;
        int count = 0;      
      
        try {             
            br = new BufferedReader(new FileReader(arq));  
            while ((linha = br.readLine()) != null) 
            {
                linhaSQL = "INSERT INTO ALOQUIRO (ID, ABREVIATURA,DESCRICAO,IMG_ALOQUIRO,ID_QUIREMA) VALUES (";
                String[] valores = linha.split(separador);
                if (valores.length == 5)
                {                      
                    linhaSQL += valores[0]+",";
                    linhaSQL += "'"+valores[1]+valores[2]+"',";
                    linhaSQL += "'"+valores[3]+"',";
                    linhaSQL += "'/img/quiremas/"+ valores[0] + valores[4] +"',";
                    Quirema q = new Quirema().buscaPorAbrev(valores[1]);                    
                                        
                    linhaSQL += "" + q.getId()+");";                    
                }
                else
                {
                    for (int i = valores.length-1; i >= 0; i--)
                        System.out.print(valores[i] + " || ");
                    System.out.println("");
                }
                
                //this.insere(linhaSQL);                
                System.out.println(linhaSQL);
                linhaSQL = "";
            }                      
        } catch (FileNotFoundException e) {             
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
        } catch (IOException e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );          
        } finally {  
            if (br != null) 
            {  
                System.out.println("*********** ERRO **********" + linhaSQL);
                try {  
                    br.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }         
  }
  
  /* Insercao em lote */
  public void batchInsAloquiros(File arq)
  {
        String sql = "INSERT INTO ALOQUIRO (ABREVIATURA,DESCRICAO,IMG_ALOQUIRO,ID_QUIREMA) VALUES (?, ?, ?, ?)";
        String linhaSQL = "INSERT INTO ALOQUIRO (ABREVIATURA,DESCRICAO,IMG_ALOQUIRO,ID_QUIREMA) VALUES (";
        String linha;
        String separador = "@";
        BufferedReader br = null; 
        final int batchSize = 200;
        PreparedStatement ps;
        int count = 0;      
      
        try { 
            ps = this.conexao.prepareStatement(sql);
            br = new BufferedReader(new FileReader(arq));  
            while ((linha = br.readLine()) != null) 
            {
                String[] valores = linha.split(separador);
                if (valores.length == 5)
                {                    
                    linhaSQL.concat(valores[0]+"," );
                    linhaSQL.concat("'"+valores[1]+valores[2]+"',");
                    linhaSQL.concat("'/img/quiremas/"+ valores[0] + valores[4] +"',");
                    linhaSQL.concat("0);");
             
                }
                else
                {
                    for (int i = valores.length-1; i >= 0; i--)
                        System.out.print(valores[i] + " || ");
                    System.out.println("");
                }
              /* 
                if(++count % batchSize == 0) 
                   ps.executeBatch();  */
            }          
            //  ps.executeBatch(); // insert remaining records
              ps.close();          
        } catch (FileNotFoundException e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );  
        } catch (IOException e) {  
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );          
        } catch ( java.sql.SQLException e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        } finally {  
            if (br != null) 
            {  
                try {  
                    br.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }         
  }
}
