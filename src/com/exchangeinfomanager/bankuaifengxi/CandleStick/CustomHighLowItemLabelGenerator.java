package com.exchangeinfomanager.bankuaifengxi.CandleStick;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @author ashraf
 *
 */
@SuppressWarnings("serial")
public class CustomHighLowItemLabelGenerator extends HighLowItemLabelGenerator 
{

	/** The date formatter. */
	private DateFormat dateFormatter;

	/** The number formatter. */
	private NumberFormat numberFormatter;

	/**
	 * Creates a tool tip generator using the supplied date formatter.
	 *
	 * @param dateFormatter
	 *            the date formatter (<code>null</code> not permitted).
	 * @param numberFormatter
	 *            the number formatter (<code>null</code> not permitted).
	 */
	public CustomHighLowItemLabelGenerator(DateFormat dateFormatter, NumberFormat numberFormatter) {
		if (dateFormatter == null) {
			throw new IllegalArgumentException("Null 'dateFormatter' argument.");
		}
		if (numberFormatter == null) {
			throw new IllegalArgumentException("Null 'numberFormatter' argument.");
		}
		this.dateFormatter = dateFormatter;
		this.numberFormatter = numberFormatter;
	}

	/**
	 * Generates a tooltip text item for a particular item within a series.
	 *
	 * @param dataset
	 *            the dataset.
	 * @param series
	 *            the series (zero-based index).
	 * @param item
	 *            the item (zero-based index).
	 *
	 * @return The tooltip text.
	 */
	@Override
	public String generateToolTip(XYDataset dataset, int series, int item) {

		String result = null;
		String htmlstring = "";

		if (dataset instanceof OHLCDataset) {
			OHLCDataset d = (OHLCDataset) dataset;
			Number high = d.getHigh(series, item);
			Number low = d.getLow(series, item);
			Number open = d.getOpen(series, item);
			Number close = d.getClose(series, item);
			Number x = d.getX(series, item);

			result = d.getSeriesKey(series).toString();
			
			
			if (x != null) {
				org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
				Elements body = doc.getElementsByTag("body");
				for(Element elbody : body) {
					 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
					 
					 Date date = new Date(x.longValue());
					 org.jsoup.nodes.Element li3 = dl.appendElement("li");
					 li3.appendText(this.dateFormatter.format(date));
					 
					 org.jsoup.nodes.Element li = dl.appendElement("li");
					 li.appendText(" H=" + this.numberFormatter.format(high.doubleValue() ) );
					 
					 org.jsoup.nodes.Element li2 = dl.appendElement("li");
					 li2.appendText(" L=" + this.numberFormatter.format(low.doubleValue()) );
					 
					 org.jsoup.nodes.Element li4 = dl.appendElement("li");
					 li4.appendText( " O=" + this.numberFormatter.format(open.doubleValue()) );
					 
					 
					 org.jsoup.nodes.Element li5 = dl.appendElement("li");
					 li5.appendText(" C=" + this.numberFormatter.format(close.doubleValue()) );
					
					
				}
				
				htmlstring = doc.toString();
			}

//			if (x != null) {
//				Date date = new Date(x.longValue());
//				result = result + "--> Time=" + this.dateFormatter.format(date);
//				if (high != null) {
//					result = result + " H=" + this.numberFormatter.format(high.doubleValue());
//				}
//				if (low != null) {
//					result = result + " L=" + this.numberFormatter.format(low.doubleValue());
//				}
//				if (open != null) {
//					result = result + " O=" + this.numberFormatter.format(open.doubleValue());
//				}
//				if (close != null) {
//					result = result + " C=" + this.numberFormatter.format(close.doubleValue());
//				}
//			}

		}

		return htmlstring;

	}

}
