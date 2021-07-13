package com.exchangeinfomanager.bankuaifengxi;

import java.awt.MouseInfo;
import java.awt.Point;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
		this.exportdate = exportdate1.with(DayOfWeek.FRIDAY);
		this.period = period1;
	}
	
	 @Override
	 public void run() 
	 {
		 	BanKuaiAndStockTree bkcyltree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
			TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
			
			int sleepcount = 15; int mousenotmoverange = 50;
			long idleTime = 0 ;
		    long start = System.currentTimeMillis();
		    Point currLocation = MouseInfo.getPointerInfo().getLocation();
		    
		    for(int i = 0;i <= bankuaicount-1  ; i++) {
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

			            try {	Thread.sleep(sleepcount);
						} catch (InterruptedException e) {e.printStackTrace();}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	    Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > mousenotmoverange ) {
						            	if(restartscreentips) {
						            		System.out.printf(" \n System slept long enough. Getting BanKuai Weekly Zhanbi Data from Database starting... " + LocalTime.now() + " \n");
						            		restartscreentips = false;
						            	}
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						            		if(childnode.getType() == BkChanYeLianTreeNode.TDXBK) 
						            				getNodeZhanbiData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            	
						            	restartscreentips = true; counttoentertonewline = 0;
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
//						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    
		    for(int i = 0;i <=bankuaicount-1  ; i++) {
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
			                    } catch (InterruptedException ex) {break; }
			                    if (!running) { // running might have changed since we paused
			                        break;
			                    }
			                }
			            }

			            try {  Thread.sleep(sleepcount);
						} catch (InterruptedException e) {e.printStackTrace();}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	    Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > mousenotmoverange ) {
						            	if(restartscreentips) {
						            		System.out.printf(" \n System slept long enough. Getting BanKuai K Data from Database starting..." + LocalTime.now() + " \n");
						            		restartscreentips = false;
						            	}
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						            		if(childnode.getType() == BkChanYeLianTreeNode.TDXBK)
						            				getNodeKxianData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            	
						            	restartscreentips = true; counttoentertonewline = 0;
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
//						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    
		    System.out.println("......\n Done! Gotten All TDX BanKuai Nodes ZhanBi And K Data At " + LocalTime.now() + "\r\n");
		    
		    for(int i = bankuaicount-1 ;i >=0  ; i--) {
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

			            try {	Thread.sleep(sleepcount);
						} catch (InterruptedException e) {e.printStackTrace();}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	    Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > mousenotmoverange ) {
						            	if(restartscreentips) {
						            		System.out.printf(" \n System slept long enough. Getting Stock Weekly Zhanbi Data from Database starting..." + LocalTime.now() + " \n");
						            		restartscreentips = false;
						            	}
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						            		if(childnode.getType() == BkChanYeLianTreeNode.TDXGG)
						            			getNodeZhanbiData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            	
						            	restartscreentips = true; counttoentertonewline = 0;
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
//						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    
		    for(int i = bankuaicount-1 ;i >=0  ; i--) {
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
			                    } catch (InterruptedException ex) {break; }
			                    if (!running) { // running might have changed since we paused
			                        break;
			                    }
			                }
			            }

			            try {  Thread.sleep(sleepcount);
						} catch (InterruptedException e) {e.printStackTrace();}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	    Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > mousenotmoverange ) {
						            	if(restartscreentips) {
						            		System.out.printf(" \n System slept long enough. Getting Stock K Data from Database starting... " + LocalTime.now() + " \n");
						            		restartscreentips = false;
						            	}
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						            		if(childnode.getType() == BkChanYeLianTreeNode.TDXGG)
						            			getNodeKxianData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            	
						            	restartscreentips = true; counttoentertonewline = 0;
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
//						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    System.out.println("......\n Done! Gotten TDX Stock Nodes ZhanBi And K Data At " + LocalTime.now() + "\r\n");
		    
		    BanKuaiAndStockTree dzhtree = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks();
			TDXNodes dzhtreeroot = (TDXNodes)dzhtree.getModel().getRoot();
			int dzhbankuaicount = dzhtree.getModel().getChildCount(dzhtreeroot);
		    for(int i = 0;i <= dzhbankuaicount-1  ; i++) {
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

			            try {	Thread.sleep(sleepcount);
						} catch (InterruptedException e) {e.printStackTrace();}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	    Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > mousenotmoverange ) {
						            	if(restartscreentips) {
						            		System.out.printf(" \n System slept long enough. Getting BanKuai Weekly Zhanbi Data from Database starting..." + LocalTime.now() + " \n");
						            		restartscreentips = false;
						            	}
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						            		if(childnode.getType() == BkChanYeLianTreeNode.DZHBK) 
						            				getNodeZhanbiData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            	
						            	restartscreentips = true; counttoentertonewline = 0;
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
//						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    System.out.println("......\n Done! Gotten DZH BanKuai Nodes ZhanBi  Data At " + LocalTime.now() + "\r\n");
		    for(int i = 0;i <=dzhbankuaicount-1  ; i++) {
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
			                    } catch (InterruptedException ex) {break; }
			                    if (!running) { // running might have changed since we paused
			                        break;
			                    }
			                }
			            }

			            try {  Thread.sleep(sleepcount);
						} catch (InterruptedException e) {e.printStackTrace();}
			            
			            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
				        	    Point newLocation = MouseInfo.getPointerInfo().getLocation();
						        if(newLocation.equals(currLocation)){
						            //not moved
						            idleTime = System.currentTimeMillis() - start;
						            long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf(" I am sleeping %s %s \n", seconds, i);
						            if( seconds > mousenotmoverange ) {
						            	if(restartscreentips) {
						            		System.out.printf(" \n System slept long enough. Getting BanKuai K Data from Database starting... " + LocalTime.now() + " \n");
						            		restartscreentips = false;
						            	}
						            	
						            	synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            		BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						            		if(childnode.getType() == BkChanYeLianTreeNode.DZHBK)
						            				getNodeKxianData (childnode);
							            	break;
						            	}
						            }
						        } else {
						        	long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTime);
//						            System.out.printf("Idle time was: %s seconds \n", seconds);
						            synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						            	idleTime=0;
						            	start =  System.currentTimeMillis();
						            	
						            	restartscreentips = true; counttoentertonewline = 0;
						            }
						        }
						        
						        synchronized (GetNodeDataFromDbWhenSystemIdle.class) {
						        	currLocation = newLocation;
//						        	globlenodeindex = i;
						        }
				        }
			        }
		    }
		    
		    System.out.println("......\n Done! Gotten DZH BanKuai Nodes  K Data At " + LocalTime.now() + "\r\n");
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

	Boolean restartscreentips = true;
	int counttoentertonewline = 0;
	private synchronized void getNodeZhanbiData (BkChanYeLianTreeNode node) 
	{
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");
		
		if(BkChanYeLianTreeNode.isBanKuai(node)) {
			if( !((BanKuai)node).getBanKuaiOperationSetting().isShowinbkfxgui() )
				return;
			
			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				return;
			
			if( ((TDXNodes)node).getShuJuJiLuInfo().wetherHasReiewedToday()   )
				return;
			
			LocalDate jlmaxdate = ((BanKuai)node).getShuJuJiLuInfo().getJyjlmaxdate();
			LocalDate jlmindate = ((BanKuai)node).getShuJuJiLuInfo().getJyjlmindate();
			if(jlmaxdate == null && jlmindate == null)
				return; //bankuai without data records should not be displayed
			
			animation ();
			System.out.print("Buffering BanKuai" + node.getMyOwnCode() + node.getMyOwnName() + " Zhanbi\n");
			node = ((BanKuai)node).getServicesForNode(true).getNodeData( node, requirestart, exportdate,NodeGivenPeriodDataItem.WEEK,true);
			((BanKuai)node).getServicesForNode(false);

		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG) {
			if( ((TDXNodes)node).getShuJuJiLuInfo().wetherHasReiewedToday()   )
				return;
			
			animation ();
			
			System.out.print("Buffering Stock" + node.getMyOwnName() + " Zhanbi \n");
			SvsForNodeOfStock svsstk = new SvsForNodeOfStock  ();
			node = (Stock) svsstk.getNodeData( (Stock)node,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
			svsstk = null;
		}
		System.gc();
	}
	
	private synchronized void getNodeKxianData (BkChanYeLianTreeNode node) 
	{
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");
		
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			if( !((BanKuai)node).getBanKuaiOperationSetting().isShowinbkfxgui() )
				return;
			
			if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				return;
			
			animation ();
			System.out.print("Buffering BanKuai" + node.getMyOwnCode() + node.getMyOwnName() + " KXian\n");
			node = ((BanKuai)node).getServicesForNode(true).getNodeKXian( node, requirestart, exportdate,NodeGivenPeriodDataItem.DAY,true);
			((BanKuai)node).getServicesForNode(false);
		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG) {
			animation ();
			System.out.print("Buffering Stock" + node.getMyOwnName() + " KXian\n");
			SvsForNodeOfStock svsstk = new SvsForNodeOfStock  ();
			node = (Stock) svsstk.getNodeKXian( (Stock)node,requirestart,exportdate,NodeGivenPeriodDataItem.DAY,true);
			svsstk = null;
		}
		System.gc(); 
	}
	
	private void animation ()
	{
		if(counttoentertonewline == 0) {
			System.out.print('\b');
			System.out.print("-" );
			counttoentertonewline ++;
		} else if(counttoentertonewline == 1) {
			System.out.print('\b');
			System.out.print("\\" );
			counttoentertonewline ++;
		} else if(counttoentertonewline == 2) {
			System.out.print('\b');
			System.out.print("|" );
			counttoentertonewline ++;
		} else if(counttoentertonewline == 3) {
			System.out.print('\b');
			System.out.print("/" );
			counttoentertonewline = 0;
		}
	}
			

}
