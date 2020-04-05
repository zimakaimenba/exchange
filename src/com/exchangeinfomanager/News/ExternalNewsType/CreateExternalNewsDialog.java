package com.exchangeinfomanager.News.ExternalNewsType;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;


public class CreateExternalNewsDialog  extends ExternalNewsDialog<ExternalNewsType> {

//  private News News;

  public CreateExternalNewsDialog(ServicesForNews NewsService)
  {
      super(NewsService);
      super.setTitle("Create");
      
      // lets create button and add controller to it
      JLabel createButton = JLabelFactory.createOrangeButton("Create");
      createButton.addMouseListener(new CreateController());
      
      // add button to the main panel
      JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new FlowLayout(FlowLayout.RIGHT), 35);
      layoutPanel.add(createButton);
      super.centerPanel.add(layoutPanel);
      super.centerPanel.add(Box.createVerticalStrut(PADDING));
  }
  /*
   * (non-Javadoc)
   * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsDialog#setNews(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.News)
   */
  public Boolean setNews(ExternalNewsType event)
  {
  	super.setNews( event);
  	return true;
  }
  /*
   * 
   */
  private class CreateController extends MouseAdapter {

      @Override
      public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);

            ExternalNewsType mt = getNews();
          	if(mt.getTitle().length() >150) {
          		JOptionPane.showMessageDialog(null,"新闻标题过长！");
          		setVisible(true);
          		return;
          	} else
          	if(mt.getStart().isAfter(mt.getEnd() )) {
          		JOptionPane.showMessageDialog(null,"起始日期晚于截止日期，不合理！");
          		setVisible(true);
          		return;
          	}
          	 
          		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
          		SvsForNodeOfStock svsstock = new SvsForNodeOfStock (); 
          		SvsForNodeOfDaPan svsdapan = new SvsForNodeOfDaPan (); 
          		
          		BkChanYeLianTreeNode bk = svsbk.getNode(mt.getNewsOwnerCodes());
          		BkChanYeLianTreeNode stock = svsstock.getNode(mt.getNewsOwnerCodes());
          		BkChanYeLianTreeNode dapan = svsdapan.getNode(mt.getNewsOwnerCodes());
          		
          		if(bk == null && stock == null && dapan == null) {
          			JOptionPane.showMessageDialog(null,"板块或个股代码不存在！");
              		setVisible(true);
              		return;
          		}
          		
          		if(bk != null)
          			mt.setNode(bk);
          		else if(stock != null)
          			mt.setNode(stock);
          		else if(dapan != null)
          			mt.setNode(dapan);
          		
          		Boolean check = checkDuplicate (mt);
                if(check) {
                	JOptionPane.showMessageDialog(null,"信息已经在系统中，无需重复添加！");
                	return;
                }
                
                try {
                	NewsService.createNews(mt);
                	setVisible(false);
                } catch (com.mysql.jdbc.MysqlDataTruncation e2) {
                	e2.printStackTrace();
                }catch (SQLException e1) {
                	e1.printStackTrace();
                }
      }
      
      private Boolean checkDuplicate(ExternalNewsType ExternalNewsType)
	  {
    	 Collection<News> curnews = NewsService.getCache().produceNews();
    	 return false;
	  }
      
  }
}
