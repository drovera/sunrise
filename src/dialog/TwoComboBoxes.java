// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
/**
 * Dialog for selecting 2 BufferStrings from 2 lists
 */
public class TwoComboBoxes extends JDialog implements ActionListener,ItemListener {
	private static final long serialVersionUID = 1L;
	private JComboBox<String> comboBox1;
	private JComboBox<String> comboBox2;
	private JButton okBouton, cancelBouton ;
	private boolean ok=false;
	private final int width=360,height=120;
	public TwoComboBoxes(JFrame parent,String title,String label1,String label2,String[]values1,String[]values2){
		super(parent,title,true);
		setSize(width,height);
		Container container=getContentPane();
		container.setLayout(new GridLayout(3,2));
		container.add(new JLabel(label1));
		container.add(new JLabel(label2));
		comboBox1=new JComboBox<String>();
		container.add(comboBox1);
		comboBox1.addItemListener(this);
		for(int i=0;i<values1.length;i++) comboBox1.addItem(values1[i]);
		comboBox2=new JComboBox<String>();
		container.add(comboBox2);
		comboBox2.addItemListener(this);
		for(int i=0;i<values2.length;i++) comboBox2.addItem(values2[i]);
		okBouton = new JButton ("OK") ;
		container.add(okBouton);
		okBouton.addActionListener(this) ;
		cancelBouton = new JButton ("Cancel") ;
		container.add(cancelBouton);
		cancelBouton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public boolean launchDialog(StringBuffer selected1,StringBuffer selected2){
		ok=false;
		setVisible(true);
		if(ok){
			selected1.append((String)comboBox1.getSelectedItem());
			selected2.append((String)comboBox2.getSelectedItem());
		}
		return ok;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==okBouton){
			ok=true;
			dispose();
		}
		if (e.getSource()==cancelBouton) dispose();
	}
	public void itemStateChanged(ItemEvent e) {
	}
}
