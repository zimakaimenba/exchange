package com.exchangeinfomanager.commonlib;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;
import org.joda.time.Interval;

public class TimeIntervalUnion {

	public static Interval union( Interval firstInterval, Interval secondInterval ) 
	{
	    // Purpose: Produce a new Interval instance from the outer limits of any pair of Intervals.

	    // Take the earliest of both starting date-times.
	    DateTime start =  firstInterval.getStart().isBefore( secondInterval.getStart() )  ? firstInterval.getStart() : secondInterval.getStart();
	    // Take the latest of both ending date-times.
	    DateTime end =  firstInterval.getEnd().isAfter( secondInterval.getEnd() )  ? firstInterval.getEnd() : secondInterval.getEnd();
	    // Instantiate a new Interval from the pair of DateTime instances.
	    Interval unionInterval = new Interval( start, end );

	    return unionInterval;
	}
	
	public static List<Interval> getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
	{
		if(nodestart == null)	return null;
		
		List<Interval> result = new ArrayList<Interval> ();
		
		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = null;
		try {
			nodeinterval = new Interval(nodestartdt, nodeenddt);
		} catch ( java.lang.IllegalArgumentException e) {
			e.printStackTrace();
			return result;
		}
		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
		if(overlapinterval != null) {
			DateTime overlapstart = overlapinterval.getStart();
			DateTime overlapend = overlapinterval.getEnd();
			
			Interval resultintervalpart1 = null ;
			if(requiredstartday.isBefore(nodestart)) {
				DateTime overlapdate = overlapstart.minusDays(1); //如果是周末，保证到周五
				Property dayofwk = overlapdate.dayOfWeek();
				int dayofwknumber = dayofwk.get();
				if(dayofwknumber ==6 ) overlapdate = overlapdate.minusDays(1);
				else if(dayofwknumber ==7) overlapdate = overlapdate.minusDays(2);
					 
				resultintervalpart1 = new Interval(requiredstartdt,overlapdate);
			} 
			
			Interval resultintervalpart2 = null;
			if (requiredendday.isAfter(nodeend)) {
				DateTime overlapdate = overlapend.plusDays(1); //如果是周末，保证到周五
				Property dayofwk = overlapdate.dayOfWeek();
				int dayofwknumber = dayofwk.get();
				if(dayofwknumber ==6 )	overlapdate = overlapdate.plusDays(2);
				else if(dayofwknumber ==7)	overlapdate = overlapdate.plusDays(1);
				
				resultintervalpart2 = new Interval(overlapdate ,requiredenddt  );
			}
			if(resultintervalpart1 != null)
				result.add(resultintervalpart1);
			if(resultintervalpart2 != null)
				result.add(resultintervalpart2);
		}
		if(requiredinterval.abuts(nodeinterval)) {
			result.add(requiredinterval);
		}
		Interval gapinterval = requiredinterval.gap(nodeinterval);
		if(gapinterval != null) {
			result.add(requiredinterval);
			result.add(gapinterval);
		}
		
		return result;
	}

}
