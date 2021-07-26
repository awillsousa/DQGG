
package dqgg;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *  Classe utilizada para gerar as Tabs que conterão categorias para a consulta
 *  Ex.: Movimentos de Cabeça, Articulações da Mão
 *  @author Will
 */
public class TabCategoria extends JPanel{
    private static final long serialVersionUID = 1L;
    private final JFrame pai;
    private final Categoria categoria;
    private final ArrayList<Quirema> quiremas;
    private final ArrayList<PanelQuirema> panels = new ArrayList<PanelQuirema>();    
    private final String titulo;
    
    public TabCategoria(Categoria c, JFrame cont)
    {
        super();        
        this.pai = cont;
        this.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N        
        
        GridBagLayout gbLayout = new GridBagLayout();        
        this.setLayout(gbLayout);        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(2,2,2,2);            
        gbc.ipadx = 4;
        gbc.ipady = 4;
                
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;                
        gbc.weightx = 1.0;
        //gbc.weighty = 1.0;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
                
        this.categoria = c;
        this.quiremas = new Quirema().buscaPorCategoria(this.categoria);
        this.titulo = this.categoria.getDescricao();
        
        for (Quirema q : this.quiremas) {
            PanelQuirema p = new PanelQuirema(pai);
            p.setQuirema(q);                
            this.panels.add(p);  
            gbc.gridy = Integer.parseInt(q.getLinha());
            gbc.gridx = Integer.parseInt(q.getColuna());
            this.add(p, gbc);                 
        }        
    }
    
    public String getTitulo()
    {
        return (this.titulo);
    }
    
    public JFrame getPai()
    {
        return (this.pai);
    }
}
