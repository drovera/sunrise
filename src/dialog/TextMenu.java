// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Text window with menu to copy selected or whole text and close the dialog
 */	
public class TextMenu extends JDialog implements ClipboardOwner,ActionListener{
	private static final long serialVersionUID = 1L;
	protected JTextArea jtext;
	JMenuBar menuBar;
	protected JMenuItem cwt,cst,close;
	public TextMenu(JFrame parent,String title,double wScreen,double hScreen){
		super();
		setTitle(title);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(scr.width*wScreen),(int)(scr.height*hScreen));
		jtext=new JTextArea();
		add(new JScrollPane(jtext),BorderLayout.CENTER);
		menuBar=new JMenuBar();
		setJMenuBar(menuBar);
		setEditMenu();
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	private void setEditMenu(){
		final String editMenu="Edition";
		final String[] endItems={"Copy Whole Text","Copy Selected Text","Close Window"};
		JMenu edit=new JMenu(editMenu);
		menuBar.add(edit);
		cwt=new JMenuItem(endItems[0]);
		edit.add(cwt);
		cwt.addActionListener(this);
		cst=new JMenuItem(endItems[1]);
		edit.add(cst);
		cst.addActionListener(this);
		close=new JMenuItem(endItems[2]);
		edit.add(close);
		close.addActionListener(this);
	}
	public void actionPerformed(ActionEvent e){
		Object source=e.getSource();
		if(source==cwt) setClipboardContents(jtext.getText());
		if(source==cst) setClipboardContents(jtext.getSelectedText());
		if(source==close) dispose();
	}
	public void clear(){
		jtext.setText("");
	}
	public void appendBegin(String text){
		jtext.insert(text,0);
	}
	public void appendEnd(String text){	
		jtext.append(text);	
	}
	protected void setClipboardContents(String aString){
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}
	public void lostOwnership(Clipboard aClipboard, Transferable aContents){}
}
