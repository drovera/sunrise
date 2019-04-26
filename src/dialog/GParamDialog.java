// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import utils.GeneticUtils;
/**
 * Dialog to input parameters to launch genetic algorithm
 * and option of searching
 */
public class GParamDialog extends GridBagDialog implements ActionListener{
	private static final long serialVersionUID = 1L;
	final String lb0S="Parameters and Options";
	final String lb4S="Option of Search";
	GeneticUtils.Param p;
	final static int cx[]={0,0,1,0,1,0,1,0,0,1,0,1,0,1};
	final static int cy[]={0,1,1,2,2,3,3,4,5,5,6,6,7,7};
	final static int cw[]={2,1,1,1,1,1,1,2,1,1,1,1,1,1};
	final static int ch[]={1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	final static int xw[]={1,1,10,1,10,1,10,1,1,10,1,10,1,1};
	final static int yw[]={1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	final static int cf[]={B,B,B,B,B,B,B,B,B,B,B,B,H,H};
	final int width=550;
	final int height=250;
	private JLabel lb0,lb1,lb2,lb3,lb4;
	private JTextField tx1,tx2,tx3;
	private JRadioButton rb1,rb2;
	private JTextField tb1,tb2;
	private JButton okBt,cancelBt;
	public GParamDialog(JFrame parent,String title,GeneticUtils.Param p) {
		super(parent,title,true,cx,cy,cw,ch,xw,yw,cf);
		this.p=p;
		setSize(width,height);
		container=getContentPane();
		container.setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		lb0=new JLabel(lb0S,JLabel.CENTER);
		addWithConstraints(0,lb0);
		lb1=new JLabel(p.lbSP,JLabel.LEFT);
		addWithConstraints(1,lb1);
		tx1=new JTextField();
		addWithConstraints(2,tx1);
		tx1.setText(Integer.toString(p.population));
		lb2=new JLabel(p.lbNG,JLabel.LEFT);
		addWithConstraints(3,lb2);
		tx2=new JTextField();
		addWithConstraints(4,tx2);
		tx2.setText(Integer.toString(p.generationNb));
		lb3=new JLabel(p.lbNM,JLabel.LEFT);
		addWithConstraints(5,lb3);
		tx3=new JTextField();
		addWithConstraints(6,tx3);
		tx3.setText(Integer.toString(p.mutationNb));
		lb4=new JLabel(lb4S,JLabel.CENTER);
		addWithConstraints(7,lb4);
		ButtonGroup group = new ButtonGroup();
		rb1=new JRadioButton(p.rb1S,true);
		group.add(rb1);
		addWithConstraints(8,rb1);
		rb1.addActionListener(this);
		tb1=new JTextField();
		addWithConstraints(9,tb1);
		tb1.setText(Integer.toString(p.salvo));
		rb2=new JRadioButton(p.rb2S,false);
		group.add(rb2);
		addWithConstraints(10,rb2);
		rb2.addActionListener(this);
		tb2=new JTextField();
		addWithConstraints(11,tb2);
		tb2.setText(Long.toString(p.seed));
		okBt = new JButton("OK");
		addWithConstraints(12,okBt);
		okBt.addActionListener(this);
		cancelBt = new JButton("Cancel");
		addWithConstraints(13,cancelBt);
		cancelBt.addActionListener(this);
		addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
	}
	public void actionPerformed(ActionEvent ev){
		if (ev.getSource()==okBt){
			if (rb1.isSelected()) p.oneShot=false;
			if (rb2.isSelected()) p.oneShot=true;
			try{
				p.population=Integer.valueOf(tx1.getText());
				p.generationNb=Integer.valueOf(tx2.getText());
				p.mutationNb=Integer.valueOf(tx3.getText());
				p.salvo=Integer.valueOf(tb1.getText());
				p.seed=Long.valueOf(tb2.getText());
				dispose();	
			}
			catch (Exception exc){
				JOptionPane.showMessageDialog(this,"Not Correct Input, Not Numeric or Overflow",getTitle(),JOptionPane.ERROR_MESSAGE);
			}		
		}
		if (ev.getSource()==cancelBt){
			p.population=-1;
			dispose();
		}				
	}
}