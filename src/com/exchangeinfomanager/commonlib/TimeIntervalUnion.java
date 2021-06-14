package com.exchangeinfomanager.commonlib;

import org.joda.time.DateTime;
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

}
