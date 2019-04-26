// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.cytoscape.model.CyNetwork;
import utils.CyUtils;
import utils.Dijkstra.CShortestPath;
/**
 * Dialog for selecting a BufferString from a list, displaying 2 labels and a text
 * Specialized for dialog of ExtendToPositive to display result and select the node to add
 */
public class ComboText extends JDialog implements ActionListener,ItemListener {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> comboBox;
	private JTextArea jtext;
	private JButton okBouton, cancelBouton ;
	private final int width=640,height=480;
	private CyNetwork net;
	private ArrayList<String> orderNames;
	private ArrayList<CShortestPath> shortestPaths;
	public ComboText(JFrame parent,String title,String label1,String label2){
		super(parent,title,true);
		setSize(width,height);
		Container cntnr=getContentPane();
		Box container=Box.createVerticalBox();
		cntnr.add(container);
		container.add(new JLabel(label1));
		jtext=new JTextArea();
		container.add(jtext);
		container.add(new JScrollPane(jtext),BorderLayout.CENTER);
		container.add(new JLabel(label2));
		comboBox=new JComboBox<String>();
		container.add(comboBox);
		comboBox.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,height/8));
		comboBox.addItemListener(this);
		okBouton = new JButton ("    OK     ") ;
		container.add(okBouton);
		okBouton.addActionListener(this) ;
		cancelBouton = new JButton ("Cancel ") ;
		container.add(cancelBouton);
		cancelBouton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void fill(StringBuffer text,ArrayList<String> orderNames,ArrayList<CShortestPath> shortestPaths,CyNetwork net){
		this.orderNames=orderNames;
		this.shortestPaths=shortestPaths;
		this.net=net;
		for(int i=0;i<shortestPaths.size();i++){
			String name=orderNames.get(shortestPaths.get(i).src);
			text.append(name);
			text.append("\t");text.append(shortestPaths.get(i).dist);
			text.append("\t");text.append(shortestPaths.get(i).path.cardinality());
			text.append("\r\n");
			comboBox.addItem(name);
		}
		jtext.setText(text.toString());
		this.net=net;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==okBouton){
			String nodeName=(String)comboBox.getSelectedItem();
			int node=orderNames.indexOf(nodeName);
			Iterator<CShortestPath> it=shortestPaths.iterator();
			CShortestPath csp;
			do csp=it.next(); while((csp.src!=node)&&it.hasNext());
			for(int i=csp.path.nextSetBit(0);i>-1;i=csp.path.nextSetBit(i+1)){
				net.getRow((new CyUtils(net)).getCyNode(orderNames.get(i))).set("selected",true);
			}
			dispose();
		}
		if (e.getSource()==cancelBouton) dispose();
	}
	public void itemStateChanged(ItemEvent e) {
	}
}
