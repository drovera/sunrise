// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import utils.CyUtils;
import utils.GeneticUtils;
import dialog.GParamDialog;
import dialog.GTextBox;
/**
 * launch the task of searching the highest score sub-network
 * harbor the functions launched by the result window
*/
public class GeneticSearch extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Search By Genetic Algorithm";
	public CyNetwork compactNet;
	public GeneticUtils gu;
	public GeneticUtils.Param p;
	public GTextBox gt;
	public HashSet<String> result;
	private TaskManager<?,?> taskManager;
	private TaskFactory taskFactory;
	public GeneticSearch(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public void keepParam(){
		p.write();
	}
	public void nodeListResult(){
		p.oneShot=true;
		result=new HashSet<String>();
		gt.clear();
		taskManager.execute(taskFactory.createTaskIterator());
	}
	public void SelectInCompact(){
		(new CyUtils(compactNet,false)).selectList(result);
	}
	public void SelectInRef(){
		(new GeneticUtils(compactNet)).selectInRef(result);
	}
	public class TaskFactory extends AbstractTaskFactory{
		GeneticSearch action;
		public TaskFactory(GeneticSearch action){this.action=action;}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new GeneticSearchTask(action)));
		}		
	}
	public void actionPerformed(ActionEvent e) {
		JFrame frame=sunrise.getAdapter().getCySwingApplication().getJFrame();
		compactNet=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		gu=new GeneticUtils(compactNet);
		if(!gu.rightScoreCol()) if(!gu.selectScoreCol()) return;
		p=gu.new Param();
		if (!p.read()){
			JOptionPane.showMessageDialog(sunrise.getAdapter().getCySwingApplication().getJFrame(),
				"Some Parameters are not in Network Table, Check Compact Network","Warning About Network Table",JOptionPane.ERROR_MESSAGE);
			return;
		}
		GParamDialog gp=new GParamDialog(frame,title,p);
		gp.setVisible(true);
		if(p.population==-1) return;
		if(p.oneShot) result=new HashSet<String>();else result=null;
		gt=new GTextBox(frame,title+" on "+(new CyUtils(compactNet).getName()),0.15,1.0,this);
		gt.setVisible(true);
		taskManager=sunrise.getAdapter().getTaskManager();
		taskFactory=new TaskFactory(this);
		taskManager.execute(taskFactory.createTaskIterator());
	}
}