package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.ServicesForNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.udojava.evalex.Expression;

public class ExportMatchedNode2
{
	private BanKuaiGeGuMatchCondition cond;
//	private SvsForNodeOfBanKuai svsbk;
//	private SvsForNodeOfStock svsstk;
	private LocalDate exportdate;

	public ExportMatchedNode2 (BanKuaiGeGuMatchCondition cond) 
	{
		this.cond = cond;
	}
	
	public Set<TDXNodes> checkTDXNodeMatchedCurSettingConditons (LocalDate exportdate, String period)
	{
		this.exportdate = exportdate;
		BanKuaiAndStockTree bkcyltree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
		int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
		
		Set<TDXNodes> matchednodeset = new HashSet<> ();
		Set<String> checkednodesset = new HashSet<String> ();
		
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
			//Yellow sign part
			Boolean yellowsignresult = checkNodeMatchedCurSettingConditonsOfYellowSign (childnode, exportdate, period);
			if(yellowsignresult != null  ) {
				if(!matchednodeset.contains(childnode)  && yellowsignresult ) matchednodeset.add((TDXNodes) childnode);
				if(!checkednodesset.contains(childnode.getMyOwnCode()))  checkednodesset.add( childnode.getMyOwnCode() );
				continue;
			} 
			
			//normal part
			if(childnode.getType() == BkChanYeLianTreeNode.TDXBK ) {
//				if( ! ((BanKuai)childnode).isImportdailytradingdata() )
//					continue;
//				if( ! ((BanKuai)childnode).isExportTowWlyFile() )
//					continue;
//				
//				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
//						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ 
//						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //�������и��ɵİ��
//					continue;
//				
//				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) 
//					continue;
//				
////				String checkresult = this.checkBanKuaiMatchedCurSettingConditons((BanKuai)childnode, exportdate, period);
//				checkednodesset.add( childnode.getMyOwnCode() );
//				
//				if(checkresult.toUpperCase().contains("MATCHED") ) {
//					if( !this.cond.shouldExportOnlyGeGuNotBanKuai() ) //ֻ����������
//						if(!matchednodeset.contains(childnode)) 
//							matchednodeset.add((TDXNodes) childnode);
//				}  
//					
//				if( this.cond.shouldExportOnlyBankuaiNotGeGu() )		continue;
//								
//				if( !checkresult.toUpperCase().contains("WITHCHECKGEGU") )			continue;
//				
//				childnode = ((SvsForNodeOfBanKuai)((BanKuai) childnode).getServicesForNode(true)).getAllGeGuOfBanKuai((BanKuai) childnode);
//				((BanKuai) childnode).getServicesForNode(false);
//				
//				Collection<BkChanYeLianTreeNode> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(exportdate,0);
//				if(nowbkallgg != null)
//					for (BkChanYeLianTreeNode ggstock : nowbkallgg) {
//						if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //�Ѿ�������stock�Ͳ����ˣ��ӿ��ٶ�
//							continue;
//						
//						Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)ggstock);
//						if(result && !matchednodeset.contains(childnode))	matchednodeset.add((TDXNodes) childnode);
//						if(!checkednodesset.contains(childnode))  checkednodesset.add( childnode.getMyOwnCode() );
//					}
			}
			
			if(childnode.getType() == BkChanYeLianTreeNode.TDXGG  ) { //ֻ���ڵ������а����ɵ�ʱ��Ż��õ����������ɶ���ǰ������
				if( checkednodesset.contains(childnode.getMyOwnCode() ) ) //�Ѿ�������stock�Ͳ����ˣ��ӿ��ٶ�
					continue;
				
				if(!this.cond.shouldExportST() && childnode.getMyOwnName() != null && childnode.getMyOwnName().toUpperCase().contains("ST"))
					continue;
				if(childnode.getMyOwnName()!= null &&  childnode.getMyOwnName().contains("��"))
					continue;
				
				if( this.cond.shouldExportOnlyYellowSignBkStk() ) // 
					continue;
				
				if( ((Stock)childnode).isVeryVeryNewXinStock(this.exportdate) )
					continue;
				
				Boolean result = this.checkNodesMatchedCurSettingConditions ((Stock)childnode);
				if(result && !matchednodeset.contains(childnode))	matchednodeset.add((TDXNodes) childnode);
				if(!checkednodesset.contains(childnode.getMyOwnCode() ) )  checkednodesset.add( childnode.getMyOwnCode() );
			}
		}
		
		checkednodesset = null;
		return matchednodeset;
	}
	/*
	 * ���Ʊ�
	 */
	private Boolean checkNodeMatchedCurSettingConditonsOfYellowSign(BkChanYeLianTreeNode childnode, LocalDate exportdate,
			String period) 
	{
		if(this.cond.shouldExportOnlyYellowSignBkStk() ) {
			NodesTreeRelated filetree = childnode.getNodeTreeRelated ();
			if(!filetree.selfIsMatchModel(exportdate) )
				return false;
			else
				return true;
		} 
		
		return null;
	}
	/*
	 * �Ը��ɽ��м��
	 */
	private Boolean checkNodesMatchedCurSettingConditions (TDXNodes node)
	{
		Boolean checkresult = false;
		LocalDate requirestart = CommonUtility.getSettingRangeDate(exportdate,"large");

		ServicesForNode svsnode = node.getServicesForNode(true);
		try {
			node =  (TDXNodes) svsnode.getNodeData( node,requirestart,exportdate,NodeGivenPeriodDataItem.WEEK,true);
		} catch (java.lang.NullPointerException e) {	e.printStackTrace();}
		
		String formula = this.cond.getExportConditionFormula();
		if(formula.contains("CLOSEVSMA") || formula.contains("ZhangDieFu") || formula.contains("DAY")) 
			svsnode.getNodeKXian( node, requirestart, exportdate, NodeGivenPeriodDataItem.DAY,true);
		
		List<String> exportfactors = Splitter.on("AND").omitEmptyStrings().splitToList(formula);
		for(String factor : exportfactors) {
			String factoreq = factor.replaceAll(" ", "");
			
			ArrayList<String> vars = new ArrayList<String>();
            Pattern p = Pattern.compile("\'.*?\'", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(factor);
            while (m.find()) {
                vars.add(m.group());
            }
            
            for(String var : vars) {
            	LocalDate sltdate = null ; String datestr = null;
            	Pattern pdate = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE);
                Matcher mdate = pdate.matcher(var);
                while (mdate.find()) {
                	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                	sltdate = LocalDate.parse(mdate.group(), formatter);
                	datestr = mdate.group();
                }
                int indexofdate = var.indexOf(datestr);
            	String kw = var.substring(1, indexofdate);
            	String period = var.substring(indexofdate+8, var.length()-1);
                String sltvalue = getKeywordValue (node, kw,sltdate, period, factor);
                if(sltvalue == null) {
                	checkresult = false;
                	break;
                }
                	
                if(kw.equals("CLOSEVSMA") ) {
                	if(sltvalue.equals("true"))  checkresult = true;
                	else	checkresult = false;
                }
                else factoreq = factoreq.replace(var, sltvalue);
            }
            
            if(!factoreq.contains("CLOSEVSMA") ) 
            	try{
    		    	BigDecimal result = null; 
    		    	result = new Expression(factoreq).eval(); //https://github.com/uklimaschewski/EvalEx
    		    	String sesultstr = result.toString();
    			    if(sesultstr.equals("0"))   	checkresult = false;
    			    else   	checkresult = true;
    		    } catch (com.udojava.evalex.Expression.ExpressionException e) {
    		    	System.out.println(node.getMyOwnCode() + node.getMyOwnName() + "����������㹫ʽ:" + factor + "��������:"+ factoreq + "\n"); 
    		    	return false;}
            
            if(checkresult == false) 
            	break;
		}
		
		svsnode = null;
		return checkresult;
	}
	private String getKeywordValue(TDXNodes node, String kw, LocalDate selectdate, String curperiod,String factor)
	{
		  Object value = null;
		  factor = factor.replaceAll(" ", "");
		  NodeXPeriodData nodexdata = node.getNodeXPeroidData(curperiod);
		  
		  if(kw.equals("CLOSEVSMA")) {
			  int indexofeq = factor.trim().lastIndexOf("=");
			  int indexofend = factor.trim().lastIndexOf(")");
			  String maformula = factor.substring(indexofeq + 1, indexofend);
			  value =  nodexdata.getNodeDataByKeyWord( kw, selectdate,  maformula);
		  }
		  else 
			  value = nodexdata.getNodeDataByKeyWord(kw,selectdate,"");
		  
		  if(value != null)
			  return value.toString();
		  else
			  return null;
	}
	/*
	 * ������Ƿ�����趨
	 */
	public String checkBanKuaiMatchedCurSettingConditons (BanKuai node, LocalDate exportdate, String period)
	{
			String settingbk = this.cond.getSettingBanKuai();

			if( this.cond.shouldExportOnlyCurrentBanKuai()  && !Strings.isNullOrEmpty( settingbk ) )  {//���޵�ǰ���
				if(!node.getMyOwnCode().equals(settingbk))
					return "UNMATCH";
			}
			
			if( this.cond.shouldExportBankuaiInConfig ()) //ֻ����������������
				if( !((BanKuai)node).isExportTowWlyFile() )
					return "UNMATCH";
			
			if(this.cond.shouldExportOnlyGeGuNotBanKuai() && ! this.cond.shouldExportAllBanKuai() )
				return  "WITHCHECKGEGU";
			
			if(this.cond.shouldExportOnlyGeGuNotBanKuai() )
				return "UNMATCH";
			
			String checkresult = ""; 
			if( ! this.cond.shouldExportAllBanKuai()  )
				checkresult = "WITHCHECKGEGU";
			
			return checkresult;
	}
}
