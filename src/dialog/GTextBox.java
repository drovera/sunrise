// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import main.GeneticSearch;
/**
 * Text window with 2 menus to use the result of algorithm
 * and to copy selected or whole text and close the dialog
 * the 2 menus depends on the type of search, one or several shots
 */
public class GTextBox extends TextMenu{
	private static final long serialVersionUID = 1L;
	private JMenuItem action1,action2;
	GeneticSearch a;
	public GTextBox(JFrame parent,String title,double wScreen,double hScreen,GeneticSearch action){
		super(parent,title,wScreen,hScreen);
		a=action;
		setActionMenu();
	}
	protected void setActionMenu(){
		final String actionMenu="Action from Result";
		final String[] firstItems={"Keep Parameters in Table","Node List by One Research"};
		final String[] secondItems={"Select Nodes in Compact","Select Nodes in Reference"};
		JMenu action=new JMenu(actionMenu);
		menuBar.add(action);
		if(a.p.oneShot){
			action1=new JMenuItem(secondItems[0]);
			action2=new JMenuItem(secondItems[1]);
		}else{
			action1=new JMenuItem(firstItems[0]);
			action2=new JMenuItem(firstItems[1]);
		}
		action.add(action1);
		action1.addActionListener(this);
		action.add(action2);
		action2.addActionListener(this);
	}
	public void actionPerformed(ActionEvent e){
		Object source=e.getSource();
		if(source==action1) if(a.p.oneShot) a.SelectInCompact();else a.keepParam();
		if(source==action2) if(a.p.oneShot) a.SelectInRef();else a.nodeListResult();
		if(source==cwt) setClipboardContents(jtext.getText());
		if(source==cst) setClipboardContents(jtext.getSelectedText());
		if(source==close) dispose();
	}
}