
package dqgg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * TableModel utilizado para tabelas de Sinais
 * @author AntonioSousa
 */
class SinaisTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
        public static final int NOMENORMAL_IDX = 0;
        public static final int NOME_IDX = 1;
        public static final int ID_IDX = 2;        
        public static final int MOVIMENTOS_IDX = 3;  
        public static final int DESCRICAO_IDX = 4;  
        public static final int ARQUIVO_IDX = 5;  
        public static final int ARQUIVOSINAIS_IDX = 6;  
        
        private List<Sinal> data;                
        //private String[] columnNames = {"Nome", "Id", "Movimentos", "Descrição", "Arquivo", "ArquivoSinais"};
        private String[] columnNames = {"NormeNormalizado", "Nome"};
        
        public SinaisTableModel(ArrayList<Sinal> lista)
        {
            super();
            data = (List) lista;            
        }              
        
        public void addRow(Sinal a)
        {
            if (a !=  null)
            {    
                this.data.add(a); 
                this.fireTableRowsInserted(this.data.indexOf(a), this.data.indexOf(a));
            }
        }
                
        public void removeRow(int i)
        {
            if ((i >=0)&&(i < this.data.size()))
            {                
                this.data.remove(i);
                fireTableRowsDeleted(i, i); 
            }
            
        }
        
        public int getRowNumbyId(int id)
        {
            for (Iterator<Sinal> i = data.iterator(); i.hasNext();)
            {
                Sinal s = i.next();
                if (s.getId() == id)
                    return (data.indexOf(s));
            }
            return (-1);            
        }
        
        public Sinal getRowAt(int rowIndex) {
         return data.get(rowIndex);
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
 
        @Override
        public int getRowCount() {
            return data.size();
        }
 
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        @Override
        public Object getValueAt(int row, int col) {
            if ((row < 0)||(data.isEmpty())) return (null);
            if (col == SinaisTableModel.NOME_IDX)
                return data.get(row).getNome();
            if (col == SinaisTableModel.NOMENORMAL_IDX)
                return data.get(row).getNomeNormalizado();
            if (col == SinaisTableModel.ID_IDX)
                return data.get(row).getId();
            if (col == SinaisTableModel.MOVIMENTOS_IDX)
                return data.get(row).getMovimentos();
            if (col == SinaisTableModel.DESCRICAO_IDX)
                return data.get(row).getDescricao();
            if (col == SinaisTableModel.ARQUIVO_IDX)
                return data.get(row).getArquivo();
            if (col == SinaisTableModel.ARQUIVOSINAIS_IDX)
                return data.get(row).getArquivoSinais();
            return null;
        }
 
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {
            int columnCount;
            // dataModel is an object of the data Model class(default or abstract)
            columnCount = this.getRowCount();
              if(columnCount <= 0){
                 return String.class;
              }
                return getValueAt(0, c).getClass();            
        }
 
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2) {
                return false;
            } else {
                return true;
            }
        }
} 
