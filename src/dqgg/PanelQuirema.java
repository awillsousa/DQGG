
package dqgg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.TimerTask;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * Classe responsável por criar um panel contendo os elementos de um Quirema utilizado para montar
 * a busca de sinais através de seus elementos
 * PanelQuirema(this) 
 *   -----------------------------------------  
 *  |panelTitulo                              |      
 *  | --------------------------------------- |     
 *  || labelTitulo                           ||     
 *  | --------------------------------------- |     
 *  |panelControles                           |     
 *  | --------------------------------------- |     
 *  || __  __  __________  ________________  ||     1 - btAnterior
 *  |||1 ||2 ||   3      ||   4            | ||     2 - btProximo
 *  |||  ||  ||          ||                | ||     3 - panelImg
 *  |||__||__||__________||________________| ||     4 - panelDescricao
 *  | --------------------------------------- |     
 *   -----------------------------------------  
 * @author Willian Sousa
 */
public class PanelQuirema extends JPanel implements MouseWheelListener{   
    private static final long serialVersionUID = 1L;
    public final JFrame pai;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JPanel panelControles;
    private javax.swing.JPanel panelBotoes;
    private javax.swing.JButton btAnterior;
    private javax.swing.JButton btProximo;
    private javax.swing.JPanel panelImg;
    private javax.swing.JLabel imgAloquiro;
    private javax.swing.JScrollPane panelDescricao;
    private javax.swing.JTextPane txtDescricao;
    private javax.swing.GroupLayout panelLayout;
    
    private Quirema quirema;
    private ArrayList<Aloquiro> aloquiros;
    private Aloquiro aloquiroAtual;
    private int indiceAloquiro = 0;
    
    private boolean mouseDown = false;
    private boolean isRunning = false;
    private Timer timerAnt;
    private Timer timerProx;
    
    /* CONSTANTES */
    private static final int LARGURA_BOTAO   = 25;            /* largura padrão dos botões de navegação             */
    private static final int ALTURA_CMPNTS   = 110;           /* altura dos componentes (botões, imagem e descrição */
    private static final int LARGURA_MAX_FIG = 120;           /* largura máxima da figura exibida (label)           */
    private static final int LARGURA_MIN_FIG = 100;           /* largura máxima da figura exibida (label)           */
    private static final int TEMPO_NAVEGACAO = 150;
        
    public PanelQuirema(JFrame p)
    {
        super();                
        this.pai = p;
        this.panelTitulo    = new JPanel();
        this.labelTitulo    = new JLabel();
        this.panelControles = new JPanel();        
        this.panelBotoes = new JPanel();
        this.btProximo = new BasicArrowButton(BasicArrowButton.EAST);
        this.btAnterior = new BasicArrowButton(BasicArrowButton.WEST);
        this.btProximo.setVerifyInputWhenFocusTarget(true);
        this.btAnterior.setVerifyInputWhenFocusTarget(true);
        this.imgAloquiro    = new JLabel();
        this.panelImg       = new javax.swing.JPanel();
        this.panelDescricao = new JScrollPane();
        this.txtDescricao   = new JTextPane();
        
        // INICIO -- Configuraçõoes do frame principal
        this.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        
        this.setAlignmentX(1.0F);
        this.setAlignmentY(0.0F);        
        this.setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
                        
            // INICIO -- Configurações do panel do titulo            
            panelTitulo.setAlignmentX(0.0F);
            panelTitulo.setAlignmentY(0.0F);
            //panelTitulo.setMinimumSize(new java.awt.Dimension(284, 25));
            /*panelTitulo.setMaximumSize(new java.awt.Dimension(32767, 1500));*/
            /*panelTitulo.setMinimumSize(new java.awt.Dimension(484, 25));   */
            /*panelTitulo.setPreferredSize(new java.awt.Dimension(300, 25)); */ 
            panelTitulo.setLayout(new java.awt.BorderLayout());
                labelTitulo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N                
                labelTitulo.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
            panelTitulo.add(labelTitulo, java.awt.BorderLayout.CENTER);
        this.add(panelTitulo);
            // FIM -- Configurações do panel do titulo
        
            // INICIO -- Configurações do panel dos controles            
            panelControles.setAlignmentX(0.0F);
            panelControles.setAlignmentY(1.0F);
            panelControles.setLayout(new javax.swing.BoxLayout(panelControles, javax.swing.BoxLayout.X_AXIS));
                            
                panelBotoes.setAlignmentX(0.0F);
                panelBotoes.setAlignmentY(0.0F);
                panelBotoes.setLayout(new javax.swing.BoxLayout(panelBotoes, javax.swing.BoxLayout.X_AXIS));        
                panelBotoes.setMaximumSize(new java.awt.Dimension(LARGURA_BOTAO*2, ALTURA_CMPNTS));
                panelBotoes.setMinimumSize(new java.awt.Dimension(LARGURA_BOTAO*2, ALTURA_CMPNTS));
                panelBotoes.setPreferredSize(new java.awt.Dimension(LARGURA_BOTAO*2, ALTURA_CMPNTS));

                    btAnterior.setAlignmentX(0.5F);
                    btAnterior.setAlignmentY(0.0F);
                    btAnterior.setToolTipText("Anterior");
                    btAnterior.setMaximumSize(new java.awt.Dimension(LARGURA_BOTAO, ALTURA_CMPNTS));
                    btAnterior.setMinimumSize(new java.awt.Dimension(LARGURA_BOTAO, ALTURA_CMPNTS));
                    btAnterior.setPreferredSize(new java.awt.Dimension(LARGURA_BOTAO, ALTURA_CMPNTS));
                panelBotoes.add(btAnterior);

                    btProximo.setAlignmentX(0.5F);
                    btProximo.setAlignmentY(0.0F);
                    btProximo.setToolTipText("Próximo");
                    btProximo.setMaximumSize(new java.awt.Dimension(LARGURA_BOTAO, ALTURA_CMPNTS));
                    btProximo.setMinimumSize(new java.awt.Dimension(LARGURA_BOTAO, ALTURA_CMPNTS));
                    btProximo.setPreferredSize(new java.awt.Dimension(LARGURA_BOTAO, ALTURA_CMPNTS)); 
                panelBotoes.add(btProximo);    
            
            panelControles.add(panelBotoes);
            panelControles.add(Box.createRigidArea(new Dimension(3, 0)));
            
                panelImg.setBackground(new java.awt.Color(255, 255, 255));
                panelImg.setBorder(BorderFactory.createLineBorder(Color.black));
                panelImg.setAlignmentY(0.0F);
                panelImg.setMaximumSize(new java.awt.Dimension(LARGURA_MAX_FIG, ALTURA_CMPNTS));
                panelImg.setMinimumSize(new java.awt.Dimension(LARGURA_MIN_FIG, ALTURA_CMPNTS));
                panelImg.setPreferredSize(new java.awt.Dimension(120, ALTURA_CMPNTS));
                
                   /* TODO: prover uma maneira melhor de implementar o MouseListener do FormPrincipal e do JLabel dos Aloquiros   */        
                    imgAloquiro.addMouseListener((MouseListener) pai);
                    imgAloquiro.addMouseWheelListener(this);
                    imgAloquiro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);                    
                    imgAloquiro.setVerticalAlignment(SwingConstants.CENTER);                    
                    imgAloquiro.setAlignmentX(0.5F);
                    imgAloquiro.setAlignmentY(0.0F);
                    imgAloquiro.setMaximumSize(new java.awt.Dimension(LARGURA_MAX_FIG, ALTURA_CMPNTS));
                    imgAloquiro.setMinimumSize(new java.awt.Dimension(LARGURA_MIN_FIG, ALTURA_CMPNTS));
                    imgAloquiro.setPreferredSize(new java.awt.Dimension(120, ALTURA_CMPNTS));

                javax.swing.GroupLayout panelImgLayout = new javax.swing.GroupLayout(panelImg);
                panelImg.setLayout(panelImgLayout);
                panelImgLayout.setHorizontalGroup(
                    panelImgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(imgAloquiro, javax.swing.GroupLayout.PREFERRED_SIZE, LARGURA_MAX_FIG, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
                panelImgLayout.setVerticalGroup(
                    panelImgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(imgAloquiro, javax.swing.GroupLayout.PREFERRED_SIZE, ALTURA_CMPNTS, javax.swing.GroupLayout.PREFERRED_SIZE)
                );
            panelControles.add(panelImg); 
            panelControles.add(Box.createRigidArea(new Dimension(1, 0)));
            
                txtDescricao.setEditable(false);
                txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N                
                txtDescricao.setAlignmentX(0.0F);
                txtDescricao.setAlignmentY(0.0F);
                txtDescricao.setMaximumSize(new java.awt.Dimension(32767, ALTURA_CMPNTS));
                txtDescricao.setMinimumSize(new java.awt.Dimension(100, ALTURA_CMPNTS));
                txtDescricao.setPreferredSize(new java.awt.Dimension(100, ALTURA_CMPNTS));
                panelDescricao.setViewportView(this.txtDescricao);                                
                panelDescricao.setAlignmentY(0.0F);
                panelDescricao.setMaximumSize(new java.awt.Dimension(32767, ALTURA_CMPNTS));
                panelDescricao.setMinimumSize(new java.awt.Dimension(100, ALTURA_CMPNTS));
                panelDescricao.setPreferredSize(new java.awt.Dimension(100, ALTURA_CMPNTS));                
                panelDescricao.setViewportView(txtDescricao);
            panelControles.add(panelDescricao);
            panelControles.add(Box.createRigidArea(new Dimension(1, 0)));
            // FIM -- Configurações do panel dos controles    
          this.add(panelControles);            
        // FIM -- Configuraçõoes do frame principal
        
        // Define os metodos de tratamento do clique nos botoes do painel  
        btAnterior.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                   AntAloq();
                } 
            }
            @Override
            public void mousePressed(MouseEvent e) { 
                if (e.getButton() == MouseEvent.BUTTON1) {                    
                    ActionListener action = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {                            
                                AntAloq();
                        }                        
                    };                    
                    timerAnt = new Timer(TEMPO_NAVEGACAO, action);                    
                    timerAnt.start();
                }
            }
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
          
        btProximo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                   ProxAloq();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {  
                if (e.getButton() == MouseEvent.BUTTON1) {                    
                    ActionListener action = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            ProxAloq();
                        }                        
                    };
                    timerProx = new Timer(TEMPO_NAVEGACAO, action);                    
                    timerProx.start();
                }
            }
            @Override    
            public void mouseReleased(MouseEvent e) { 
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (timerProx != null)
                        {
                            timerProx.stop();                    
                            JButton bt = (JButton) e.getSource();                        
                            bt.setSelected(false);                             
                        }
                    }
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (timerProx != null) 
                    {
                        timerProx.stop();
                        JButton bt = (JButton) e.getSource();                        
                        bt.setSelected(false);                        
                    }
                }
            }  
        }); 
    }
    
    public Container getPai()
    {
        return (this.pai);
    }
    
    public void setQuirema(Quirema q)
    {
        this.quirema = q;
        //this.labelTitulo.setText(this.quirema.getDescricao());
        this.aloquiros = new Aloquiro().buscaPorQuirema(q); 
        this.labelTitulo.setText(this.quirema.getDescricao()+ " (" + String.valueOf(this.aloquiros.size()-1) + ")" );
        if (!this.aloquiros.isEmpty())
        {
            this.aloquiroAtual = this.aloquiros.get(0);  // pega o primeiro aloquiro
            this.atualiza();
        }
    }
    
    public void atualiza()
    {
        if (this.aloquiroAtual != null)
        {    
            this.txtDescricao.setText(this.aloquiroAtual.getDescricao());                    
            Dimension d = this.panelImg.getPreferredSize();
            int w = (int) d.getWidth();
            int h = (int) d.getHeight();
            
            ImageIcon icone = Utils.getImagem(this.aloquiroAtual.getImagem());            
            if ((icone.getIconHeight() > h) || (icone.getIconWidth() > w))
                icone = Utils.rescaleImage(this.aloquiroAtual.getImagem(), icone.getIconWidth(), icone.getIconHeight());                                    
            
            this.imgAloquiro.setIcon(icone);
        }    
    }
    
    public Quirema getQuirema()
    {
        return (this.quirema);
    }
    
    public Aloquiro getAloquiroAtual()
    {
        return (this.aloquiroAtual);
    }
    
    private void ProxAloq()
    {
        if (this.indiceAloquiro < this.aloquiros.size()-1)
        {
            this.indiceAloquiro++;
            this.aloquiroAtual = this.aloquiros.get(indiceAloquiro);
            this.atualiza();
        }  
    }
    
    private void AntAloq()
    {
        if (this.indiceAloquiro > 0)
        {
            this.indiceAloquiro--;
            this.aloquiroAtual = this.aloquiros.get(indiceAloquiro);
            this.atualiza();
        }        
    }
    
    private void PrimAloq()
    {
        if (this.indiceAloquiro > 0)
        {
            this.indiceAloquiro = 0;
            this.aloquiroAtual = this.aloquiros.get(indiceAloquiro);
            this.atualiza();
        }        
    }
    
    private void UltAloq()
    {
        if (this.indiceAloquiro < this.aloquiros.size()-1)
        {
            this.indiceAloquiro = (this.aloquiros.size()-1);
            this.aloquiroAtual = this.aloquiros.get(indiceAloquiro);
            this.atualiza();
        }  
    }
        
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();        
        if (notches < 0) 
           this.AntAloq();
        else 
           this.ProxAloq();               
    }
}
