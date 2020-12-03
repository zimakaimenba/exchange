package com.exchangeinfomanager.bankuaifengxi;

import java.awt.MouseInfo;
import java.awt.Point;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

//https://stackoverflow.com/questions/4352803/reg-calculating-system-idle-time
public class GetNodeDataFromDbWhenSystemIdle implements Runnable
{
	private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    
	private LocalDate exportdate;
	private String period;

	public GetNodeDataFromDbWhenSystemIdle (LocalDate exportdate1, String period1) {
		this.exportdate = exportdate1;
		this.period = period1;
	}
	
	 @Override
	 public void run() 
	 {
		 	BanKuaiAndStockTree bkcyltree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
			TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
			int globlenodeindex = bankuaicount;
			
			long idleTime = 0 ;
		    long start = System.currentTimeMillis();
		    Point currLocation = MouseInfo.getPointerInfo().getLocation();
		    
		    for(int i = globlenodeindex -1 ;i >= 0 ; i--) {
		    	 while (running) {
			            synchronized (pauseLock) {
			                if (!running) { // may have changed while waiting to
			                    // synchronize on pauseLock
			                    break;
			                }
			                if (paused) {
			                    try {
			                        synchronized (pauseLock) {
			                            pauseLock.wait(); // will cause this Thread to block until 
			                            // another thread calls pauseLock.notifyAll()
			                            // Note that calling wait() will 
			                            // relinquish the synchronized lock that this 
			                            // thread holds on pauseLock so another thread
			                            // can acquire the lock to call notifyAll()
			                            // (link with explanation below this code)
			                        }
			                    } catch (InterruptedException ex) {
			                        break;
			                    }
			                    if (!running) { // running might have changed since we paused
			                        break;
			                    }
			                }
			            }

			            try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	 Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > 60.0 ) {
//						            	System.out.printf(" System has slept for enough seconds %s %s. Get Node data from Database starting...... \n", seconds, i);
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
							            	getNodeData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    
		    System.out.printf(" Get Node data from Database Ended. \n");
	 }
	 
	 public void stop() {
	        running = false;
	        // you might also want to interrupt() the Thread that is 
	        // running this Runnable, too, or perhaps call:
	        resume();
	        // to unblock
	 }

	 public void pause() {
	        // you may want to throw an IllegalStateException if !running
	        paused = true;
	 }

	 public void resume() {
	        synchronized (pauseLock) {
	            paused = false;
	            pauseLock.notifyAll(); // Unblocks thread
	        }
	 }

	
	private synchronized void getNodeData (BkChanYeLianTreeNode node) 
	{
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");
		
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			System.out.printf("...Get BanKuai %s data from database on background.\n" , node.getMyOwnName() );
			
			if( !((BanKuai)node).isShowinbkfxgui() )
				return;
			
			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				return;
			
			SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai  ();
			node = svsbk.getNodeData( node, requirestart, exportdate,period,true);
			
		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG) {
			System.out.printf("...Get Stock %s data from database on background.\n" , node.getMyOwnName() );
			
			SvsForNodeOfStock svsstk = new SvsForNodeOfStock  ();
			node = (Stock) svsstk.getNodeData( (Stock)node,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
		}
	}
			

}
