/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dqgg;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
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
import javax.swing.border.Border;

/**
 *
 * @author Will
 */
public class PanelQuirema_1 extends JPanel implements MouseWheelListener{    
    public final JFrame pai;
    private javax.swing.JLabel labelTitulo;
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
    
    public PanelQuirema_1(JFrame p)
    {
        super();
        this.pai = p;
        this.labelTitulo    = new JLabel();
        this.btAnterior     = new JButton();
        this.btProximo      = new JButton();
        this.imgAloquiro    = new JLabel();
        this.panelImg       = new javax.swing.JPanel();
        this.panelDescricao = new JScrollPane();
        this.txtDescricao   = new JTextPane();
        
        this.btAnterior.setFont(new java.awt.Font("Aharoni", 0, 14)); // NOI18N
        this.btAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/prev-estreito.jpg"))); // NOI18N
        this.btAnterior.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        this.btAnterior.setMaximumSize(new java.awt.Dimension(40, 25));
        this.btAnterior.setMinimumSize(new java.awt.Dimension(40, 25));
        this.btAnterior.setPreferredSize(new java.awt.Dimension(40, 25));

       // this.btProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/img/sistema/next-estreito.jpg"))); // NOI18N
        this.btProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dqgg/next-estreito.jpg"))); // NOI18N
        this.btProximo.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        this.btProximo.setMaximumSize(new java.awt.Dimension(40, 25));
        this.btProximo.setMinimumSize(new java.awt.Dimension(40, 25));
        this.btProximo.setPreferredSize(new java.awt.Dimension(40, 25));

        this.panelImg.setBackground(new java.awt.Color(255, 255, 255));
        
        this.imgAloquiro.setBorder(javax.swing.BorderFactory.createEtchedBorder());        
       /* TODO: prover uma maneira melhor de implementar o MouseListener do FormPrincipal e do JLabel dos Aloquiros   */        
        this.imgAloquiro.addMouseListener((MouseListener) pai);
        this.imgAloquiro.addMouseWheelListener(this);
        this.imgAloquiro.setHorizontalAlignment(SwingConstants.CENTER);
        this.imgAloquiro.setVerticalAlignment(SwingConstants.CENTER);
        this.imgAloquiro.setBorder(BorderFactory.createLineBorder(Color.black));
        
        this.txtDescricao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        this.txtDescricao.setEditable(false);
        this.panelDescricao.setViewportView(this.txtDescricao);
        this.panelDescricao.setMinimumSize(new java.awt.Dimension(40, 25));
        //this.txtDescricao.setMinimumSize(new java.awt.Dimension(40, 25));
        
        
        this.txtDescricao.setAlignmentX(0);
        this.txtDescricao.setAlignmentY(0);
        this.labelTitulo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N       

        javax.swing.GroupLayout panelImgLayout = new javax.swing.GroupLayout(panelImg);
        panelImg.setLayout(panelImgLayout);
        panelImgLayout.setHorizontalGroup(
            panelImgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imgAloquiro, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
        );
        panelImgLayout.setVerticalGroup(
            panelImgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imgAloquiro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        
        panelLayout = new javax.swing.GroupLayout(this);
        this.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(labelTitulo)
                        .addGap(133, 133, 133))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(btAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(btProximo, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        //.addComponent(imgAloquiro, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(panelImg, javax.swing.GroupLayout.PREFERRED_SIZE, 117/*javax.swing.GroupLayout.DEFAULT_SIZE*/, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(panelDescricao)
                        .addContainerGap())))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addComponent(labelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btProximo, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    //.addComponent(imgAloquiro, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelImg, javax.swing.GroupLayout.DEFAULT_SIZE, 116/*javax.swing.GroupLayout.DEFAULT_SIZE*/, javax.swing.GroupLayout.PREFERRED_SIZE/*Short.MAX_VALUE*/)
                    .addComponent(panelDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        
        // Define os metodos de tratamento do clique nos botoes do painel
        btAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAnteriorActionPerformed(evt);
            }
        });
        btProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btProximoActionPerformed(evt);
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
        this.labelTitulo.setText(this.quirema.getDescricao());
        this.aloquiros = new Aloquiro().buscaPorQuirema(q); 
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
            //this.imgAloquiro.setIcon(new javax.swing.ImageIcon(DQGG.pathApp + this.aloquiroAtual.getImagem()));
            //int w = this.panelImg.getWidth();
            //int h = this.panelImg.getHeight();
            Dimension d = this.panelImg.getPreferredSize();
            int w = (int) d.getWidth();
            int h = (int) d.getHeight();
            
            ImageIcon icone = Utils.getImagem(this.aloquiroAtual.getImagem());            
            if ((icone.getIconHeight() > h) || (icone.getIconWidth() > w))
                icone = Utils.rescaleImage(this.aloquiroAtual.getImagem(), icone.getIconWidth(), icone.getIconHeight());                                    
            
            this.imgAloquiro.setIcon(icone);
                
                
              //  this.imgAloquiro.setIcon(new javax.swing.ImageIcon(getClass().getResource(DQGG.pathRes + this.aloquiroAtual.getImagem())));                                   
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
    
    private void btAnteriorActionPerformed(java.awt.event.ActionEvent evt) {                                                
        this.AntAloq();
    }  
    
    private void btProximoActionPerformed(java.awt.event.ActionEvent evt) {        
        this.ProxAloq();
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
