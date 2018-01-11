package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import javax.swing.JPanel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.toedter.calendar.JDateChooser;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class ChanYeLianNews extends JPanel implements Cloneable{  
	
	private JTextField tfldtitle;
	private JTextField tfldKeyWords;
	private JTextField tfldslack;
	private JDateChooser dateChooser;
	private int newsid = -1;
	private boolean selectedinnewtable = false;
	private String newrelatedbankuai;
//	private static Logger logger = Logger.getLogger(ChanYeLianNews.class);

	/**
	 * Create the panel.
	 */
	public ChanYeLianNews() {
		
		dateChooser = new JDateChooser(new Date() );
		
		JLabel lblNewLabel = new JLabel("\u65B0\u95FB\u6807\u9898");
		
		tfldtitle = new JTextField();
		tfldtitle.setColumns(10);
		
		JLabel label = new JLabel("\u5F55\u5165\u65E5\u671F");
		
		JLabel lblNewLabel_1 = new JLabel("\u65B0\u95FB\u5173\u952E\u8BCD");
		
		tfldKeyWords = new JTextField();
		tfldKeyWords.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("SLACK\u94FE\u63A5");
		
		tfldslack = new JTextField();
		tfldslack.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(label)
							.addGap(187)
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 166, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(16)
							.addComponent(tfldtitle, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_1)
							.addGap(4)
							.addComponent(tfldKeyWords, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_2)
							.addGap(10)
							.addComponent(tfldslack, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(label))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(7)
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(lblNewLabel))
						.addComponent(tfldtitle, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1)
						.addComponent(tfldKeyWords, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(9)
							.addComponent(lblNewLabel_2))
						.addComponent(tfldslack, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)))
		);
		setLayout(groupLayout);

	}
	
//	   public Object clone() {  
//		   ChanYeLianNews newnews = null;  
//	        try{  
//	        	newnews = (ChanYeLianNews)super.clone();  
//	        }catch(CloneNotSupportedException e) {  
//	            e.printStackTrace();  
//	        }  
//	        return newnews;  
//	    } 
	public void resetInput ()
	{
		 tfldtitle.setText("");;
		 tfldKeyWords.setText("");
		 tfldslack.setText("");
	}
	/**
	 * @return the generatedate
	 */
	public LocalDate getGenerateDate() {
		try {
			return dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @param generatedate the generatedate to set
	 */
	public void setGenerateDate(LocalDate generatedate) {
		dateChooser.setDate(Date.from(generatedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}
	/**
	 * @return the newstitle
	 */
	public String getNewsTitle() {
		return tfldtitle.getText();
	}
	/**
	 * @param newstitle the newstitle to set
	 */
	public void setNewsTitle(String newstitle) {
		this.tfldtitle.setText(newstitle );
	}
	/**
	 * @return the newsSlackUrl
	 */
	public String getNewsSlackUrl() {
		return tfldslack.getText();
	}
	/**
	 * @param newsSlackUrl the newsSlackUrl to set
	 */
	public void setNewsSlackUrl(String newsSlackUrl) {
		this.tfldslack.setText( newsSlackUrl );
	}

	public String getKeyWords() {
		// TODO Auto-generated method stub
		return tfldKeyWords.getText();
	}
	public void setKeyWords (String keywords)
	{
		tfldKeyWords.setText(keywords);
	}
	public void setNewsId (int newsid2)
	{
		this.newsid = newsid2;
	}
	public int getNewsId ()
	{
		return this.newsid ;
	}
	public boolean isSelected ()
	{
		return this.selectedinnewtable;
	}
	public void setIsSelected (boolean selected)
	{
		this.selectedinnewtable = selected;
	}
	public String getNewsRelatedBanKuai ()
	{
		return newrelatedbankuai;
	}
	public void setNewsRelatedBanKuai (String relatedbk) 
	{
		newrelatedbankuai = relatedbk;
	}

}
