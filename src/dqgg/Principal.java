
package dqgg;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultRowSorter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultCaret;
import static org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.*;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 * Formulário principal da aplicação
 * @author AntonioSousa
 */
public class Principal extends javax.swing.JFrame implements MouseListener,MouseWheelListener {
    private static final long serialVersionUID = 1L;
    enum Modo {INCLUSAO, ALTERACAO, CONSULTA, GRAVACAO, ESPERA};
    Modo modo;
    
    ArrayList<Categoria> categorias = new Categoria().buscaTodos();
    Comparator<Sinal> comparaSinais;           // comparator para realizar a comparação de sinais
    
    FormSinal formSinal;    // formulario do banco de dados
    FormCategoria fc;  
    FormQuirema fq;    
    FormAloquiro fa;   
    FormListSinal frmListaSinal;
    
    ArrayList<Sinal> listaSinais;
    ArrayList<Sinal> listaSinaisBA;
    Sinal sinalAtual;
    Sinal sinalAtualBA;
    
    ArrayList<Aloquiro> aloqSelecionados = new ArrayList<Aloquiro>();
    Aloquiro aloqAtual;
    private int indiceAloquiro = 0;
    
    JTableBinding bindingAloq;
    JTableBinding tbSinal;
    
    ListSelectionListener lsl; // Listener para verificar alterações nas celulas da JTable
    TableModelListener tml;
    /**
     * Creates new form Principal
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Principal() {   
        /* Declara um comparador especifico para os nomes dos sinais em Português */
        comparaSinais = new Comparator<Sinal>() {
                                         @Override
                                         public int compare(Sinal s1, Sinal s2) 
                                         {                                             
                                             Collator collator = Collator.getInstance(new Locale("pt", "BR"));                                             
                                             collator.setStrength(Collator.PRIMARY);                                             
                                             return collator.compare(s1.getNomeNormalizado(), s2.getNomeNormalizado());                                                        
                                         }
                                     };
        
        
        this.modo = Modo.ALTERACAO;
        this.listaSinais   = new Sinal().buscaTodos();
        this.listaSinaisBA = new Sinal().buscaTodos();
                
        initComponents(); 
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Altera o autoscrolling do JTextPane. Evitando dele rolar para a última linha
        DefaultCaret caret = (DefaultCaret)TextDescricao.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);        
        DefaultCaret caretBA = (DefaultCaret)TextDescricaoBA.getCaret();
        caretBA.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        // Percorre todas as categorias montando uma tab para cada
        Iterator<Categoria> i = this.categorias.iterator();
        while (i.hasNext())
        {
            Categoria c = i.next();
            TabCategoria t = new TabCategoria(c, this);            
            JPanel panel = new JPanel();
            panel.add(t, BorderLayout.PAGE_START);
            this.PanelTabs.add(panel, this.PanelTabs.getTabCount());            
            this.PanelTabs.setTitleAt(this.PanelTabs.getTabCount()-1, t.getTitulo());            
        }                      
        
        /* Configura model e eventos da TableNomes */         
        TableNomes.setModel(new SinaisTableModel(listaSinais));        
        this.ordenaColuna(TableNomes);
        this.labelTotal.setText(String.valueOf(this.listaSinais.size()));                               
        sinalAtual   = this.listaSinais.get(0);        
        this.atualizaSinal();        
        
        ListSelectionModel cellSMSinal = TableNomes.getSelectionModel();
        cellSMSinal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        
        cellSMSinal.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {            
                int linhaSelecionada = TableNomes.getSelectedRow(); 
                if ((listaSinais.size() > 0)&&(linhaSelecionada > -1))
                    sinalAtual = listaSinais.get(linhaSelecionada);
                else 
                    sinalAtual = null;
                atualizaSinal();
          }
        });
              
        /* Configura model e eventos da TableNomesBA - utilizada na Busca Alfabética */                  
             
        TableNomesBA.setModel(new SinaisTableModel(listaSinaisBA));
        this.ordenaColuna(TableNomesBA);
        this.labelTotalBA.setText(String.valueOf(this.listaSinaisBA.size()));                       
        sinalAtualBA = this.listaSinaisBA.get(0);
        this.atualizaSinalBA();
                
        ListSelectionModel cellSMSinalBA = TableNomesBA.getSelectionModel();
        cellSMSinalBA.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        
        cellSMSinalBA.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {            
                int linhaSelecionada = TableNomesBA.getSelectedRow(); 
                if ((listaSinaisBA.size() > 0)&&(linhaSelecionada > -1))
                    sinalAtualBA = listaSinaisBA.get(linhaSelecionada);
                else 
                    sinalAtualBA = null;
                atualizaSinalBA();
          }
        });
        
        /* Configura a tabela dos aloquiros selecionados para busca */
        TableAloqs.setModel(new AloqTableModel(this.aloqSelecionados));
        
        tml = new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();                                
                AloqTableModel model = (AloqTableModel) e.getSource();

                if (e.getType() == TableModelEvent.INSERT)
                {                    
                    int linhaSelecionada = TableAloqs.getSelectedRow();
                    if (linhaSelecionada < 0 ) linhaSelecionada = 0;
                    indiceAloquiro = linhaSelecionada;
                    aloqAtual = aloqSelecionados.get(linhaSelecionada);
                    atualizaAloq();
                }
                else if (e.getType() == TableModelEvent.DELETE)  // Isso evita do evento no TableModel chamar varias vezes o tableChanged
                {
                   model.removeTableModelListener(this);                   
                   model.addTableModelListener(this);                   
                }                
            }
        };
        
        ListSelectionListener lslAloq = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
                int linhaSelecionada = TableAloqs.getSelectedRow();                 
                indiceAloquiro = linhaSelecionada;
                aloqAtual = aloqSelecionados.get(linhaSelecionada);
                atualizaAloq();                
          }
        };
       
        TableAloqs.getModel().addTableModelListener(tml);
        TableAloqs.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);                
        this.addMouseListener(this);
        this.LabelQuirema.addMouseWheelListener(this);
        this.LabelQuirema.addMouseListener(this);
        this.LabelQuirema.setName("LabelQuirema");   
        
        // Oculta menus. Esses menus somente devem ser utilizados em modo de desenvolvimento. 
        this.jMenu2.setVisible(false);
        this.jMenu1.setVisible(false);
        
        // Atualiza a viewport do JScrollPane (para evitar rolagem automática)        
        PanelDescricao1.setViewportView(TextDescricaoBA);
        
        /* Oculta a primeira coluna com os textos normalizados */
        // Esse ocultamento das colunas é feito por conta da ordenação do JTable
        // A tabela é ordenada pelo nome normalizado e depois essa coluna do campo normalizado
        // é escondida.
        this.ocultaColunas(TableNomes, 0);
        this.ocultaColunas(TableNomesBA, 0);        
    }

    /* Ordena o JTable passado pela primeira coluna, que estará oculta. Esta coluna tem as strings todas normalizadas
     para permitir uma ordenação específica do sistema. */
    private void ordenaColuna(JTable tabela)
    {          
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tabela.getModel());
        Comparator<String>     comparator = new Comparator<String>() {
                                                            @Override
                                                            public int compare(String s1, String s2) 
                                                            {
                                                                Collator collator = Collator.getInstance(new Locale("pt", "BR"));                                                                
                                                                collator.setStrength(Collator.PRIMARY);
                                                                String[] as1 = s1.split(" ");
                                                                String[] as2 = s2.split(" ");
                                                                return collator.compare(as1[0], as2[0]); 
                                                            }
                                                        };        
        sorter.setComparator(0, comparator); 
        tabela.setRowSorter(sorter);                
        TableRowSorter rowSorter = (TableRowSorter) tabela.getRowSorter(); 
        rowSorter.setSortsOnUpdates(true);
        List<SortKey> keys = new ArrayList<SortKey>();
        SortKey sortKey = new SortKey(0, SortOrder.ASCENDING); // ordenação da coluna 0
        keys.add(sortKey);
        rowSorter.setSortKeys(keys);

        rowSorter.sort();       
    }
    
    /* Oculta uma coluna da JTable passada, alterando seu tamanho para 0 */
    private void ocultaColunas(JTable tabela, int num) {
        tabela.getColumnModel().getColumn(num).setMinWidth(0);
        tabela.getColumnModel().getColumn(num).setMaxWidth(0);
        tabela.getColumnModel().getColumn(num).setWidth(0); 
        tabela.repaint();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        arrayList1 = new java.util.ArrayList<Aloquiro>();
        jPanel3 = new javax.swing.JPanel();
        PanelTabs = new javax.swing.JTabbedPane();
        TabSinais = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        labelTotal = new javax.swing.JLabel();
        PanelNomes = new javax.swing.JScrollPane();
        TableNomes = new javax.swing.JTable();
        PanelDescricao = new javax.swing.JScrollPane();
        TextDescricao = new javax.swing.JTextPane();
        PanelRepresentacao = new javax.swing.JPanel();
        LabelRepresentacao = new javax.swing.JLabel();
        PanelSinais = new javax.swing.JPanel();
        LabelSinais = new javax.swing.JLabel();
        TabBuscaAlfa = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        labelTotalBA = new javax.swing.JLabel();
        PanelNomes1 = new javax.swing.JScrollPane();
        TableNomesBA = new javax.swing.JTable();
        PanelDescricao1 = new javax.swing.JScrollPane();
        TextDescricaoBA = new javax.swing.JTextPane();
        PanelRepresentacao2 = new javax.swing.JPanel();
        LabelRepresentacaoBA = new javax.swing.JLabel();
        PanelSinais1 = new javax.swing.JPanel();
        LabelSinaisBA = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtBuscaAlfa = new javax.swing.JTextField();
        btBuscaAlfa = new javax.swing.JButton();
        btBuscaAlfaTodos = new javax.swing.JButton();
        PanelSelecionadas = new javax.swing.JPanel();
        PanelDescSelecionadas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableAloqs = new javax.swing.JTable();
        PanelQuiremas = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btQuirAnt = new javax.swing.JButton();
        panelImgSelec = new javax.swing.JPanel();
        LabelQuirema = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TextDescQuirema = new javax.swing.JTextPane();
        btQuirProx = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btLocalizar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btLimpar = new javax.swing.JButton();
        menuPrincipal = new javax.swing.JMenuBar();
        menuBases = new javax.swing.JMenu();
        menuCategoria = new javax.swing.JMenuItem();
        menuQuirema = new javax.swing.JMenuItem();
        menuAloquiro = new javax.swing.JMenuItem();
        menuSinal = new javax.swing.JMenuItem();
        menuSair = new javax.swing.JMenuItem();
        menuBases1 = new javax.swing.JMenu();
        menuSinal1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        menuImpSinais = new javax.swing.JMenuItem();
        menuImpTQuir = new javax.swing.JMenuItem();
        menuDelSinais = new javax.swing.JMenuItem();
        menuDelTQuir = new javax.swing.JMenuItem();
        menuInsQuirIniciais = new javax.swing.JMenuItem();
        menuBases2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DQSG(G)");
        setMinimumSize(new java.awt.Dimension(900, 675));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.X_AXIS));

        PanelTabs.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        PanelTabs.setAlignmentX(0.0F);
        PanelTabs.setAlignmentY(0.0F);
        PanelTabs.setAutoscrolls(true);
        PanelTabs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        PanelTabs.setMinimumSize(new java.awt.Dimension(515, 675));
        PanelTabs.setPreferredSize(new java.awt.Dimension(515, 675));

        TabSinais.setAlignmentX(0.0F);
        TabSinais.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TabSinais.setLayout(new javax.swing.BoxLayout(TabSinais, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel4.setAlignmentX(0.0F);
        jPanel4.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanel4.setMinimumSize(new java.awt.Dimension(595, 25));
        jPanel4.setPreferredSize(new java.awt.Dimension(595, 25));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Total de Sinais Encontrados:");
        jLabel2.setAlignmentY(0.0F);

        labelTotal.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelTotal.setForeground(new java.awt.Color(51, 0, 204));
        labelTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTotal.setText("0");
        labelTotal.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(348, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(labelTotal))
                .addGap(0, 7, Short.MAX_VALUE))
        );

        TabSinais.add(jPanel4);

        PanelNomes.setAlignmentX(0.0F);
        PanelNomes.setAlignmentY(0.0F);
        PanelNomes.setMinimumSize(new java.awt.Dimension(450, 140));
        PanelNomes.setPreferredSize(new java.awt.Dimension(450, 140));

        TableNomes.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TableNomes.setModel(new javax.swing.table.DefaultTableModel(
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
        TableNomes.setAlignmentX(0.0F);
        TableNomes.setAlignmentY(0.0F);
        TableNomes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableNomes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TableNomes.setMaximumSize(new java.awt.Dimension(32767, 32767));
        TableNomes.setMinimumSize(new java.awt.Dimension(450, 140));
        TableNomes.setRowHeight(20);
        TableNomes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TableNomes.getTableHeader().setReorderingAllowed(false);
        PanelNomes.setViewportView(TableNomes);
        TableNomes.getColumnModel().getColumn(0).setMinWidth(0);
        TableNomes.getColumnModel().getColumn(0).setPreferredWidth(0);
        TableNomes.getColumnModel().getColumn(0).setMaxWidth(0);
        TableNomes.getColumnModel().getColumn(1).setResizable(false);
        TableNomes.getColumnModel().getColumn(2).setResizable(false);
        TableNomes.getColumnModel().getColumn(3).setResizable(false);
        TableNomes.getColumnModel().getColumn(4).setResizable(false);
        TableNomes.getColumnModel().getColumn(5).setResizable(false);
        TableNomes.getColumnModel().getColumn(6).setResizable(false);

        TabSinais.add(PanelNomes);

        PanelDescricao.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        PanelDescricao.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        PanelDescricao.setAlignmentX(0.0F);
        PanelDescricao.setAlignmentY(0.0F);
        PanelDescricao.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        PanelDescricao.setMinimumSize(new java.awt.Dimension(628, 120));
        PanelDescricao.setPreferredSize(new java.awt.Dimension(628, 120));

        TextDescricao.setEditable(false);
        TextDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TextDescricao.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TextDescricao.setMaximumSize(new java.awt.Dimension(32767, 32767));
        TextDescricao.setMinimumSize(new java.awt.Dimension(628, 120));
        TextDescricao.setPreferredSize(new java.awt.Dimension(628, 120));
        PanelDescricao.setViewportView(TextDescricao);

        TabSinais.add(PanelDescricao);

        PanelRepresentacao.setBackground(new java.awt.Color(255, 255, 255));
        PanelRepresentacao.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelRepresentacao.setAlignmentX(0.0F);
        PanelRepresentacao.setAlignmentY(0.0F);
        PanelRepresentacao.setMinimumSize(new java.awt.Dimension(628, 140));
        PanelRepresentacao.setPreferredSize(new java.awt.Dimension(628, 140));
        PanelRepresentacao.setLayout(new java.awt.BorderLayout());

        LabelRepresentacao.setBackground(new java.awt.Color(0, 0, 255));
        LabelRepresentacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelRepresentacao.setAlignmentX(0.5F);
        LabelRepresentacao.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LabelRepresentacao.setMaximumSize(new java.awt.Dimension(32767, 32767));
        LabelRepresentacao.setMinimumSize(new java.awt.Dimension(628, 140));
        LabelRepresentacao.setName("LabelRepresentacao"); // NOI18N
        LabelRepresentacao.setPreferredSize(new java.awt.Dimension(628, 140));
        PanelRepresentacao.add(LabelRepresentacao, java.awt.BorderLayout.CENTER);
        LabelRepresentacao.getAccessibleContext().setAccessibleName("LabelRepresentacao");

        TabSinais.add(PanelRepresentacao);

        PanelSinais.setBackground(new java.awt.Color(255, 255, 255));
        PanelSinais.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelSinais.setAlignmentX(0.0F);
        PanelSinais.setAlignmentY(0.0F);
        PanelSinais.setMinimumSize(new java.awt.Dimension(628, 140));
        PanelSinais.setPreferredSize(new java.awt.Dimension(628, 140));
        PanelSinais.setLayout(new java.awt.BorderLayout());

        LabelSinais.setBackground(new java.awt.Color(0, 0, 255));
        LabelSinais.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelSinais.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LabelSinais.setMaximumSize(new java.awt.Dimension(32767, 32767));
        LabelSinais.setMinimumSize(new java.awt.Dimension(628, 140));
        LabelSinais.setName("LabelSinais"); // NOI18N
        LabelSinais.setPreferredSize(new java.awt.Dimension(628, 140));
        PanelSinais.add(LabelSinais, java.awt.BorderLayout.CENTER);
        LabelSinais.getAccessibleContext().setAccessibleName("LabelSinais");

        TabSinais.add(PanelSinais);

        PanelTabs.addTab("Sinais", TabSinais);

        TabBuscaAlfa.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TabBuscaAlfa.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TabBuscaAlfa.setLayout(new javax.swing.BoxLayout(TabBuscaAlfa, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel6.setAlignmentX(0.0F);
        jPanel6.setMaximumSize(new java.awt.Dimension(32767, 50));
        jPanel6.setMinimumSize(new java.awt.Dimension(595, 25));

        jLabel3.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("Total de Sinais Encontrados:");
        jLabel3.setAlignmentY(0.0F);

        labelTotalBA.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelTotalBA.setForeground(new java.awt.Color(51, 0, 204));
        labelTotalBA.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelTotalBA.setText("0");
        labelTotalBA.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelTotalBA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(348, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelTotalBA))
                .addGap(0, 7, Short.MAX_VALUE))
        );

        TabBuscaAlfa.add(jPanel6);

        PanelNomes1.setAlignmentX(0.0F);
        PanelNomes1.setAlignmentY(0.0F);
        PanelNomes1.setMinimumSize(new java.awt.Dimension(450, 140));
        PanelNomes1.setPreferredSize(new java.awt.Dimension(450, 140));

        TableNomesBA.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TableNomesBA.setModel(new javax.swing.table.DefaultTableModel(
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
        TableNomesBA.setAlignmentX(0.0F);
        TableNomesBA.setAlignmentY(0.0F);
        TableNomesBA.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableNomesBA.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TableNomesBA.setMaximumSize(new java.awt.Dimension(32767, 32767));
        TableNomesBA.setMinimumSize(new java.awt.Dimension(450, 140));
        TableNomesBA.setRowHeight(20);
        TableNomesBA.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TableNomesBA.getTableHeader().setReorderingAllowed(false);
        PanelNomes1.setViewportView(TableNomesBA);
        TableNomesBA.getColumnModel().getColumn(0).setMinWidth(0);
        TableNomesBA.getColumnModel().getColumn(0).setPreferredWidth(0);
        TableNomesBA.getColumnModel().getColumn(0).setMaxWidth(0);
        TableNomesBA.getColumnModel().getColumn(1).setResizable(false);
        TableNomesBA.getColumnModel().getColumn(2).setResizable(false);
        TableNomesBA.getColumnModel().getColumn(3).setResizable(false);
        TableNomesBA.getColumnModel().getColumn(4).setResizable(false);
        TableNomesBA.getColumnModel().getColumn(5).setResizable(false);
        TableNomesBA.getColumnModel().getColumn(6).setResizable(false);

        TabBuscaAlfa.add(PanelNomes1);

        PanelDescricao1.setAlignmentX(0.0F);
        PanelDescricao1.setAlignmentY(0.0F);
        PanelDescricao1.setMinimumSize(new java.awt.Dimension(628, 120));
        PanelDescricao1.setPreferredSize(new java.awt.Dimension(628, 120));

        TextDescricaoBA.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TextDescricaoBA.setMaximumSize(new java.awt.Dimension(6, 23));
        PanelDescricao1.setViewportView(TextDescricaoBA);

        TabBuscaAlfa.add(PanelDescricao1);

        PanelRepresentacao2.setBackground(new java.awt.Color(255, 255, 255));
        PanelRepresentacao2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelRepresentacao2.setAlignmentX(0.0F);
        PanelRepresentacao2.setAlignmentY(0.0F);
        PanelRepresentacao2.setMinimumSize(new java.awt.Dimension(628, 140));
        PanelRepresentacao2.setPreferredSize(new java.awt.Dimension(628, 140));

        LabelRepresentacaoBA.setBackground(new java.awt.Color(0, 0, 255));
        LabelRepresentacaoBA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelRepresentacaoBA.setAlignmentY(0.0F);
        LabelRepresentacaoBA.setName("LabelRepresentacao"); // NOI18N
        LabelRepresentacaoBA.setPreferredSize(LabelRepresentacao.getMaximumSize());

        javax.swing.GroupLayout PanelRepresentacao2Layout = new javax.swing.GroupLayout(PanelRepresentacao2);
        PanelRepresentacao2.setLayout(PanelRepresentacao2Layout);
        PanelRepresentacao2Layout.setHorizontalGroup(
            PanelRepresentacao2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelRepresentacaoBA, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );
        PanelRepresentacao2Layout.setVerticalGroup(
            PanelRepresentacao2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelRepresentacaoBA, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        );

        TabBuscaAlfa.add(PanelRepresentacao2);

        PanelSinais1.setBackground(new java.awt.Color(255, 255, 255));
        PanelSinais1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelSinais1.setAlignmentX(0.0F);
        PanelSinais1.setAlignmentY(0.0F);
        PanelSinais1.setMinimumSize(new java.awt.Dimension(628, 140));
        PanelSinais1.setPreferredSize(new java.awt.Dimension(628, 140));

        LabelSinaisBA.setBackground(new java.awt.Color(0, 0, 255));
        LabelSinaisBA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelSinaisBA.setAlignmentY(0.0F);
        LabelSinaisBA.setName("LabelSinais"); // NOI18N

        javax.swing.GroupLayout PanelSinais1Layout = new javax.swing.GroupLayout(PanelSinais1);
        PanelSinais1.setLayout(PanelSinais1Layout);
        PanelSinais1Layout.setHorizontalGroup(
            PanelSinais1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelSinaisBA, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
        );
        PanelSinais1Layout.setVerticalGroup(
            PanelSinais1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelSinaisBA, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        );

        TabBuscaAlfa.add(PanelSinais1);

        jPanel2.setAlignmentX(0.0F);
        jPanel2.setAlignmentY(0.0F);
        jPanel2.setMinimumSize(new java.awt.Dimension(595, 35));
        jPanel2.setPreferredSize(new java.awt.Dimension(595, 35));

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
                .addComponent(txtBuscaAlfa, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
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

        TabBuscaAlfa.add(jPanel2);

        PanelTabs.addTab("Busca Alfabética", TabBuscaAlfa);

        jPanel3.add(PanelTabs);
        PanelTabs.getAccessibleContext().setAccessibleName("Busca Alfabética");

        PanelSelecionadas.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelSelecionadas.setAlignmentX(0.0F);
        PanelSelecionadas.setAlignmentY(0.0F);
        PanelSelecionadas.setMinimumSize(new java.awt.Dimension(300, 675));
        PanelSelecionadas.setPreferredSize(new java.awt.Dimension(300, 675));
        PanelSelecionadas.setLayout(new javax.swing.BoxLayout(PanelSelecionadas, javax.swing.BoxLayout.Y_AXIS));

        PanelDescSelecionadas.setAlignmentX(0.0F);
        PanelDescSelecionadas.setMinimumSize(new java.awt.Dimension(268, 470));
        PanelDescSelecionadas.setPreferredSize(new java.awt.Dimension(268, 470));

        jScrollPane1.setAlignmentY(0.0F);
        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(268, 470));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(268, 470));

        TableAloqs.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TableAloqs.setAlignmentX(0.0F);
        TableAloqs.setAlignmentY(0.0F);
        TableAloqs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TableAloqs.setEnabled(false);
        TableAloqs.setFocusable(false);
        TableAloqs.setRowHeight(20);
        TableAloqs.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, arrayList1, TableAloqs, "TableAloqs");
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${abreviatura}"));
        columnBinding.setColumnName("Abreviatura");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("Id");
        columnBinding.setColumnClass(Integer.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${imagem}"));
        columnBinding.setColumnName("Imagem");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${quirema}"));
        columnBinding.setColumnName("Quirema");
        columnBinding.setColumnClass(dqgg.Quirema.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(TableAloqs);
        TableAloqs.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TableAloqs.getColumnModel().getColumn(0).setResizable(false);

        PanelQuiremas.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelQuiremas.setAlignmentY(0.0F);
        PanelQuiremas.setMaximumSize(new java.awt.Dimension(32767, 1500));
        PanelQuiremas.setMinimumSize(new java.awt.Dimension(170, 200));
        PanelQuiremas.setPreferredSize(new java.awt.Dimension(170, 200));

        jPanel5.setAlignmentY(1.0F);
        jPanel5.setMaximumSize(new java.awt.Dimension(32767, 1050));
        jPanel5.setMinimumSize(new java.awt.Dimension(170, 122));
        jPanel5.setPreferredSize(new java.awt.Dimension(170, 122));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.X_AXIS));

        btQuirAnt.setFont(new java.awt.Font("Aharoni", 0, 14)); // NOI18N
        btQuirAnt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/prev.jpg"))); // NOI18N
        btQuirAnt.setAlignmentY(0.0F);
        btQuirAnt.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btQuirAnt.setMaximumSize(new java.awt.Dimension(40, 133));
        btQuirAnt.setMinimumSize(new java.awt.Dimension(40, 133));
        btQuirAnt.setPreferredSize(new java.awt.Dimension(40, 133));
        btQuirAnt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btQuirAntActionPerformed(evt);
            }
        });
        jPanel5.add(btQuirAnt);

        panelImgSelec.setBackground(new java.awt.Color(255, 255, 255));
        panelImgSelec.setAlignmentY(0.0F);
        panelImgSelec.setMinimumSize(new java.awt.Dimension(120, 116));
        panelImgSelec.setPreferredSize(new java.awt.Dimension(120, 116));

        LabelQuirema.setBackground(new java.awt.Color(255, 255, 255));
        LabelQuirema.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelQuirema.setAlignmentY(0.0F);
        LabelQuirema.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        LabelQuirema.setMaximumSize(new java.awt.Dimension(32767, 32767));
        LabelQuirema.setMinimumSize(new java.awt.Dimension(120, 116));
        LabelQuirema.setPreferredSize(new java.awt.Dimension(120, 116));

        javax.swing.GroupLayout panelImgSelecLayout = new javax.swing.GroupLayout(panelImgSelec);
        panelImgSelec.setLayout(panelImgSelecLayout);
        panelImgSelecLayout.setHorizontalGroup(
            panelImgSelecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelQuirema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelImgSelecLayout.setVerticalGroup(
            panelImgSelecLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelQuirema, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
        );

        jPanel5.add(panelImgSelec);

        jScrollPane2.setAlignmentY(0.0F);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(120, 116));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(120, 116));

        TextDescQuirema.setEditable(false);
        TextDescQuirema.setBorder(null);
        TextDescQuirema.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TextDescQuirema.setAlignmentX(0.0F);
        TextDescQuirema.setAlignmentY(0.0F);
        TextDescQuirema.setMaximumSize(new java.awt.Dimension(32767, 32767));
        TextDescQuirema.setMinimumSize(new java.awt.Dimension(120, 116));
        TextDescQuirema.setPreferredSize(new java.awt.Dimension(120, 116));
        jScrollPane2.setViewportView(TextDescQuirema);

        jPanel5.add(jScrollPane2);

        btQuirProx.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/next.jpg"))); // NOI18N
        btQuirProx.setAlignmentY(0.0F);
        btQuirProx.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btQuirProx.setMaximumSize(new java.awt.Dimension(40, 133));
        btQuirProx.setMinimumSize(new java.awt.Dimension(40, 133));
        btQuirProx.setPreferredSize(new java.awt.Dimension(40, 133));
        btQuirProx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btQuirProxActionPerformed(evt);
            }
        });
        jPanel5.add(btQuirProx);

        jPanel1.setAlignmentY(1.0F);
        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 80));
        jPanel1.setPreferredSize(new java.awt.Dimension(85, 34));

        btLocalizar.setText("Localizar");
        btLocalizar.setAlignmentX(0.5F);
        btLocalizar.setMaximumSize(new java.awt.Dimension(150, 65));
        btLocalizar.setMinimumSize(new java.awt.Dimension(85, 30));
        btLocalizar.setPreferredSize(new java.awt.Dimension(100, 35));
        btLocalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLocalizarActionPerformed(evt);
            }
        });
        jPanel1.add(btLocalizar);

        btExcluir.setText("Excluir");
        btExcluir.setAlignmentX(0.5F);
        btExcluir.setMaximumSize(new java.awt.Dimension(150, 65));
        btExcluir.setMinimumSize(new java.awt.Dimension(85, 30));
        btExcluir.setPreferredSize(new java.awt.Dimension(100, 35));
        btExcluir.setVerifyInputWhenFocusTarget(false);
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });
        jPanel1.add(btExcluir);

        btLimpar.setText("Limpar");
        btLimpar.setAlignmentX(0.5F);
        btLimpar.setMaximumSize(new java.awt.Dimension(150, 65));
        btLimpar.setMinimumSize(new java.awt.Dimension(85, 30));
        btLimpar.setPreferredSize(new java.awt.Dimension(100, 35));
        btLimpar.setVerifyInputWhenFocusTarget(false);
        btLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLimparActionPerformed(evt);
            }
        });
        jPanel1.add(btLimpar);

        javax.swing.GroupLayout PanelQuiremasLayout = new javax.swing.GroupLayout(PanelQuiremas);
        PanelQuiremas.setLayout(PanelQuiremasLayout);
        PanelQuiremasLayout.setHorizontalGroup(
            PanelQuiremasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelQuiremasLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(PanelQuiremasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        PanelQuiremasLayout.setVerticalGroup(
            PanelQuiremasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelQuiremasLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout PanelDescSelecionadasLayout = new javax.swing.GroupLayout(PanelDescSelecionadas);
        PanelDescSelecionadas.setLayout(PanelDescSelecionadasLayout);
        PanelDescSelecionadasLayout.setHorizontalGroup(
            PanelDescSelecionadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
            .addComponent(PanelQuiremas, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
        );
        PanelDescSelecionadasLayout.setVerticalGroup(
            PanelDescSelecionadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDescSelecionadasLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelQuiremas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        PanelSelecionadas.add(PanelDescSelecionadas);

        jPanel3.add(PanelSelecionadas);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        menuBases.setText("Bases de Dados");

        menuCategoria.setText("Categorias");
        menuCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCategoriaActionPerformed(evt);
            }
        });
        menuBases.add(menuCategoria);

        menuQuirema.setText("Sematosemas");
        menuQuirema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuQuiremaActionPerformed(evt);
            }
        });
        menuBases.add(menuQuirema);

        menuAloquiro.setText("Aloquiros");
        menuAloquiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAloquiroActionPerformed(evt);
            }
        });
        menuBases.add(menuAloquiro);

        menuSinal.setText("Sinais");
        menuSinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSinalActionPerformed(evt);
            }
        });
        menuBases.add(menuSinal);

        menuSair.setText("Sair");
        menuSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSairActionPerformed(evt);
            }
        });
        menuBases.add(menuSair);

        menuPrincipal.add(menuBases);

        menuBases1.setText("Listas");

        menuSinal1.setText("Sinais");
        menuSinal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSinal1ActionPerformed(evt);
            }
        });
        menuBases1.add(menuSinal1);

        menuPrincipal.add(menuBases1);

        jMenu2.setText("Listar");
        jMenu2.setEnabled(false);

        jMenuItem1.setText("Categorias");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem2.setText("Sematosemas");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("Aloquiros");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Sinais");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        menuPrincipal.add(jMenu2);

        jMenu1.setText("Importar/Exportar");
        jMenu1.setEnabled(false);

        menuImpSinais.setText("Importar Sinais (CSV)");
        menuImpSinais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImpSinaisActionPerformed(evt);
            }
        });
        jMenu1.add(menuImpSinais);

        menuImpTQuir.setText("Importar Tipos Quiremas (CSV)");
        menuImpTQuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImpTQuirActionPerformed(evt);
            }
        });
        jMenu1.add(menuImpTQuir);

        menuDelSinais.setText("Deletar Todos Sinais");
        menuDelSinais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDelSinaisActionPerformed(evt);
            }
        });
        jMenu1.add(menuDelSinais);

        menuDelTQuir.setText("Deletar Todos Tipos Quiremas");
        menuDelTQuir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDelTQuirActionPerformed(evt);
            }
        });
        jMenu1.add(menuDelTQuir);

        menuInsQuirIniciais.setText("Inserir Quiremas Iniciais");
        menuInsQuirIniciais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuInsQuirIniciaisActionPerformed(evt);
            }
        });
        jMenu1.add(menuInsQuirIniciais);

        menuPrincipal.add(jMenu1);

        menuBases2.setText("Sobre");
        menuBases2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuBases2MouseClicked(evt);
            }
        });
        menuPrincipal.add(menuBases2);

        setJMenuBar(menuPrincipal);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /* Abre o form dos Quiremas */
    private void menuQuiremaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuQuiremaActionPerformed
        this.fq = new FormQuirema();
        this.fq.setVisible(true);        
    }//GEN-LAST:event_menuQuiremaActionPerformed

    /* Abre o form de categorias */
    private void menuCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCategoriaActionPerformed
        this.fc = new FormCategoria();
        this.fc.setVisible(true);        
    }//GEN-LAST:event_menuCategoriaActionPerformed

    /* Abre o form de aloquiros */
    private void menuAloquiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAloquiroActionPerformed
        this.fa = new FormAloquiro();
        this.fa.setVisible(true);        
    }//GEN-LAST:event_menuAloquiroActionPerformed

    /* Abre o form de sinais */
    private void menuSinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSinalActionPerformed
        this.formSinal = new FormSinal();    
        this.formSinal.setVisible(true);  
    }//GEN-LAST:event_menuSinalActionPerformed

    /* Utilizado para realizar a importação em massa de SINAIS a partir de um arquivo de texto
     * ( CUIDADO!!! )
     */
    private void menuImpSinaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuImpSinaisActionPerformed
        JFileChooser fcArquivo;   // Janela (dialog) para selecao de arquivos
        File file;
        fcArquivo = new JFileChooser(DQGG.pathApp);
        int returnVal = fcArquivo.showOpenDialog(this); 
        if (returnVal == JFileChooser.APPROVE_OPTION) {                
            file = fcArquivo.getSelectedFile();                
            //String caminhoRelativo = Utils.getRelativePath(file, new File(DQGG.pathApp));            
            DQGG.bd.batchInsSinais(file);
        }        
    }//GEN-LAST:event_menuImpSinaisActionPerformed

    /* Utilizado para realizar a importação em massa de ALOQUIROS a partir de um arquivo de texto
     * ( CUIDADO!!! )
     */
    private void menuImpTQuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuImpTQuirActionPerformed
       JFileChooser fcArquivo;   // Janela (dialog) para selecao de arquivos
        File file;
        fcArquivo = new JFileChooser(DQGG.pathApp);
        int returnVal = fcArquivo.showOpenDialog(this); 
        if (returnVal == JFileChooser.APPROVE_OPTION) {                
            file = fcArquivo.getSelectedFile();                            
            //DQGG.bd.batchInsAloquiros(file);
            DQGG.bd.listInsAloquiros(file);
        }        
    }//GEN-LAST:event_menuImpTQuirActionPerformed

    /* Utilizado para APAGAR todos os Sinais da base de dados
     * ( CUIDADO!!! )
     */
    private void menuDelSinaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDelSinaisActionPerformed
        int opcao = JOptionPane.showConfirmDialog(this,
                                              "Confirmar a exclusão de TODOS os registros?",
                                              "CONFIRMAÇÃO",
                                              JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION)         
            DQGG.bd.exclui("DELETE FROM REPRESENTACAO");
    }//GEN-LAST:event_menuDelSinaisActionPerformed

    /* Quirema anterior selecionado para utilizar na busca */
    private void AntQuir()
    {
        if (this.indiceAloquiro > 0)
        {
            this.indiceAloquiro--;            
            this.aloqAtual = this.aloqSelecionados.get(indiceAloquiro);
            this.atualizaAloq();
        }        
    }
    
    /* Próximo Quirema selecionado para utilizar na busca */
    private void ProxQuir()
    {
        if (this.indiceAloquiro < this.aloqSelecionados.size()-1)
        {
            this.indiceAloquiro++;            
            this.aloqAtual = this.aloqSelecionados.get(indiceAloquiro);
            this.atualizaAloq();
        }   
    }
   
    /* Realiza a busca alfabética */
    private void btBuscaAlfaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscaAlfaActionPerformed
        if (!this.txtBuscaAlfa.getText().equals(""))    
        {
            buscaSinalAlfa(this.txtBuscaAlfa.getText());        
            atualizaSinalBA();                
        }
    }//GEN-LAST:event_btBuscaAlfaActionPerformed

    /* Faz a busca pelo Aloquiros selecionados */
    private void btLocalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLocalizarActionPerformed
        if (this.aloqSelecionados.isEmpty())
        {            
            JOptionPane.showMessageDialog(this,"Selecione pelo menos um elemento para a busca.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);
        }
        else
        {
            this.listaSinais = new Sinal().buscaPorAloquiros(this.aloqSelecionados);
        }
        
        if (!this.listaSinais.isEmpty())
        {         
            //Collections.sort(listaSinais);
            
            this.TableNomes.setModel(new SinaisTableModel(listaSinais));            
            SinaisTableModel model = (SinaisTableModel) TableNomes.getModel();            
            
            model.fireTableDataChanged();
            this.labelTotal.setText(String.valueOf(this.listaSinais.size()));            
            this.ordenaColuna(TableNomes);  
            this.ocultaColunas(TableNomes, 0);            
            
            sinalAtual = this.listaSinais.get(0);
            this.atualizaSinal();
            this.PanelTabs.setSelectedComponent(this.TabSinais);
            this.TableNomes.changeSelection(0, 0, false, false);
            this.TableNomes.requestFocus();
        }
        else
        {
            JOptionPane.showMessageDialog(this,"Não foram encontrados sinais para esse conjuunto de elementos.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btLocalizarActionPerformed

    /* Limpa lista de Aloquiros selecionados */
    private void btLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLimparActionPerformed
        if (!this.aloqSelecionados.isEmpty())
        {        
            this.aloqSelecionados = new ArrayList<Aloquiro>();
            TableAloqs.setModel(new AloqTableModel(this.aloqSelecionados));
            this.aloqAtual = null;
            this.indiceAloquiro = -1;
            this.atualizaAloq();
            // Busca todos os sinais

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            this.listaSinais = new Sinal().buscaTodos();

            if (!this.listaSinais.isEmpty())
            {         
                Collections.sort(listaSinais);
                this.TableNomes.setModel(new SinaisTableModel(listaSinais));            
                this.labelTotal.setText(String.valueOf(this.listaSinais.size()));            
                this.ordenaColuna(TableNomes);  
                this.ocultaColunas(TableNomes, 0);

                sinalAtual = this.listaSinais.get(0);
                this.atualizaSinal();
                this.setCursor(Cursor.getDefaultCursor());

                this.PanelTabs.setSelectedComponent(this.TabSinais);
                this.TableNomes.changeSelection(0, 0, false, false);
                this.TableNomes.requestFocus();
            }
            else
            {
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this,"Não foram encontrados sinais na base de dados.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btLimparActionPerformed
    
    private void btQuirProxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btQuirProxActionPerformed
        this.ProxQuir();
    }//GEN-LAST:event_btQuirProxActionPerformed

    private void btQuirAntActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btQuirAntActionPerformed
        this.AntQuir();
    }//GEN-LAST:event_btQuirAntActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        if (TableAloqs.getModel().getRowCount() > 0)
        {
            indiceAloquiro = TableAloqs.getSelectedRow();                  
            this.unselecAloquiro(indiceAloquiro);
        }
    }//GEN-LAST:event_btExcluirActionPerformed

    private void menuSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSairActionPerformed
        this.dispose();
    }//GEN-LAST:event_menuSairActionPerformed

    private void menuInsQuirIniciaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuInsQuirIniciaisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuInsQuirIniciaisActionPerformed

    private void menuDelTQuirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDelTQuirActionPerformed
        String linhaSQL;
        linhaSQL = "DELETE FROM ALOQUIRO";
        DQGG.bd.exclui(linhaSQL);       
    }//GEN-LAST:event_menuDelTQuirActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        FormLista lsQuirema = new FormLista("SELECT * FROM SEMATOSEMA");
        lsQuirema.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        FormLista lsCateg = new FormLista("SELECT * FROM CATEGORIA");
        lsCateg.setVisible(true); 
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        FormLista lsAloquiro = new FormLista("SELECT * FROM ALOQUIRO");
        lsAloquiro.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        FormLista lsSinal = new FormLista("SELECT ID, NOME, IMG_REPRES, IMG_SINAL, MOVIMENTOS, DESCRICAO FROM REPRESENTACAO");
        lsSinal.setVisible(true);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void txtBuscaAlfaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscaAlfaActionPerformed
            // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscaAlfaActionPerformed

    private void btBuscaAlfaTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btBuscaAlfaTodosActionPerformed
        txtBuscaAlfa.setText("");
        buscaSinalAlfa("");        
        atualizaSinalBA();
        
    }//GEN-LAST:event_btBuscaAlfaTodosActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
       // System.out.println(this.getSize().toString());
    }//GEN-LAST:event_formComponentResized

    private void menuSinal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSinal1ActionPerformed
        frmListaSinal = new FormListSinal();
        frmListaSinal.setVisible(true);         
    }//GEN-LAST:event_menuSinal1ActionPerformed

    private void menuBases2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuBases2MouseClicked
        FormAbout frmAbout = new FormAbout();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int posX = (int) (screenSize.width - frmAbout.getSize().getWidth())/2;
        int posY = (int) (screenSize.height - frmAbout.getSize().getHeight())/2;
        
        frmAbout.setLocation(posX, posY);
        frmAbout.setVisible(true);
    }//GEN-LAST:event_menuBases2MouseClicked

    /* Busca de alfabética de sinais */
    private void buscaSinalAlfa(String s)            
    {  
      Sinal sinalProcurado = null; 
        
      this.TabBuscaAlfa.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (s.isEmpty())
        {
            listaSinaisBA = new Sinal().buscaTodos();
        }
        else 
        {
            if (s.length() < 3)
                JOptionPane.showMessageDialog(this,"Essa busca poderá retornar muitos registros. \n Tente uma busca mais precisa.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);
                        
            //listaSinaisBA = new Sinal().buscaAlfabetica(s);            
            sinalProcurado = new Sinal().buscaAlfabeticaOcorr1(s);
        }            
        
        if (listaSinaisBA.isEmpty())
        {            
            JOptionPane.showMessageDialog(this,"Não foram encontrados sinais com esse critério de busca.","ATENÇÃO!",JOptionPane.WARNING_MESSAGE);    
            
            TableNomesBA.selectAll();
            TableNomesBA.clearSelection();
            this.listaSinaisBA = new ArrayList<Sinal>();
            SinaisTableModel stm = new SinaisTableModel(this.listaSinaisBA);            
            this.TableNomesBA.setModel(stm);
            
            this.sinalAtualBA = null;
            this.atualizaSinalBA();          
            this.labelTotalBA.setText(String.valueOf(this.listaSinaisBA.size()));            
            
            this.TabBuscaAlfa.setCursor(Cursor.getDefaultCursor());
            this.PanelTabs.setSelectedComponent(this.TabBuscaAlfa);           
            this.TableNomesBA.requestFocus();
        }
        else
        {
         /* Collections.sort(listaSinaisBA);
            this.TableNomesBA.setModel(new SinaisTableModel(listaSinaisBA)); 
            this.labelTotalBA.setText(String.valueOf(this.listaSinaisBA.size()));
            this.ordenaColuna(TableNomesBA);
            */
            if (sinalProcurado != null)
                this.sinalAtualBA = sinalProcurado; 
            else
                this.sinalAtualBA = listaSinaisBA.get(0); 
            
            /*this.PanelTabs.setSelectedComponent(this.TabBuscaAlfa);
            this.TableNomesBA.changeSelection(0, 0, false, false);
            this.TableNomesBA.requestFocus();*/
        }
      this.TabBuscaAlfa.setCursor(Cursor.getDefaultCursor());
    }
    
    /* Atualiza a visualizacao do sinal selecionado */
    private void atualizaSinal()
    {
        if (sinalAtual != null)
        {            
            // Atualiza os valores dos campos de texto
            this.TextDescricao.setText(sinalAtual.getDescricao());
            PanelDescricao.getViewport().setViewPosition(new Point(0,0));
            this.LabelRepresentacao.setIcon(Utils.getImagem(sinalAtual.getArquivo()));            
            this.LabelSinais.setIcon(Utils.getImagem(sinalAtual.getArquivoSinais()));             
        }
    }
    
    /* Atualiza a visualizacao do sinal selecionado na tab da Busca Alfabética */
    private void atualizaSinalBA() 
    {         
        SinaisTableModel stm;
        
        int indiceBA = 0;
        stm = (SinaisTableModel) this.TableNomesBA.getModel();
                        
        if ((stm != null)&&(sinalAtualBA != null))
        {
            indiceBA = stm.getRowNumbyId(sinalAtualBA.getId());
            // Atualiza os valores dos campos de texto            
            this.TextDescricaoBA.setText(sinalAtualBA.getDescricao());
            PanelDescricao1.getViewport().setViewPosition(new Point(0,0));
            this.LabelRepresentacaoBA.setIcon(Utils.getImagem(sinalAtualBA.getArquivo()));                
            this.LabelSinaisBA.setIcon(Utils.getImagem(sinalAtualBA.getArquivoSinais()));  
            this.TableNomesBA.getSelectionModel().setSelectionInterval(indiceBA, indiceBA);            
            this.TableNomesBA.changeSelection(indiceBA, 0, false, false);
            this.TableNomesBA.requestFocus();            
        } else if ((stm != null)&&(sinalAtualBA == null))
        {            
            this.TextDescricaoBA.setText("");        
            this.LabelRepresentacaoBA.setIcon(null);                
            this.LabelSinaisBA.setIcon(null);
        }        
    }
    
    /* Atualiza os dados do Aloquiro selecionado (não visual) */
    private void atualizaAloq()
    {
        if (aloqAtual != null) 
        {
            this.TableAloqs.getSelectionModel().setSelectionInterval(indiceAloquiro, indiceAloquiro);
            this.TextDescQuirema.setText(this.aloqAtual.getDescricao());
            this.LabelQuirema.setIcon(Utils.getImagem(this.aloqAtual.getImagem()));                 
        }
        else
        {
            this.TextDescQuirema.setText("");
            this.LabelQuirema.setIcon(null);
        }                
    }
    
    /* Seleciona um Aloquiro */
    public void selecAloquiro(Aloquiro aloq)
    {        
        AloqTableModel atm = (AloqTableModel) this.TableAloqs.getModel();
        atm.addRow(aloq);         
        this.aloqAtual = aloq;        
        this.indiceAloquiro =  this.aloqSelecionados.size()-1;        
        this.atualizaAloq();        
    }
    
    /* Deseleciona um Aloquiro */
    public void unselecAloquiro(int ind)
    {       
       AloqTableModel atm = (AloqTableModel) this.TableAloqs.getModel();
       atm.removeRow(ind);     
       if (ind > 0)
       {          
         this.indiceAloquiro--;  
         this.aloqAtual = this.aloqSelecionados.get(this.indiceAloquiro); 
         this.atualizaAloq();         
       }
       else if (ind == 0)
       {
         this.indiceAloquiro=0;
         if (this.aloqSelecionados.size() > 0)
             this.aloqAtual = this.aloqSelecionados.get(0); 
         else             
            this.aloqAtual = new Aloquiro();
         this.atualizaAloq();  
       }
    }    
      
    @Override
    public void mousePressed(MouseEvent e) 
    {
        Object source = e.getSource().getClass().getName();
        if ((source == "javax.swing.JLabel")&&(e.getClickCount()==1))
        { 
           JLabel j = (JLabel) e.getSource();
           String nome = j.getName();
           
           if ((nome != null) && (nome.equals("LabelQuirema")))  // se refere aos aloquiros selecionados
           {
                 indiceAloquiro = TableAloqs.getSelectedRow();                  
                 this.unselecAloquiro(indiceAloquiro); 
           }
           else   // se refere aos aloquiros a selecionar
           {
                PanelQuirema pq = (PanelQuirema) ((JPanel)((JPanel) j.getParent()).getParent()).getParent();
                this.selecAloquiro(pq.getAloquiroAtual());
           }           
        }        	            
    }
	
    @Override
    public void mouseReleased(MouseEvent e)
    {}
    @Override
    public void mouseEntered(MouseEvent e) 
    {}
    @Override
    public void mouseExited(MouseEvent e) 
    {}
    @Override
    public void mouseClicked(MouseEvent e) 
    {}
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();        
        if (notches < 0) 
        {
            if (this.indiceAloquiro > 0)
            {
                this.indiceAloquiro--;                
                this.aloqAtual = this.aloqSelecionados.get(indiceAloquiro);
                this.atualizaAloq();
            }
        } else {            
            if (this.indiceAloquiro < this.aloqSelecionados.size()-1)
            {
                this.indiceAloquiro++;            
                this.aloqAtual = this.aloqSelecionados.get(indiceAloquiro);
                this.atualizaAloq();
            }    
        }           
    }
    
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
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelQuirema;
    private javax.swing.JLabel LabelRepresentacao;
    private javax.swing.JLabel LabelRepresentacaoBA;
    private javax.swing.JLabel LabelSinais;
    private javax.swing.JLabel LabelSinaisBA;
    private javax.swing.JPanel PanelDescSelecionadas;
    private javax.swing.JScrollPane PanelDescricao;
    private javax.swing.JScrollPane PanelDescricao1;
    private javax.swing.JScrollPane PanelNomes;
    private javax.swing.JScrollPane PanelNomes1;
    private javax.swing.JPanel PanelQuiremas;
    private javax.swing.JPanel PanelRepresentacao;
    private javax.swing.JPanel PanelRepresentacao2;
    private javax.swing.JPanel PanelSelecionadas;
    private javax.swing.JPanel PanelSinais;
    private javax.swing.JPanel PanelSinais1;
    private javax.swing.JTabbedPane PanelTabs;
    private javax.swing.JPanel TabBuscaAlfa;
    private javax.swing.JPanel TabSinais;
    private javax.swing.JTable TableAloqs;
    private javax.swing.JTable TableNomes;
    private javax.swing.JTable TableNomesBA;
    private javax.swing.JTextPane TextDescQuirema;
    private javax.swing.JTextPane TextDescricao;
    private javax.swing.JTextPane TextDescricaoBA;
    private java.util.ArrayList<Aloquiro> arrayList1;
    private javax.swing.JButton btBuscaAlfa;
    private javax.swing.JButton btBuscaAlfaTodos;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btLimpar;
    private javax.swing.JButton btLocalizar;
    private javax.swing.JButton btQuirAnt;
    private javax.swing.JButton btQuirProx;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JLabel labelTotalBA;
    private javax.swing.JMenuItem menuAloquiro;
    private javax.swing.JMenu menuBases;
    private javax.swing.JMenu menuBases1;
    private javax.swing.JMenu menuBases2;
    private javax.swing.JMenuItem menuCategoria;
    private javax.swing.JMenuItem menuDelSinais;
    private javax.swing.JMenuItem menuDelTQuir;
    private javax.swing.JMenuItem menuImpSinais;
    private javax.swing.JMenuItem menuImpTQuir;
    private javax.swing.JMenuItem menuInsQuirIniciais;
    private javax.swing.JMenuBar menuPrincipal;
    private javax.swing.JMenuItem menuQuirema;
    private javax.swing.JMenuItem menuSair;
    private javax.swing.JMenuItem menuSinal;
    private javax.swing.JMenuItem menuSinal1;
    private javax.swing.JPanel panelImgSelec;
    private javax.swing.JTextField txtBuscaAlfa;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables


}
