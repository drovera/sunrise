// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.List;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * A dialog box to select strings in a list displaying selected strings
 * An arrayList is filled of selected values
 * 
 */
public class ListDialog extends GridBagDialog implements ActionListener,ListSelectionListener{
	private static final long serialVersionUID = 1L;
	public static int cx[]={0,0,1,0,1};
	public static int cy[]={0,1,1,2,2};
	public static int cw[]={2,1,1,1,1};
	public static int ch[]={1,1,1,1,1};
	public static int xw[]={0,10,5,1,1};
	public static int yw[]={0,10,5,0,0};
	public static int cf[]={H,B,B,H,H};
	final int width=480;
	final int height=480;
	private JButton okBouton, cancelBouton ;
	private boolean ok = false ;
	private JList<String> display;
	private List selected;
	public ListDialog(JFrame parent,String title,String label,String[] values){ 
		super(parent,title,true,cx,cy,cw,ch,xw,yw,cf);
		setSize(width,height);
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		addWithConstraints(0,new JLabel(label));
		display = new JList<String>(values);
		addWithConstraints(1,new JScrollPane(display));
		display.addListSelectionListener(this);
		selected=new List();
		addWithConstraints(2,selected);
		selected.setFont(display.getFont());
		okBouton = new JButton ("OK") ;
		addWithConstraints(3,okBouton);
		okBouton.addActionListener(this) ;
		cancelBouton = new JButton ("Cancel");
		addWithConstraints(4,cancelBouton);
		cancelBouton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void launchDialog(ArrayList<String> values){
		ok = false ;
		display.clearSelection();
		setVisible(true);
		values.clear();
		if(ok) values.addAll(display.getSelectedValuesList());
	}
	public void actionPerformed (ActionEvent e){
		if (e.getSource()==okBouton){
			ok=true;
			dispose();
		}
		if (e.getSource()==cancelBouton) dispose();
	}
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()){
			selected.removeAll();
			for(String s:display.getSelectedValuesList()) selected.add(s);
		}
	}
}