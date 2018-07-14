package com.exchangeinfomanager.bankuaifengxi.ai;

import java.awt.Color;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaifengxi.ai.DaPanWeeklyFengXi;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.github.cjwizard.APageFactory;
import com.github.cjwizard.StackWizardSettings;
import com.github.cjwizard.WizardContainer;
import com.github.cjwizard.WizardListener;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.github.cjwizard.pagetemplates.TitledPageTemplate;

public class WeeklyFenXiWizard  extends JDialog {

	   private BkChanYeLianTreeNode dapan;
	/**
	    * Commons logging log instance
	    */
		public WeeklyFenXiWizard(String nodecodeshouldbedisplayed, int nodetype, LocalDate selectdate2)
		{
			this.selectdate = selectdate2;
			
			AllCurrentTdxBKAndStoksTree allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
			displaygegunode = allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecodeshouldbedisplayed, nodetype);
			
			dapan = allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000000", BanKuaiAndStockBasic.DAPAN);
			
			setupWizard ();
		}
		
	   public WeeklyFenXiWizard(BkChanYeLianTreeNode nodeshouldbedisplayed, LocalDate selectdate2)
	   {
	      // first, build the wizard.  The TestFactory defines the
	      // wizard content and behavior.
		  this.selectdate = selectdate2;
		  this.displaygegunode = nodeshouldbedisplayed;
		  
		  setupWizard ();
	   }
	   
//	   private final Logger log = LoggerFactory.getLogger(WeeklyFenXiWizard.class);
	   private static Logger log = Logger.getLogger(WeeklyFenXiWizard.class);
//		private AllCurrentTdxBKAndStoksTree allbksks;
//		private BanKuaiAndChanYeLian2 bkcyl;
		private LocalDate selectdate;

		private BkChanYeLianTreeNode displaygegunode;
		
	   /*
	    * 
	    */
	   private void setupWizard() 
	   {
		   	WeeklyFxGuiFactory wkfxfactory = new WeeklyFxGuiFactory();
		    final WizardContainer wc =
		         new WizardContainer(wkfxfactory,
		                             new TitledPageTemplate(),
		                             new StackWizardSettings());
		      
		      //do you want to store previously visited path and repeat it if you hit back
		      //and then go forward a second time?
		      //this options makes sense if you have a conditional path where depending on choice of a page
		      // you can visit one of two other pages.
		      wc.setForgetTraversedPath(true);
		      
		      // add a wizard listener to update the dialog titles and notify the
		      // surrounding application of the state of the wizard:
		      wc.addWizardListener(new WizardListener(){
		         @Override
		         public void onCanceled(List<WizardPage> path, WizardSettings settings) {
		            log.debug("settings: "+wc.getSettings());
		            WeeklyFenXiWizard.this.dispose();
		         }

		         @Override
		         public void onFinished(List<WizardPage> path, WizardSettings settings) {
		            log.debug("settings: "+wc.getSettings());
		            wkfxfactory.saveFenXiResult ();
		            WeeklyFenXiWizard.this.dispose();
		         }

		         @Override
		         public void onPageChanged(WizardPage newPage, List<WizardPage> path) {
		            log.debug("settings: "+wc.getSettings());
		            // Set the dialog title to match the description of the new page:
		            String description = newPage.getDescription();
		            WeeklyFenXiWizard.this.setTitle(description);
		         }

		        public void onPageChanging(WizardPage newPage, List<WizardPage> path) {
		            log.debug("settings: "+ wc.getSettings());
		            WeeklyFenXiWizard.this.dispose();
		        }
		      });
		      
		      // Set up the standard bookkeeping stuff for a dialog, and
		      // add the wizard to the JDialog:
		      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		      this.getContentPane().add(wc);
		      this.pack();
			
		}

	   /**
	    * Implementation of PageFactory to generate the wizard pages needed
	    * for the wizard.
	    */
	   private class WeeklyFxGuiFactory extends APageFactory
	   {
	      // To keep things simple, we'll just create an array of wizard pages:
		   WeeklyFengXiXmlHandler dpxmlhandler = new DaPanWeeklyFengXiXmlHandler ("000000",selectdate);
		   
		   
		   WeeklyFenXiWizardPage dpfx =  new DaPanWeeklyFengXi("", "大盘指数分析",dapan,selectdate,dpxmlhandler) {
				private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
               * @see com.github.cjwizard.WizardPage#updateSettings(com.github.cjwizard.WizardSettings)
               */
              @Override
              public void updateSettings(WizardSettings settings) {
//                 super.updateSettings(settings);
                 
                 // This is called when the user clicks next, so we could do
                 // some longer-running processing here if we wanted to, and
                 // pop up progress bars, etc.  Once this method returns, the
                 // wizard will continue.  Beware though, this runs in the
                 // event dispatch thread (EDT), and may render the UI
                 // unresponsive if you don't issue a new thread for any long
                 // running ops.  Future versions will offer a better way of
                 // doing this.
            	  
              }
           };
           
           GovAndBanKuaiWeeklyFengXi bkfx = new GovAndBanKuaiWeeklyFengXi ("", "政策面和板块分析",dapan,selectdate,dpxmlhandler);  
           
           GeGuWeeklyFengXiXmlHandler ggxmlhandler = new GeGuWeeklyFengXiXmlHandler (displaygegunode.getMyOwnCode(),selectdate);
           
           GeGuWeeklyFengXi ggfx = new GeGuWeeklyFengXi("", "个股/板块分析 <"+ displaygegunode.getMyOwnCode()+displaygegunode.getMyOwnName() + ">",
        		   displaygegunode,selectdate,ggxmlhandler)
           {
				private static final long serialVersionUID = 1L;

			/* (non-Javadoc)
	           * @see com.github.cjwizard.WizardPage#updateSettings(com.github.cjwizard.WizardSettings)
	           */
//	          @Override
//	          public void updateSettings(WizardSettings settings) {
//	//             super.updateSettings(settings);
//	             
//	             // This is called when the user clicks next, so we could do
//	             // some longer-running processing here if we wanted to, and
//	             // pop up progress bars, etc.  Once this method returns, the
//	             // wizard will continue.  Beware though, this runs in the
//	             // event dispatch thread (EDT), and may render the UI
//	             // unresponsive if you don't issue a new thread for any long
//	             // running ops.  Future versions will offer a better way of
//	             // doing this.
//	          }
          
           };
           
         
           
           WizardPage thirdpage = new WizardPage("Three", "Third Page") {
           {
                  add(new JLabel("Three!"));
                  setBackground(Color.BLUE.darker());
           }

               /**
                * This is the last page in the wizard, so we will enable the finish
                * button and disable the "Next >" button just before the page is
                * displayed:
                */
               public void rendering(List<WizardPage> path, WizardSettings settings) {
                  super.rendering(path, settings);
                  setFinishEnabled(true);
                  setNextEnabled(false);
               }
          };
          
          
	      private final WizardPage[] pages = {
	    		  dpfx,
	    		  bkfx,
	    		  ggfx,
	    		  thirdpage
	      };
	      private final WizardPage[] pageszhishu = {
	    		  dpfx,
	    		  bkfx,
	    		  thirdpage
	      };
	      
	      WizardGuiXmlHandlerController dpcontroller ;
	      WizardGuiXmlHandlerController ggcontroller ;

	      public WeeklyFxGuiFactory ()
	      {
	    	  List<WeeklyFenXiWizardPage> dpwizardpage = new ArrayList<WeeklyFenXiWizardPage> ();
	    	  List<WeeklyFenXiWizardPage> ggwizardpage = new ArrayList<WeeklyFenXiWizardPage> ();
	    	  
	    	  dpwizardpage.add(dpfx); 
	    	  dpwizardpage.add(bkfx);
	    	  
	    	  ggwizardpage.add(ggfx);
	    	  
	    	  dpcontroller = new WizardGuiXmlHandlerController (dpwizardpage,dpxmlhandler); //MVC MODEL
    		  ggcontroller = new WizardGuiXmlHandlerController (ggwizardpage,ggxmlhandler);
	      }
	      
	      /* (non-Javadoc)
	       * @see com.github.cjwizard.PageFactory#createPage(java.util.List, com.github.cjwizard.WizardSettings)
	       */
	      @Override
	      public WizardPage createPage(List<WizardPage> path, WizardSettings settings) 
	      {
	         log.debug("creating page "+path.size());
         
	         // Get the next page to display.  The path is the list of all wizard
	         // pages that the user has proceeded through from the start of the
	         // wizard, so we can easily see which step the user is on by taking
	         // the length of the path.  This makes it trivial to return the next
	         // WizardPage:
	         WizardPage page;
	         
	         if(displaygegunode.getType() == BanKuaiAndStockBasic.DAPAN /*|| displaygegunode.getType() == BanKuaiAndStockBasic.TDXBK*/)
	        	 page = pageszhishu[path.size()];
	         else
	        	 page = pages[path.size()];
	         // if we wanted to, we could use the WizardSettings object like a
	         // Map<String, Object> to change the flow of the wizard pages.
	         // In fact, we can do arbitrarily complex computation to determine
	         // the next wizard page.
	         
	         log.debug("Returning page: "+ page);
	         return page;
	      }
	      /*
	       * 
	       */
	      public void saveFenXiResult ()
	      {
	    	  if(displaygegunode.getType() == BanKuaiAndStockBasic.DAPAN /*|| displaygegunode.getType() == BanKuaiAndStockBasic.TDXBK*/) {
	    		  dpcontroller.saveFenXiResult();
	    	  }
	    	  else {
	    		  dpcontroller.saveFenXiResult();
	    		  ggcontroller.saveFenXiResult();
	    	  }
	      }
	      
	   }
	}
