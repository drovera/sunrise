// Daniel.Rovera@curie.fr or daniel.rovera@gmail.com
// Cytoscape Plugin under GNU GPL by Institut Curie
package main;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.TreeSet;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import utils.*;
import utils.GeneticUtils.Item;
/**
 * search the highest score sub-network by genetic algorithm according to options (see algorithm)
 * draw lots the start subnetwork and keep only max score tree with discarding the not necessary negation nodes (cleaning the node set)
 * draw lots a position of cross-over, do the cross-over, apply mutations and clean the got node set (function lauchOne)
 * the sequence of random number start from a seed which is drawn lots in cases of launchshots or input or read from parameters in launchseed
 */
public class GeneticSearchTask extends NetUtils implements Task {
	private Random r;
	private long seed;
	private TreeSet<Item> ranking;
	private int maxGen;	
	private GeneticSearch a;
	protected ArrayList<BitSet> connectNegPos;
	private TaskMonitor monitor;
	private boolean stop=false;
	public GeneticSearchTask(GeneticSearch action){
		super(action.compactNet);
		a=action;
	}
	private void initGenetic(){
		connectNegPos=new ArrayList<BitSet>();
		for(int n=0;n<firstPos;n++){
			BitSet positive=(BitSet)adjacentN.get(n).clone();
			positive.clear(0,firstPos);
			connectNegPos.add(positive);
		}
		for(int n=firstPos;n<nodeNb;n++) connectNegPos.add(new BitSet());
		ranking=new TreeSet<Item>();
	}
	private Item drawLotNet(){
		int start=r.nextInt(nodeNb);
		int size=r.nextInt(nodeNb-firstPos)+2;
		BitSet gone=new BitSet();
		Stack<Integer> stack=new Stack<Integer>();
		stack.push(start);
		while(!stack.isEmpty()){
			int node=stack.pop();
			if(!gone.get(node)){
				gone.set(node);	
				if(node<firstPos) gone.or(connectNegPos.get(node));
				if(gone.cardinality()>size) break;
				for(int a=adjacentN.get(node).nextSetBit(0);a>-1;a=adjacentN.get(node).nextSetBit(a+1)) stack.push(a);
			}
		}		
		gone=delNegKeepPos(gone);
		return (a.gu.new Item(gone,getScore(gone)));
	}
	private BitSet maxOfConnected(BitSet group){
		double maxScore=Double.NEGATIVE_INFINITY;
		BitSet maxGroup=null;
		Stack<Integer> stack=new Stack<Integer>();
		int start=group.nextSetBit(0);
		while(start>-1){
			BitSet gone=new BitSet();
			stack.push(start);
			while(!stack.isEmpty()){
				int node=stack.pop();
				if(!gone.get(node)){
					gone.set(node);
					if(node<firstPos) gone.or(connectNegPos.get(node));
					group.clear(node);
					for(int a=adjacentN.get(node).nextSetBit(0);a>-1;a=adjacentN.get(node).nextSetBit(a+1)) if(group.get(a)) stack.push(a);
				}
			}
			double score=getScore(gone);
			if((gone.cardinality()>1)&&(score>maxScore)){
				maxGroup=gone;
				maxScore=score;
			}
			start=group.nextSetBit(start+1);
		}
		return maxGroup;
	}
	private void mergeAndAdd(BitSet p1,BitSet p2){
		BitSet child=new BitSet();
		child.or(p1);
		child.or(p2);
		int mutNb=(new Double((a.p.mutationNb+1)*r.nextDouble())).intValue();
		for(int m=0;m<mutNb;m++){
			int mp=r.nextInt(nodeNb);
			child.flip(mp);
		}
		child=maxOfConnected(child);
		if(child==null) return;	
		child=delNegKeepPos(child);
		ranking.add(a.gu.new Item(child,getScore(child)));
	}
	private boolean launchOne(){
		maxGen=0;
		int gen=0;
		ranking.clear();
		for(int n=0;n<a.p.population;n++) ranking.add(drawLotNet());
		while(gen<a.p.generationNb){
			if(stop) return false;
			double ps=ranking.last().s;
			int pos=r.nextInt(nodeNb-2);
			int i1=r.nextInt(ranking.size()-1),i2=r.nextInt(ranking.size()-i1-1)+i1+1;
			Item f=null,m=null;
			Iterator<Item> it=ranking.iterator();
			int i=-1;
			while(i++<i1) f=it.next();
			BitSet f1=(BitSet)f.b.clone();
			f1.clear(0,pos);
			BitSet f2=(BitSet)f.b.clone();
			f2.clear(pos+1,nodeNb);
			i--;
			while(i++<i2) m=it.next();
			BitSet m1=(BitSet)m.b.clone();
			m1.clear(0,pos);
			BitSet m2=(BitSet)m.b.clone();
			m2.clear(pos+1,nodeNb);
			mergeAndAdd(f1,m2);
			mergeAndAdd(m1,f2);
			while(ranking.size()>a.p.population) ranking.pollFirst();
			if(ranking.last().s>ps){
				maxGen=gen;
				ps=ranking.last().s;
			}
			gen++;
		}
		return true;
	}
	private void launchSeed(){
		seed=a.p.seed;
		r=new Random(seed);
		launchOne();
		a.gt.appendBegin(parameters());
		a.gt.appendEnd(result());
		a.gt.appendEnd("List of Found Nodes:\r\n");
		for(int n=ranking.last().b.nextSetBit(0);n>-1;n=ranking.last().b.nextSetBit(n+1)){
			a.gt.appendEnd(getName(n)+"\r\n");
			a.result.add(getName(n));
		}		
	}
	private void launchShots(){
		a.p.bestScore=0;
		int bestGen=0;
		r=new Random();
		for(int i=0;i<a.p.salvo;i++){
			monitor.setProgress(1.0*i/a.p.salvo);
			seed=r.nextLong();
			r.setSeed(seed);
			if(!launchOne()) break;
			if(ranking.last().s>a.p.bestScore){
				a.p.bestScore=ranking.last().s;
				monitor.setStatusMessage("Score: "+a.p.bestScore);
				a.p.seed=seed;
				bestGen=maxGen;
			}
			a.gt.appendBegin(ranking.last().s+"\r\n");
		}
		bestGen++;
		a.gt.appendBegin(parameters()+"First Best Score: "+a.p.bestScore+" got with Seed: "+a.p.seed+" at generation "+bestGen+"\r\n");
		a.p.generationNb=bestGen;
	}
	private String parameters(){
		String s=a.p.lbSP+a.p.population+"\r\n"+a.p.lbNG+a.p.generationNb+"\r\n"+a.p.lbNM+a.p.mutationNb+"\r\nSeed: ";
		if(a.p.oneShot) s=s+a.p.seed+"\r\n"; else s=s+"random\r\n";
		return s;
	}
	private String result(){
		return ranking.last().s+" Score of "+ranking.last().b.cardinality()+" Nodes at Generation "+maxGen+" (Seed:"+seed+")\r\n";		
	}
	public void run(TaskMonitor taskMonitor) throws Exception {
		monitor=taskMonitor;
		monitor.setTitle(GeneticSearch.title);
		monitor.setTitle("Build Network");
		buildNetwork();if(stop) return;
		initGenetic();
		if(a.p.oneShot){
			monitor.setTitle(a.p.rb2S+a.p.seed);
			launchSeed();
		}else{
			monitor.setTitle(a.p.rb1S+a.p.salvo);
			monitor.setProgress(0.0);
			a.gt.toFront();
			launchShots();
		}		
	}
	public void cancel(){
		stop=true;
	}	
}
