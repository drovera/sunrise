// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * Not modal dialog displaying text and allowing to copy it in clipboard
 */
public class TextBox extends GridBagDialog implements ActionListener,ClipboardOwner{
	private static final long serialVersionUID = 1L;
	final static int cx[]={0,0,1};
	final static int cy[]={0,1,1};
	final static int cw[]={2,1,1};
	final static int ch[]={1,1,1};
	final static int xw[]={8,1,1};
	final static int yw[]={8,0,0} ;
	final static int cf[]={B,H,H};
	protected JTextArea jtext;
	private JButton copyButton,exitButton ;
	public TextBox(JFrame parent,String title,double wScreen,double hScreen,String text){
		super(parent,title,false,cx,cy,cw,ch,xw,yw,cf);
		Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(scr.width*wScreen),(int)(scr.height*hScreen));
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		jtext=new JTextArea(text);
		addWithConstraints(0,new JScrollPane(jtext));
		copyButton = new JButton ("Copy whole to Clipboard");
		addWithConstraints(1,copyButton);
		copyButton.addActionListener(this);
		exitButton = new JButton ("Exit");
		addWithConstraints(2,exitButton);
		exitButton.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void actionPerformed (ActionEvent e){
		if (e.getSource()==copyButton) setClipboardContents(jtext.getText());
		if (e.getSource()==exitButton) dispose();
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
	public void setClipboardContents(String aString){
	    StringSelection stringSelection = new StringSelection(aString);
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, this);
	  }
	public void lostOwnership(Clipboard aClipboard, Transferable aContents) {}
}