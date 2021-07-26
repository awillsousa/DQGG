
package dqgg;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

/**
 * TableModel utilizado para tabelas de exibição de listas
 * de Aloquiros
 * 
 * @author AntonioSousa
 */
class AloqTableModel extends AbstractTableModel {  
        private static final long serialVersionUID = 1L;
        
        public static final int DESCRICAO_IDX = 0;
        public static final int ID_IDX = 1;        
        public static final int ABREVIATURA_IDX = 2;  
        public static final int IMAGEM_IDX = 3;  
        public static final int QUIREMA_IDX = 4;  
        
        
        private List<Aloquiro> data;
        private String[] columnNames = {"Descrição"};
        
 
        public AloqTableModel(ArrayList<Aloquiro> lista)
        {
            super();
            data = (List<Aloquiro>) lista;            
        }
        
        public void addRow(Aloquiro a)
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
        
        public Aloquiro getRowAt(int rowIndex) {
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
            if (row < 0) return (null);
            if (col == AloqTableModel.DESCRICAO_IDX)
                return data.get(row).getDescricao();
            if (col == AloqTableModel.ID_IDX)
                return data.get(row).getId();
            if (col == AloqTableModel.ABREVIATURA_IDX)
                return data.get(row).getAbreviatura();
            if (col == AloqTableModel.IMAGEM_IDX)
                return data.get(row).getImagem();
            if (col == AloqTableModel.QUIREMA_IDX)
                return data.get(row).getQuirema().getDescricao();
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

