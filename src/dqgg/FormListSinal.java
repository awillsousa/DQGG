
package dqgg;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author AntonioSousa
 */
public class FormListSinal extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    Comparator<Sinal> comparaSinais;           // comparator para realizar a comparação de sinais
    private ArrayList<Sinal> listaSinaisBA;        
    private Sinal sinalAtualBA;
    private FormSinal formSinal;
    private StackTraceElement[] stackTrace;
    private String ultimaBusca;
    
    /**
     * Creates new form FormListSinal
     */
    public FormListSinal() {
        initComponents();        
        
        /* Declara um comparador especifico para os nomes dos sinais em Português */
        comparaSinais = new Comparator<Sinal>() {
                                         @Override
                                         public int compare(Sinal s1, Sinal s2) 
                                         {                                             
                                             Collator collator = Collator.getInstance(new Locale("pt", "BR"));
                                             collator.setStrength(Collator.CANONICAL_DECOMPOSITION);        
                                             return collator.compare(s1.getNome(), s2.getNome());                                                        
                                         }
                                     };
        
        this.listaSinaisBA = new Sinal().buscaTodos();
        
        /* Configura model e eventos da tbListaSinais - utilizada na Busca Alfabética */                  
        Collections.sort(listaSinaisBA, comparaSinais);                
        tbListaSinais.setModel(new ListaSinaisTM(listaSinaisBA));        
        this.labelTotalBA.setText(String.valueOf(this.listaSinaisBA.size()));                       
        sinalAtualBA = this.listaSinaisBA.get(0);
        
        ListSelectionModel cellSMSinalBA = tbListaSinais.getSelectionModel();
        cellSMSinalBA.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        
        cellSMSinalBA.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {            
                int linhaSelecionada = tbListaSinais.getSelectedRow(); 
                if ((listaSinaisBA.size() > 0)&&(linhaSelecionada > -1))
                    sinalAtualBA = listaSinaisBA.get(linhaSelecionada);
                else 
                    sinalAtualBA = null;
                atualizaSinalBA();
          }
        });
        
        tbListaSinais.getColumnModel().getColumn(0).setPreferredWidth(22);
        tbListaSinais.getColumnModel().getColumn(1).setPreferredWidth(220);
        tbListaSinais.getColumnModel().getColumn(2).setPreferredWidth(300);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tbListaSinais.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
                
        tbListaSinais.setSelectionMode( ListSelectionModel.SINGLE_SELECTION ); 
        //tbListaSinais.getRowCount(); 
        tbListaSinais.setRowSelectionInterval( 0, tbListaSinais.getRowCount()-1 );
        
        final FormListSinal formLista = this;
        tbListaSinais.addMouseListener(new MouseAdapter(){  
                                            @Override
                                            public void mouseClicked(MouseEvent e){  
                                              if(e.getClickCount() == 2){  
                                                // abre o formulário com os dados do Sinal atual
                                                 int linhaSelecionada = tbListaSinais.getSelectedRow();                                                  
                                                formSinal = new FormSinal(listaSinaisBA.get(linhaSelecionada), formLista);
                                                formSinal.setVisible(true);
                                              }  
                                            }  
                                           }); 
        
        ultimaBusca = "";
    }

    private void atualizaSinalBA() 
    {         
        ListaSinaisTM stm;
        
        int indiceBA = 0;
        stm = (ListaSinaisTM) this.tbListaSinais.getModel();
                        
        if ((stm != null)&&(sinalAtualBA != null))
        {
            indiceBA = stm.getRowNumbyId(sinalAtualBA.getId());
            // Atualiza os valores dos campos de texto            
            
            this.tbListaSinais.getSelectionModel().setSelectionInterval(indiceBA, indiceBA);            
            this.tbListaSinais.changeSelection(indiceBA, 0, false, false);
            this.tbListaSinais.requestFocus();            
        }        
    }
        
    public void atualizaTabela()
    {        
        buscaSinalAlfa(this.ultimaBusca);                    
    }
    
    private void buscaSinalAlfa(String s)            
    {  
      ultimaBusca = s;  
      this.TabBuscaAlfa.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (s.isEmpty())
        {
            listaSinaisBA = new Sinal().buscaTodos();
        }
        else 
        {
            if (s.length() < 3)
                JOptionPane.showMessageDialog(this,"Essa busca poderá retornar muitos registros. \n Tente uma busca mais precisa.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);
                        
            listaSinaisBA = new Sinal().buscaAlfabetica(s);            
        }            
        
        if (listaSinaisBA.isEmpty())
        {            
            JOptionPane.showMessageDialog(this,"Não foram encontrados sinais com esse critério de busca.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);    
            
            tbListaSinais.selectAll();
            tbListaSinais.clearSelection();
            this.listaSinaisBA = new ArrayList<Sinal>();
            ListaSinaisTM stm = new ListaSinaisTM(this.listaSinaisBA);            
            this.tbListaSinais.setModel(stm);            
            this.sinalAtualBA = null;
            
            this.labelTotalBA.setText(String.valueOf(this.listaSinaisBA.size()));                        
            this.TabBuscaAlfa.setCursor(Cursor.getDefaultCursor());            
            this.tbListaSinais.requestFocus();
        }
        else
        {
            Collections.sort(listaSinaisBA, this.comparaSinais);
            this.tbListaSinais.setModel(new ListaSinaisTM(listaSinaisBA)); 
            this.labelTotalBA.setText(String.valueOf(this.listaSinaisBA.size()));            
            this.sinalAtualBA = listaSinaisBA.get(0); 
                        
            this.tbListaSinais.changeSelection(0, 0, false, false);
            this.tbListaSinais.requestFocus();
        }
        tbListaSinais.getColumnModel().getColumn(0).setPreferredWidth(22);
        tbListaSinais.getColumnModel().getColumn(1).setPreferredWidth(220);
        tbListaSinais.getColumnModel().getColumn(2).setPreferredWidth(300);
      this.TabBuscaAlfa.setCursor(Cursor.getDefaultCursor());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TabBuscaAlfa = new javax.swing.JPanel();
        PanelNomes = new javax.swing.JScrollPane();
        tbListaSinais = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtBuscaAlfa = new javax.swing.JTextField();
        btBuscaAlfa = new javax.swing.JButton();
        btBuscaAlfaTodos = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        txtIrparaID = new javax.swing.JTextField();
        btIrPara = new javax.swing.JButton();
        btNovoSinal = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        labelTotalBA = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DQSG(G) - Lista Sinais");
        setMaximumSize(new java.awt.Dimension(950, 700));
        setName("formListSinal"); // NOI18N
        setPreferredSize(new java.awt.Dimension(950, 700));
        setResizable(false);

        TabBuscaAlfa.setOpaque(false);
        TabBuscaAlfa.setPreferredSize(new java.awt.Dimension(620, 555));
        TabBuscaAlfa.setRequestFocusEnabled(false);
        TabBuscaAlfa.setLayout(new javax.swing.BoxLayout(TabBuscaAlfa, javax.swing.BoxLayout.PAGE_AXIS));

        PanelNomes.setAlignmentX(0.0F);
        PanelNomes.setAlignmentY(0.0F);
        PanelNomes.setMinimumSize(new java.awt.Dimension(450, 140));
        PanelNomes.setPreferredSize(new java.awt.Dimension(450, 140));

        tbListaSinais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbListaSinais.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "NomeNormal", "Nome", "Id", "Movimentos", "Descrição", "Arquivo", "Sinais"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbListaSinais.setAlignmentX(0.0F);
        tbListaSinais.setAlignmentY(0.0F);
        tbListaSinais.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbListaSinais.setAutoscrolls(false);
        tbListaSinais.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbListaSinais.setMaximumSize(new java.awt.Dimension(32767, 32767));
        tbListaSinais.setMinimumSize(new java.awt.Dimension(450, 140));
        tbListaSinais.setRowHeight(20);
        tbListaSinais.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbListaSinais.getTableHeader().setReorderingAllowed(false);
        PanelNomes.setViewportView(tbListaSinais);

        TabBuscaAlfa.add(PanelNomes);

        jPanel2.setAlignmentX(1.0F);
        jPanel2.setAlignmentY(1.0F);
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanel2.setMinimumSize(new java.awt.Dimension(595, 30));

        txtBuscaAlfa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscaAlfaActionPerformed(evt);
            }
        });

        btBuscaAlfa.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btBuscaAlfa.setText("Buscar");
        btBuscaAlfa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBuscaAlfaActionPerformed(evt);
            }
        });

        btBuscaAlfaTodos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btBuscaAlfaTodos.setText("Todos");
        btBuscaAlfaTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btBuscaAlfaTodosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(txtBuscaAlfa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btBuscaAlfa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btBuscaAlfaTodos))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtBuscaAlfa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btBuscaAlfa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btBuscaAlfaTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setAlignmentX(1.0F);
        jPanel6.setAlignmentY(1.0F);
        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanel6.setMinimumSize(new java.awt.Dimension(350, 30));
        jPanel6.setPreferredSize(new java.awt.Dimension(450, 30));

        txtIrparaID.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        txtIrparaID.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtIrparaID.setToolTipText("ID do sinal para o qual se deseja ir");
        txtIrparaID.setAlignmentX(1.0F);

        btIrPara.setFont(new java.awt.Font("Verdana", 1, 15)); // NOI18N
        btIrPara.setText("Ir");
        btIrPara.setActionCommand("Ir para Linha");
        btIrPara.setOpaque(false);
        btIrPara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIrParaActionPerformed(evt);
            }
        });

        btNovoSinal.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btNovoSinal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/BD/text70.png"))); // NOI18N
        btNovoSinal.setText("Novo Sinal");
        btNovoSinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNovoSinalActionPerformed(evt);
            }
        });

        btExcluir.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/BD/delete96.png"))); // NOI18N
        btExcluir.setText("Excluir");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Linha");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btNovoSinal, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIrparaID, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btIrPara)
                .addGap(1, 1, 1))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIrparaID, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btIrPara, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btNovoSinal, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        btIrPara.getAccessibleContext().setAccessibleName("Ir para Linha");

        jPanel7.setAlignmentX(1.0F);
        jPanel7.setAlignmentY(1.0F);
        jPanel7.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanel7.setMinimumSize(new java.awt.Dimension(350, 30));
        jPanel7.setPreferredSize(new java.awt.Dimension(450, 30));

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Total de Registros:");
        jLabel5.setAlignmentX(1.0F);

        labelTotalBA.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        labelTotalBA.setForeground(new java.awt.Color(51, 0, 204));
        labelTotalBA.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTotalBA.setText("0");
        labelTotalBA.setAlignmentX(1.0F);
        labelTotalBA.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(0, 610, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTotalBA, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(labelTotalBA))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 894, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TabBuscaAlfa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 894, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 881, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TabBuscaAlfa, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscaAlfaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscaAlfaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscaAlfaActionPerformed

    private void btBuscaAlfaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscaAlfaActionPerformed
        if (!this.txtBuscaAlfa.getText().equals(""))
        {
            buscaSinalAlfa(this.txtBuscaAlfa.getText());            
        }
    }//GEN-LAST:event_btBuscaAlfaActionPerformed

    private void btBuscaAlfaTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscaAlfaTodosActionPerformed
        txtBuscaAlfa.setText("");
        buscaSinalAlfa("");        

    }//GEN-LAST:event_btBuscaAlfaTodosActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        int opcao = JOptionPane.showConfirmDialog(this,
            "Confirmar exclusão do registro?",
            "CONFIRMAÇÃO",
            JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION)
        {
            if (this.sinalAtualBA.exclui()){
                JOptionPane.showMessageDialog(this,"Dados alterados com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this,"Erro durante a esclusão de dados!","ERRO",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void btNovoSinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNovoSinalActionPerformed
        formSinal = new FormSinal(null);
        formSinal.setVisible(true);
    }//GEN-LAST:event_btNovoSinalActionPerformed

    private void btIrParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIrParaActionPerformed
        if (!this.txtIrparaID.getText().equals(""))
        {
            try {
                int reg = Integer.parseInt(this.txtIrparaID.getText());
                if (reg > 0) reg -= 1; else reg = 0;
                this.tbListaSinais.setRowSelectionInterval(reg, reg);

            } catch (NumberFormatException e){
                e.setStackTrace(stackTrace);
            }
        }
    }//GEN-LAST:event_btIrParaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormListSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormListSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormListSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormListSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormListSinal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane PanelNomes;
    private javax.swing.JPanel TabBuscaAlfa;
    private javax.swing.JButton btBuscaAlfa;
    private javax.swing.JButton btBuscaAlfaTodos;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btIrPara;
    private javax.swing.JButton btNovoSinal;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel labelTotalBA;
    private javax.swing.JTable tbListaSinais;
    private javax.swing.JTextField txtBuscaAlfa;
    private javax.swing.JTextField txtIrparaID;
    // End of variables declaration//GEN-END:variables
}
