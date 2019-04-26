// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package other;
import java.util.Iterator;
import main.sunrise;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import utils.CyUtils;
/**
 * Transfer coordinate task
 */
public class TransferCoordinatesTask implements Task{
	TransferCoordinates a;
	boolean stop=false;
	public TransferCoordinatesTask(TransferCoordinates action){
		a=action;
	}
	public void run(TaskMonitor monitor) throws Exception {
		monitor.setTitle(TransferCoordinates.title);
		CyApplicationManager applicationManager=sunrise.getAdapter().getCyApplicationManager();
		CyNetworkViewManager networkViewManager=sunrise.getAdapter().getCyNetworkViewManager();
		CyUtils utils=new CyUtils(a.toNet);
		double x=0.0,y=0.0;
		CyNetworkView fromView=null,toView=null;
		Iterator<CyNetworkView> viewIter;
		viewIter=networkViewManager.getNetworkViews(a.fromNet).iterator();
		if(viewIter.hasNext()) fromView=viewIter.next(); else return;
		viewIter=networkViewManager.getNetworkViews(a.toNet).iterator();
		if(viewIter.hasNext()) toView=viewIter.next(); else return;
		CyNode to=null;
		String nodeName;
		int p=0;
		int w=a.fromNet.getNodeList().size();
		for(CyNode from:a.fromNet.getNodeList()){
			if(stop) return;
			monitor.setProgress(1.0*p++/w);
			nodeName=a.fromNet.getRow(from).get(CyNetwork.NAME,String.class);
			x=fromView.getNodeView(from).getVisualProperty(BasicVisualLexicon.NODE_X_LOCATION);
			y=fromView.getNodeView(from).getVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION);
			to=utils.getCyNode(nodeName);
			if(to!=null){
				toView.getNodeView(to).setVisualProperty(BasicVisualLexicon.NODE_X_LOCATION,x);
				toView.getNodeView(to).setVisualProperty(BasicVisualLexicon.NODE_Y_LOCATION,y);
			}
		}
		applicationManager.setCurrentNetwork(a.toNet);
		toView.updateView();
	}
	public void cancel() {
		stop=true;
	}
}

