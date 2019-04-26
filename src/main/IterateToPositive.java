//Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
//Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import main.sunrise;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import dialog.TextMenu;
import utils.CyUtils;
import utils.GeneticUtils;
/**
* From a subnetwork, iterate searching positive score nodes connected by highest score path
*/
public class IterateToPositive extends AbstractCyAction{
	private static final long serialVersionUID = 1L;
	final static String title="  Iterate Selection to Positive Nodes";
	CyNetwork net;
	TextMenu tm;
	boolean isConnect=true;
	public IterateToPositive(){
		super(title,sunrise.getAdapter().getCyApplicationManager(),"network",sunrise.getAdapter().getCyNetworkViewManager());
		setPreferredMenu(sunrise.name);
	}
	public class TaskFactory extends AbstractTaskFactory{
		IterateToPositive action;
		public TaskFactory(IterateToPositive action){this.action=action;}
		public TaskIterator createTaskIterator() {
			return(new TaskIterator(new IterateToPositiveTask(action)));
		}		
	}
	public void actionPerformed(ActionEvent e){		
		net=sunrise.getAdapter().getCyApplicationManager().getCurrentNetwork();
		if(sunrise.scoreCol==null) if(!(new GeneticUtils(net)).selectScoreCol()) return;
		JFrame frame=sunrise.getAdapter().getCySwingApplication().getJFrame();
		GeneticUtils gu=new GeneticUtils(net);
		if(!gu.rightScoreCol()) if(!gu.selectScoreCol()) return;
		tm=new TextMenu(frame,IterateToPositive.title+" on "+(new CyUtils(net).getName()),0.15,1.0);
		TaskManager<?,?> taskManager=sunrise.getAdapter().getTaskManager();
		TaskFactory taskFactory=new TaskFactory(this);
		taskManager.execute(taskFactory.createTaskIterator());
		tm.setVisible(true);
		if(!isConnect){
			tm.dispose();
			JOptionPane.showMessageDialog(sunrise.getAdapter().getCySwingApplication().getJFrame(),
					"The Selected Nodes are Not Connected and so The Got Nodes do Not do a SubNetwork",
					"Warning About Selected Nodes",JOptionPane.ERROR_MESSAGE);
		}
	}
}
