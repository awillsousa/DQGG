package dqgg;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import dqgg.FormListSinal;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.text.DefaultCaret;

/**
 * Formulário dos Sinais
 * @author AntonioSousa
 */
public class FormSinal extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    JFileChooser fcImagem;   // Janela (dialog) para selecao de arquivos de imagem    
    BD dqggDB;
    Sinal repAtual = new Sinal();   // Registro atualmente exibido
    HashMap filtroConsulta = new HashMap();        // Filtro de consulta atual
    ArrayList<Sinal> repsConsulta = new ArrayList<Sinal>();        // registros retornado pela consulta corrente
    ArrayList<Sinal> listaSinais = new ArrayList<Sinal>();        // registros retornado pela consulta corrente
    int indiceConsulta = 0;
    final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    FormListSinal formListaSinais = null;
    private static final int TEMPO_NAVEGACAO = 250;
    private boolean mouseDown = false;
    private boolean isRunning = false;
    private Timer timerAnt;
    private Timer timerProx;
    
    
    
    enum Modo {INCLUSAO, ALTERACAO, CONSULTA};
    Modo modoForm = Modo.ALTERACAO;
    
    /**
     * Creates new form FormSinal
     */
    public FormSinal() {
        initComponents(); 
        // Altera o autoscrolling do JTextPane. Evitando dele rolar para a última linha
        DefaultCaret caret = (DefaultCaret)txtDescricao.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);   
        
        File directory = new File(DQGG.pathImg + "img/");            
        fcImagem = new JFileChooser(directory);
        
        //Acrescenta um filtro para listar somente imagens  
        this.fcImagem.addChoosableFileFilter(new ImageFilter());
        this.fcImagem.setAcceptAllFileFilterUsed(false);

        //Adiciona icones customizados para os tipos de arquivos
        this.fcImagem.setFileView(new ImageFileView());

        //Adiciona o painel de visualizacao
        this.fcImagem.setAccessory(new ImagePreview(this.fcImagem));
                
        listaSinais = repAtual.buscaTodos();
        if (!listaSinais.isEmpty())
           repAtual = listaSinais.get(0);
        else
           repAtual = null;
                
        if (repAtual != null)
            this.atualizaFormulario(repAtual);
        else
            this.entraModoInclusao();   
        
        this.btCancela.setVisible(false);
        this.btGravar.setVisible(false);
        
        btAnterior.addMouseListener(new java.awt.event.MouseAdapter() {            
            
            @Override    
            public void mouseReleased(MouseEvent e) {                
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (timerAnt != null) 
                    {
                        timerAnt.stop();
                        JButton bt = (JButton) e.getSource();                        
                        bt.setSelected(false);                        
                    }
                }
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (timerAnt != null) 
                    {
                        timerAnt.stop();
                        JButton bt = (JButton) e.getSource();                        
                        bt.setSelected(false);                        
                    }
                }
            }            
        }); 
    }

    public FormSinal(Sinal sinal)
    {        
        initComponents(); 
        // Altera o autoscrolling do JTextPane. Evitando dele rolar para a última linha
        DefaultCaret caret = (DefaultCaret)txtDescricao.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);   
        
        File directory = new File(DQGG.pathImg + "img/");            
        fcImagem = new JFileChooser(directory);
        
        //Acrescenta um filtro para listar somente imagens  
        this.fcImagem.addChoosableFileFilter(new ImageFilter());
        this.fcImagem.setAcceptAllFileFilterUsed(false);

        //Adiciona icones customizados para os tipos de arquivos
        this.fcImagem.setFileView(new ImageFileView());

        //Adiciona o painel de visualizacao
        this.fcImagem.setAccessory(new ImagePreview(this.fcImagem));
                
        repAtual = sinal;
        if (repAtual != null)
            this.atualizaFormulario(repAtual);
        else
            this.entraModoInclusao();
        
        this.btAnterior.setVisible(false);
        this.btProximo.setVisible(false);
        this.btPrimeiro.setVisible(false);
        this.btUltimo.setVisible(false);
        this.btConfirmar.setVisible(false);
        this.btConsultar.setVisible(false);
        this.btExcluir.setVisible(false);
        this.btIncluir.setVisible(false);
        this.btCancelar.setVisible(false);
        this.btIncluir.setVisible(false);
        //this.PanelNavigator.setVisible(false);
        this.btCancela.setVisible(true);
        this.btGravar.setVisible(true);
        
    }
    
    public FormSinal(Sinal sinal, FormListSinal formLista)
    {        
        initComponents();  
        // Altera o autoscrolling do JTextPane. Evitando dele rolar para a última linha
        DefaultCaret caret = (DefaultCaret)txtDescricao.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);   
        
        File directory = new File(DQGG.pathImg + "img/");            
        fcImagem = new JFileChooser(directory);
        
        //Acrescenta um filtro para listar somente imagens  
        this.fcImagem.addChoosableFileFilter(new ImageFilter());
        this.fcImagem.setAcceptAllFileFilterUsed(false);

        //Adiciona icones customizados para os tipos de arquivos
        this.fcImagem.setFileView(new ImageFileView());

        //Adiciona o painel de visualizacao
        this.fcImagem.setAccessory(new ImagePreview(this.fcImagem));
                
        repAtual = sinal;
        if (repAtual != null)
            this.atualizaFormulario(repAtual);
        else
            this.entraModoInclusao();
        
        this.btAnterior.setVisible(false);
        this.btProximo.setVisible(false);
        this.btPrimeiro.setVisible(false);
        this.btUltimo.setVisible(false);
        this.btConfirmar.setVisible(false);
        this.btConsultar.setVisible(false);
        this.btExcluir.setVisible(false);
        this.btIncluir.setVisible(false);
        this.btCancelar.setVisible(false);
        this.btIncluir.setVisible(false);
        //this.PanelNavigator.setVisible(false);
        this.btCancela.setVisible(true);
        this.btGravar.setVisible(true);
        
        this.formListaSinais = formLista;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtNome = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMovimentos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        txtImgRep = new javax.swing.JTextField();
        btRepresentacao = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtImgSinal = new javax.swing.JTextField();
        btSinal = new javax.swing.JButton();
        PanelNavigator = new javax.swing.JPanel();
        btPrimeiro = new javax.swing.JButton();
        btAnterior = new javax.swing.JButton();
        btProximo = new javax.swing.JButton();
        btUltimo = new javax.swing.JButton();
        btIncluir = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        btConfirmar = new javax.swing.JButton();
        btCancelar = new javax.swing.JButton();
        btConsultar = new javax.swing.JButton();
        btGravar = new javax.swing.JButton();
        btCancela = new javax.swing.JButton();
        PanelSinal = new javax.swing.JPanel();
        ImgSinal = new javax.swing.JLabel();
        PanelRepres = new javax.swing.JPanel();
        ImgRepres = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DQSG(G) - Sinais");
        setAlwaysOnTop(true);
        setMaximumSize(new java.awt.Dimension(870, 750));
        setMinimumSize(new java.awt.Dimension(870, 750));
        setPreferredSize(new java.awt.Dimension(870, 750));
        setResizable(false);

        txtNome.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNome.setMaximumSize(new java.awt.Dimension(244, 23));
        txtNome.setMinimumSize(new java.awt.Dimension(244, 23));
        txtNome.setPreferredSize(new java.awt.Dimension(244, 23));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Nome:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Movimentos:");

        txtMovimentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMovimentos.setMaximumSize(new java.awt.Dimension(244, 23));
        txtMovimentos.setMinimumSize(new java.awt.Dimension(244, 23));
        txtMovimentos.setPreferredSize(new java.awt.Dimension(244, 23));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Descrição:");

        txtDescricao.setColumns(20);
        txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtDescricao.setLineWrap(true);
        txtDescricao.setRows(5);
        txtDescricao.setMaximumSize(new java.awt.Dimension(244, 90));
        txtDescricao.setMinimumSize(new java.awt.Dimension(244, 90));
        jScrollPane1.setViewportView(txtDescricao);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Representação:");

        txtImgRep.setEditable(false);
        txtImgRep.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtImgRep.setMaximumSize(new java.awt.Dimension(6, 23));
        txtImgRep.setPreferredSize(new java.awt.Dimension(69, 30));

        btRepresentacao.setText("Selecionar Imagem...");
        btRepresentacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRepresentacaoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Sinais:");

        txtImgSinal.setEditable(false);
        txtImgSinal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtImgSinal.setMaximumSize(new java.awt.Dimension(6, 23));
        txtImgSinal.setMinimumSize(new java.awt.Dimension(69, 30));
        txtImgSinal.setPreferredSize(new java.awt.Dimension(69, 30));

        btSinal.setText("Selecionar Imagem...");
        btSinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSinalActionPerformed(evt);
            }
        });

        PanelNavigator.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PanelNavigator.setAlignmentX(1.0F);
        PanelNavigator.setAlignmentY(1.0F);

        btPrimeiro.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btPrimeiro.setText("|<");
        btPrimeiro.setAlignmentX(0.5F);
        btPrimeiro.setMaximumSize(new java.awt.Dimension(53, 25));
        btPrimeiro.setMinimumSize(new java.awt.Dimension(53, 25));
        btPrimeiro.setPreferredSize(new java.awt.Dimension(53, 25));
        btPrimeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btPrimeiroActionPerformed(evt);
            }
        });

        btAnterior.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btAnterior.setText("<");
        btAnterior.setAlignmentX(0.5F);
        btAnterior.setMaximumSize(new java.awt.Dimension(53, 25));
        btAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btAnteriorMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btAnteriorMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btAnteriorMouseReleased(evt);
            }
        });
        btAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnteriorActionPerformed(evt);
            }
        });

        btProximo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btProximo.setText(">");
        btProximo.setAlignmentX(0.5F);
        btProximo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btProximoMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btProximoMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btProximoMouseReleased(evt);
            }
        });
        btProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btProximoActionPerformed(evt);
            }
        });

        btUltimo.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btUltimo.setText(">|");
        btUltimo.setAlignmentX(0.5F);
        btUltimo.setMaximumSize(new java.awt.Dimension(53, 25));
        btUltimo.setMinimumSize(new java.awt.Dimension(53, 25));
        btUltimo.setPreferredSize(new java.awt.Dimension(53, 25));
        btUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btUltimoActionPerformed(evt);
            }
        });

        btIncluir.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btIncluir.setText("Incluir");
        btIncluir.setAlignmentX(0.5F);
        btIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIncluirActionPerformed(evt);
            }
        });

        btExcluir.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btExcluir.setText("Excluir");
        btExcluir.setAlignmentX(0.5F);
        btExcluir.setMaximumSize(new java.awt.Dimension(75, 25));
        btExcluir.setMinimumSize(new java.awt.Dimension(75, 25));
        btExcluir.setPreferredSize(new java.awt.Dimension(75, 25));
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        btConfirmar.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btConfirmar.setText("Confirmar");
        btConfirmar.setAlignmentX(0.5F);
        btConfirmar.setMaximumSize(new java.awt.Dimension(75, 25));
        btConfirmar.setMinimumSize(new java.awt.Dimension(75, 25));
        btConfirmar.setPreferredSize(new java.awt.Dimension(75, 25));
        btConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConfirmarActionPerformed(evt);
            }
        });

        btCancelar.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btCancelar.setText("Cancelar");
        btCancelar.setAlignmentX(0.5F);
        btCancelar.setMaximumSize(new java.awt.Dimension(75, 25));
        btCancelar.setMinimumSize(new java.awt.Dimension(75, 25));
        btCancelar.setPreferredSize(new java.awt.Dimension(75, 25));
        btCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelarActionPerformed(evt);
            }
        });

        btConsultar.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btConsultar.setText("Consultar");
        btConsultar.setAlignmentX(0.5F);
        btConsultar.setMaximumSize(new java.awt.Dimension(75, 25));
        btConsultar.setMinimumSize(new java.awt.Dimension(75, 25));
        btConsultar.setPreferredSize(new java.awt.Dimension(75, 25));
        btConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btConsultarActionPerformed(evt);
            }
        });

        btGravar.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btGravar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/BD/check52.png"))); // NOI18N
        btGravar.setText("Gravar");
        btGravar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btGravarMouseClicked(evt);
            }
        });
        btGravar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btGravarActionPerformed(evt);
            }
        });

        btCancela.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        btCancela.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/BD/delete30.png"))); // NOI18N
        btCancela.setText("Cancelar");
        btCancela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCancelaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelNavigatorLayout = new javax.swing.GroupLayout(PanelNavigator);
        PanelNavigator.setLayout(PanelNavigatorLayout);
        PanelNavigatorLayout.setHorizontalGroup(
            PanelNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelNavigatorLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btPrimeiro, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btProximo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCancela, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelNavigatorLayout.setVerticalGroup(
            PanelNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelNavigatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(btPrimeiro, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btProximo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btIncluir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btCancela, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btGravar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        PanelSinal.setBackground(new java.awt.Color(255, 255, 255));

        ImgSinal.setBackground(new java.awt.Color(255, 255, 255));
        ImgSinal.setForeground(new java.awt.Color(255, 255, 255));
        ImgSinal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ImgSinal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ImgSinal.setMaximumSize(new java.awt.Dimension(244, 180));
        ImgSinal.setMinimumSize(new java.awt.Dimension(244, 180));

        javax.swing.GroupLayout PanelSinalLayout = new javax.swing.GroupLayout(PanelSinal);
        PanelSinal.setLayout(PanelSinalLayout);
        PanelSinalLayout.setHorizontalGroup(
            PanelSinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ImgSinal, javax.swing.GroupLayout.DEFAULT_SIZE, 694, Short.MAX_VALUE)
        );
        PanelSinalLayout.setVerticalGroup(
            PanelSinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelSinalLayout.createSequentialGroup()
                .addComponent(ImgSinal, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        PanelRepres.setBackground(new java.awt.Color(255, 255, 255));

        ImgRepres.setBackground(new java.awt.Color(255, 255, 255));
        ImgRepres.setForeground(new java.awt.Color(255, 255, 255));
        ImgRepres.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ImgRepres.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ImgRepres.setMaximumSize(new java.awt.Dimension(244, 180));
        ImgRepres.setMinimumSize(new java.awt.Dimension(244, 180));

        javax.swing.GroupLayout PanelRepresLayout = new javax.swing.GroupLayout(PanelRepres);
        PanelRepres.setLayout(PanelRepresLayout);
        PanelRepresLayout.setHorizontalGroup(
            PanelRepresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ImgRepres, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
        );
        PanelRepresLayout.setVerticalGroup(
            PanelRepresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ImgRepres, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtMovimentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtImgRep, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btRepresentacao))
                                .addComponent(PanelRepres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(PanelSinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtImgSinal, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btSinal))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(PanelNavigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovimentos, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtImgRep, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRepresentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelRepres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtImgSinal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btSinal, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelSinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelNavigator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void entraModoConsulta()
    {        
        this.modoForm = Modo.CONSULTA;
        this.limpaCampos();
        // Altera a cor dos campos 
        this.txtNome.setBackground(DQGG.corConsulta);
        this.txtMovimentos.setBackground(DQGG.corConsulta);
        this.txtDescricao.setBackground(DQGG.corConsulta);
        this.txtImgRep.setBackground(DQGG.corDesabilitado);
        this.txtImgSinal.setBackground(DQGG.corDesabilitado);
    }
    
    private void entraModoAlteracao()
    {
        this.modoForm = Modo.ALTERACAO;
        // Altera a cor dos campos 
        this.txtNome.setBackground(DQGG.corAlteracao);
        this.txtMovimentos.setBackground(DQGG.corAlteracao);
        this.txtDescricao.setBackground(DQGG.corAlteracao);
        this.txtImgRep.setBackground(DQGG.corAlteracao);
        this.txtImgSinal.setBackground(DQGG.corAlteracao);
    }
    
    private void entraModoInclusao()
    {
        this.modoForm = Modo.INCLUSAO;
        this.limpaCampos();
        // Altera a cor dos campos 
        this.txtNome.setBackground(DQGG.corInsercao);
        this.txtMovimentos.setBackground(DQGG.corInsercao);
        this.txtDescricao.setBackground(DQGG.corInsercao);
        this.txtImgRep.setBackground(DQGG.corInsercao);
        this.txtImgSinal.setBackground(DQGG.corInsercao);
    }
    
    private void atualizaFormulario(Sinal rep)
    {
        // Atualiza os valores dos campos de texto
        this.txtNome.setText(rep.getNome());
        this.txtMovimentos.setText(rep.getMovimentos());
        this.txtDescricao.setText(rep.getDescricao());
        this.jScrollPane1.getViewport().setViewPosition(new Point(0,0));
        this.txtImgRep.setText(rep.getArquivo());
        this.txtImgSinal.setText(rep.getArquivoSinais());     
        // Redesenha as imagens        
        this.ImgRepres.setIcon(Utils.getImagem(rep.getArquivo()));
        this.ImgSinal.setIcon(Utils.getImagem(rep.getArquivoSinais()));        
    }
    
    private void limpaCampos()
    {
        // Atualiza os valores dos campos de texto
        this.txtNome.setText("");
        this.txtMovimentos.setText("");
        this.txtDescricao.setText("");
        this.txtImgRep.setText("");
        this.txtImgSinal.setText("");
        // Redesenha as imagens
        this.ImgRepres.setIcon(null);
        this.ImgSinal.setIcon(null);
    }
    
    private void btRepresentacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRepresentacaoActionPerformed
        //Clique do botão "Selecionar Imagem..." (campo Representação)
        if ((evt.getSource() == this.btRepresentacao)&&(this.modoForm != Modo.CONSULTA)) {
            int returnVal = this.fcImagem.showOpenDialog(this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {                
                File file = this.fcImagem.getSelectedFile();                
                String caminhoRelativo = Utils.getRelativePath(file, new File(DQGG.pathApp));                
                this.txtImgRep.setText(caminhoRelativo);
                this.ImgRepres.setIcon(new javax.swing.ImageIcon(file.getPath()));
            }   
        }        
    }//GEN-LAST:event_btRepresentacaoActionPerformed

    private void btSinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSinalActionPerformed
        //Clique do botão "Selecionar Imagem..." (campo Sinais)
        if ((evt.getSource() == this.btSinal)&&(this.modoForm != Modo.CONSULTA)) {
            int returnVal = this.fcImagem.showOpenDialog(this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.fcImagem.getSelectedFile();
                String caminhoRelativo = Utils.getRelativePath(file, new File(DQGG.pathApp));                
                this.txtImgSinal.setText(caminhoRelativo);
                this.ImgSinal.setIcon(new javax.swing.ImageIcon(file.getPath()));                 
            } 
        }
    }//GEN-LAST:event_btSinalActionPerformed

    private void btConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConfirmarActionPerformed
        HashMap camposAlterados = new HashMap();        
                
        if (this.modoForm == Modo.INCLUSAO)
        {
            // Valida os campos 
            if (this.txtNome.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Nome obrigatório!","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtMovimentos.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Movimentos obrigatório!","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtDescricao.getText().isEmpty())            
            {                
                JOptionPane.showMessageDialog(this,"Campo Descrição obrigatório!","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Insere a representacao no banco
            Sinal rep = new Sinal(0, this.txtNome.getText(), this.txtMovimentos.getText(), this.txtDescricao.getText(), this.txtImgRep.getText(), this.txtImgSinal.getText());            
            rep.insereBD();
            this.entraModoAlteracao();
        
            //Navega para o registro incluido
            Sinal r = this.repAtual.buscaUltimo();            
            this.atualizaFormulario(repAtual=r);
            
        } else if (this.modoForm == Modo.CONSULTA) {
            if (this.repsConsulta.isEmpty())
            {   
                // Verifica os campos que foram preenchidos            
                if (!this.txtNome.getText().isEmpty())           
                    this.filtroConsulta.put("NOME", this.txtNome.getText());

                if (!this.txtMovimentos.getText().isEmpty())            
                    this.filtroConsulta.put("MOVIMENTOS", this.txtMovimentos.getText());            

                if (!this.txtDescricao.getText().isEmpty())                                        
                    this.filtroConsulta.put("DESCRICAO", this.txtDescricao.getText());

                if (this.filtroConsulta.isEmpty()) 
                {
                   JOptionPane.showMessageDialog(this,"Não há parâmetros para busca!","ALERTA",JOptionPane.INFORMATION_MESSAGE);               
                }
                else
                {
                   this.repsConsulta = this.repAtual.consultaBD(this.filtroConsulta);    
                   this.repAtual = this.repsConsulta.get(0);    // pega o primeiro registro da consulta
                   indiceConsulta = 0;
                   this.atualizaFormulario(repAtual);
                }
            }
            
        } else if (this.modoForm == Modo.ALTERACAO) {
            // Valida os campos 
            if (this.txtNome.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Nome obrigatório!.","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtMovimentos.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Movimentos obrigatório!.","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtDescricao.getText().isEmpty())            
            {                
                JOptionPane.showMessageDialog(this,"Campo Descrição obrigatório!.","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verifica os campos que foram alterados            
            if (!this.txtNome.getText().equals(this.repAtual.getNome()))           
                camposAlterados.put("NOME", this.txtNome.getText());
            
            if (!this.txtMovimentos.getText().equals(this.repAtual.getMovimentos()))            
                camposAlterados.put("MOVIMENTOS", this.txtMovimentos.getText());            
            
            if (!this.txtDescricao.getText().equals(this.repAtual.getDescricao()))                                        
                camposAlterados.put("DESCRICAO", this.txtDescricao.getText());
            
            if (!this.txtImgRep.getText().equals(this.repAtual.getArquivo()))                            
                camposAlterados.put("IMG_REPRES", this.txtImgRep.getText());            
            
            if (!this.txtImgSinal.getText().equals(this.repAtual.getArquivoSinais()))                        
                camposAlterados.put("IMG_SINAL", this.txtImgSinal.getText());            
            
            if (camposAlterados.isEmpty()) 
            {
               JOptionPane.showMessageDialog(this,"Não há alterações a serem salvas!.","ALERTA",JOptionPane.INFORMATION_MESSAGE);               
            }
            else
            {
               if (this.repAtual.atualizaBD(camposAlterados))                    
                   JOptionPane.showMessageDialog(this,"Dados alterados com sucesso!");
               else
                   JOptionPane.showMessageDialog(this,"Erro durante a atualização de dados!","ERRO",JOptionPane.ERROR_MESSAGE);
            }                
        }
        
        
    }//GEN-LAST:event_btConfirmarActionPerformed

    private void btPrimeiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btPrimeiroActionPerformed
        if (this.modoForm == Modo.ALTERACAO)    // vai para o primeiro registro da tabela
        {
            Sinal r = listaSinais.get(0);
            if (r != null)
                this.atualizaFormulario(repAtual=r);
        }
        else if ((this.modoForm == Modo.CONSULTA)&&(!this.repsConsulta.isEmpty()))    // vai para o primeiro registro da consulta
        {
            this.repAtual = this.repsConsulta.get(0);
            this.atualizaFormulario(repAtual);
        }
    }//GEN-LAST:event_btPrimeiroActionPerformed

    private void btUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btUltimoActionPerformed
        if (this.modoForm == Modo.ALTERACAO)
        {            
            Sinal r = listaSinais.get(listaSinais.size()-1);
            
            if (r != null)
                this.atualizaFormulario(repAtual=r);
        }
        else if ((this.modoForm == Modo.CONSULTA)&&(!this.repsConsulta.isEmpty()))    // vai para o ultimo registro da consulta
        {
            indiceConsulta = this.repsConsulta.size()-1;
            this.repAtual = this.repsConsulta.get(indiceConsulta);
            this.atualizaFormulario(repAtual);
        }
    }//GEN-LAST:event_btUltimoActionPerformed

    private void anteriorRegistro() {
        if (this.modoForm == Modo.ALTERACAO)
        {
            int indxAtual = -1;        
            for (Sinal s : listaSinais) {
                if (s.getId() == repAtual.getId()) {
                    indxAtual = listaSinais.indexOf(s);                
                    break;
                }
            }

            if (indxAtual > 0) {                
                repAtual = listaSinais.get(indxAtual-1);
                this.atualizaFormulario(repAtual);            
            }            
        }
        else if ((this.modoForm == Modo.CONSULTA)&&(!this.repsConsulta.isEmpty()))    // vai para o registro anterior da consulta
        {
            if (indiceConsulta > 0) 
                indiceConsulta--;
            this.repAtual = this.repsConsulta.get(indiceConsulta);
            this.atualizaFormulario(repAtual);
        }
    }
    
    private void btAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAnteriorActionPerformed
        anteriorRegistro();
    }//GEN-LAST:event_btAnteriorActionPerformed
    
    private void btProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btProximoActionPerformed
        //proxRegistro();        
    }//GEN-LAST:event_btProximoActionPerformed

    private void btIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIncluirActionPerformed
        if (this.modoForm == Modo.CONSULTA)   //Limpa os registros e o filtro de consulta
        {              
                 this.repsConsulta.clear();           
                 this.filtroConsulta.clear();    
        }        
        this.entraModoInclusao();        
    }//GEN-LAST:event_btIncluirActionPerformed

    private void btCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelarActionPerformed
           if (this.modoForm == Modo.CONSULTA)   //Limpa os registros e o filtro de consulta
           {              
                    this.repsConsulta.clear();           
                    this.filtroConsulta.clear();    
           }
            this.entraModoAlteracao();
            if (this.repAtual == null)  // consulta que não retornou nenhum registro
            {
                this.repAtual = this.repAtual.buscaPrimeiro();                
            }
            this.atualizaFormulario(repAtual);       
    }//GEN-LAST:event_btCancelarActionPerformed

    private void btConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btConsultarActionPerformed
        if (this.modoForm == Modo.CONSULTA)
        {            
                this.repsConsulta.clear();
                this.filtroConsulta.clear();
            
        }
        this.entraModoConsulta();        
    }//GEN-LAST:event_btConsultarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        if (this.modoForm != Modo.ALTERACAO)
            return;
        
        int opcao = JOptionPane.showConfirmDialog(this,
                                              "Confirmar exclusão do registro?",
                                              "CONFIRMAÇÃO",
                                              JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) 
        {
           // Pega o registro anterior ao atual
           Sinal rep = this.repAtual.buscaAnterior(repAtual);
           if (rep == null)
               rep = this.repAtual.buscaProximo(repAtual);
                      
           if (this.repAtual.exclui()){
               JOptionPane.showMessageDialog(this,"Dados alterados com sucesso!");               
               this.atualizaFormulario(this.repAtual=rep);
           } else {
               JOptionPane.showMessageDialog(this,"Erro durante a esclusão de dados!","ERRO",JOptionPane.ERROR_MESSAGE);
           }
        }
        
    }//GEN-LAST:event_btExcluirActionPerformed

    private void proxRegistro() {
        if (this.modoForm == Modo.ALTERACAO)
        {
            int indxAtual = listaSinais.indexOf(repAtual);        
            for (Sinal s : listaSinais) {
                if (s.getId() == repAtual.getId()) {
                    indxAtual = listaSinais.indexOf(s);                
                    break;
                }                    
            }

            if (indxAtual < listaSinais.size()-1) {                   
                repAtual = listaSinais.get(indxAtual+1);                        
                this.atualizaFormulario(repAtual);
            }
        }
        else if ((this.modoForm == Modo.CONSULTA)&&(!this.repsConsulta.isEmpty()))    // vai para o registro anterior da consulta
        {
            if (indiceConsulta < (this.repsConsulta.size()-1)) 
                indiceConsulta++;
            this.repAtual = this.repsConsulta.get(indiceConsulta);
            this.atualizaFormulario(repAtual);
        }        
    }
    
    private void btProximoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btProximoMouseClicked
        //proxRegistro();
    }//GEN-LAST:event_btProximoMouseClicked

    private void btProximoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btProximoMousePressed
               if (evt.getButton() == MouseEvent.BUTTON1) {                    
                    ActionListener action = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {                                                            
                             proxRegistro();                             
                        }                        
                    };                    
                    timerProx = new Timer(TEMPO_NAVEGACAO, action);                    
                    timerProx.start();
                }       
    }//GEN-LAST:event_btProximoMousePressed

    private void btProximoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btProximoMouseReleased
        if (evt.getButton() == MouseEvent.BUTTON1) {
                    if (timerProx != null) 
                    {                        
                       if (!btProximo.getModel().isEnabled())
                            btProximo.getModel().setEnabled(false); 
                        
                        timerProx.setRepeats(false);
                        JButton bt = (JButton) evt.getSource();                        
                        bt.setSelected(false);                        
                    }
                }
    }//GEN-LAST:event_btProximoMouseReleased

    private void btGravarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btGravarActionPerformed
       HashMap camposAlterados = new HashMap();        
                
        if (this.modoForm == Modo.INCLUSAO)
        {
            // Valida os campos 
            if (this.txtNome.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Nome obrigatório!","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtMovimentos.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Movimentos obrigatório!","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtDescricao.getText().isEmpty())            
            {                
                JOptionPane.showMessageDialog(this,"Campo Descrição obrigatório!","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Insere a representacao no banco
            Sinal rep = new Sinal(0, this.txtNome.getText(), this.txtMovimentos.getText(), this.txtDescricao.getText(), this.txtImgRep.getText(), this.txtImgSinal.getText());            
            rep.insereBD();
            this.entraModoAlteracao();
        
            //Navega para o registro incluido
            Sinal r = this.repAtual.buscaUltimo();            
            this.atualizaFormulario(repAtual=r);
            
            this.formListaSinais.atualizaTabela();
        } else if (this.modoForm == Modo.ALTERACAO) {
            // Valida os campos 
            if (this.txtNome.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Nome obrigatório!.","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtMovimentos.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Campo Movimentos obrigatório!.","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (this.txtDescricao.getText().isEmpty())            
            {                
                JOptionPane.showMessageDialog(this,"Campo Descrição obrigatório!.","ERRO",JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verifica os campos que foram alterados            
            if (!this.txtNome.getText().equals(this.repAtual.getNome()))           
                camposAlterados.put("NOME", this.txtNome.getText());
            
            if (!this.txtMovimentos.getText().equals(this.repAtual.getMovimentos()))            
                camposAlterados.put("MOVIMENTOS", this.txtMovimentos.getText());            
            
            if (!this.txtDescricao.getText().equals(this.repAtual.getDescricao()))                                        
                camposAlterados.put("DESCRICAO", this.txtDescricao.getText());
            
            if (!this.txtImgRep.getText().equals(this.repAtual.getArquivo()))                            
                camposAlterados.put("IMG_REPRES", this.txtImgRep.getText());            
            
            if (!this.txtImgSinal.getText().equals(this.repAtual.getArquivoSinais()))                        
                camposAlterados.put("IMG_SINAL", this.txtImgSinal.getText());            
            
            if (camposAlterados.isEmpty()) 
            {
               JOptionPane.showMessageDialog(this,"Não há alterações a serem salvas!.","ALERTA",JOptionPane.INFORMATION_MESSAGE);               
            }
            else
            {
               if (this.repAtual.atualizaBD(camposAlterados)) {
                   JOptionPane.showMessageDialog(this,"Dados alterados com sucesso!");
                   this.formListaSinais.atualizaTabela();               
               } else {
                   JOptionPane.showMessageDialog(this,"Erro durante a atualização de dados!","ERRO",JOptionPane.ERROR_MESSAGE);
               }
            }                
        }
    }//GEN-LAST:event_btGravarActionPerformed

    private void btCancelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCancelaActionPerformed
        int opcao = JOptionPane.showConfirmDialog(this,
                                              "Deseja realmente cancelar?",
                                              "CONFIRMAÇÃO",
                                              JOptionPane.YES_NO_OPTION);
        if (opcao == JOptionPane.YES_OPTION) 
        {
            this.dispose();
        }
    }//GEN-LAST:event_btCancelaActionPerformed

    private void btGravarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btGravarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btGravarMouseClicked

    private void btAnteriorMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btAnteriorMousePressed
        if (evt.getButton() == MouseEvent.BUTTON1) {                    
                    ActionListener action = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {                                
                                anteriorRegistro();
                        }                        
                    };                    
                    timerAnt = new Timer(TEMPO_NAVEGACAO, action);                    
                    timerAnt.start();
                }
    }//GEN-LAST:event_btAnteriorMousePressed

    private void btAnteriorMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btAnteriorMouseReleased
            if (evt.getButton() == MouseEvent.BUTTON1) {
                    if (timerAnt != null) 
                    {
                        if (btAnterior.getModel().isRollover())
                            btAnterior.getModel().setRollover(false);
                        
                        timerAnt.setRepeats(false);
                        JButton bt = (JButton) evt.getSource();                        
                        bt.setSelected(false);                        
                    }
                }
    }//GEN-LAST:event_btAnteriorMouseReleased

    private void btAnteriorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btAnteriorMouseExited
        if (evt.getButton() == MouseEvent.BUTTON1) {
                    if (timerAnt != null) {
                        timerAnt.stop();
                        JButton bt = (JButton) evt.getSource();                        
                        bt.setSelected(false);                        
                    }
                }
    }//GEN-LAST:event_btAnteriorMouseExited

    private void btProximoMouseExited(java.awt.event.MouseEvent evt) {                                       
        if (evt.getButton() == MouseEvent.BUTTON1) {
                    if (timerProx != null) {
                        timerProx.stop();
                        JButton bt = (JButton) evt.getSource();                        
                        bt.setSelected(false);                        
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
            java.util.logging.Logger.getLogger(FormSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormSinal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormSinal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ImgRepres;
    private javax.swing.JLabel ImgSinal;
    private javax.swing.JPanel PanelNavigator;
    private javax.swing.JPanel PanelRepres;
    private javax.swing.JPanel PanelSinal;
    private javax.swing.JButton btAnterior;
    private javax.swing.JButton btCancela;
    private javax.swing.JButton btCancelar;
    private javax.swing.JButton btConfirmar;
    private javax.swing.JButton btConsultar;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btGravar;
    private javax.swing.JButton btIncluir;
    private javax.swing.JButton btPrimeiro;
    private javax.swing.JButton btProximo;
    private javax.swing.JButton btRepresentacao;
    private javax.swing.JButton btSinal;
    private javax.swing.JButton btUltimo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtDescricao;
    private javax.swing.JTextField txtImgRep;
    private javax.swing.JTextField txtImgSinal;
    private javax.swing.JTextField txtMovimentos;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
