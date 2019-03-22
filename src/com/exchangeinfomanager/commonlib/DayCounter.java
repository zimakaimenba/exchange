package com.exchangeinfomanager.commonlib;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class DayCounter 
{
	public DayCounter () 
	{
//		ignore = Arrays.asList(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY);
	}
	
	static List<DayOfWeek> ignore;
    
//    public static void print(LocalDate date) {
//        System.out.printf("%s -> %s%n", date, date.getDayOfWeek());
//    }

    private static int simpleDaysBetween(final LocalDate start,
            final LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    private static int betterDaysBetween(final LocalDate start,
            final LocalDate end, final List<DayOfWeek> ignore) 
    {
        int count = 0;
        LocalDate curr = start.plusDays(0);

        while (curr.isBefore(end)) {
            if (!ignore.contains(curr.getDayOfWeek())) {
                count++;
            }
            curr = curr.plusDays(1); // Increment by a day.
        }

        return count;
    }

    public static int bestDaysBetweenIngoreWeekEnd(final LocalDate start,
            final LocalDate end)
    {
    	ignore = Arrays.asList(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY);
    	
        int days = simpleDaysBetween(start, end);

        if (days == 0) {
            return 0;
        }
        
        if (!ignore.isEmpty()) {
            int weeks = days / 7;
            int startDay = start.getDayOfWeek().getValue();
            int endDay = end.getDayOfWeek().getValue();
            int diff = weeks * ignore.size();

            for (DayOfWeek day : ignore) {
                int currDay = day.getValue();
                if (startDay <= currDay) {
                    diff++;
                }
                if (endDay > currDay) {
                    diff++;
                }
            }

            if (endDay > startDay) {
                diff -= endDay - startDay;
            }

            return days - diff;
        }

        return days;
    }

}