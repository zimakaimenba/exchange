package com.exchangeinfomanager.commonlib;

import java.time.LocalDate;

public class Season 
{
	private static final String seasons[] = {
			 "", "Winter", "Winter", "Spring", "Spring", "Summer", "Summer", 
			  "Summer", "Summer", "Fall", "Fall", "Winter", "Winter"
			};
	private static final String seasonsname[] = {
			 "", "ONE", "ONE", "ONE", "TWO", "TWO", "TWO", 
			  "THREE", "THREE", "THREE", "FOUR", "FOUR", "FOUR"
			};
	
	private static final String seasonsstartdate[] = { "",
			  "-01-01", "-01-01", "-01-01", 
			  "-04-01", "-04-01", "-04-01", 
			  "-07-01", "-07-01", "-07-01", 
			  "-10-01", "-10-01", "-10-01"
			};
	private static final String seasonsenddate[] = {"",
			  "-03-31", "-03-31", "-03-31",
			  "-06-30", "-06-30", "-06-30", 
			  "-09-30", "-09-30", "-09-30",
			  "-12-31", "-12-31", "-12-31"
			};
	
			public static String getSeasonName (LocalDate date) 
			{
				return seasonsname[ date.getMonthValue()  ];
			}
			public static String getSeason( LocalDate date ) {
			   return seasons[ date.getMonthValue()  ];
			}
			public static LocalDate getSeasonStartDate( LocalDate date ) {
			    String result = date.getYear() +	seasonsstartdate[ date.getMonthValue()  ] 
			    		; 
			    
			    return LocalDate.parse(result);
			}
			public static LocalDate getSeasonEndDate( LocalDate date ) {
				String result = date.getYear() 	+	seasonsenddate[ date.getMonthValue()  ]
			    		; 
				 return LocalDate.parse(result);
			}
			public static LocalDate getLastSeasonStartDate( LocalDate date ) {
				int lasmonthvalue = date.getMonthValue() - 3;
				if(lasmonthvalue < 0) {
					lasmonthvalue = lasmonthvalue + 12;
					String result = date.getYear() -1 +	seasonsstartdate[ lasmonthvalue ] 		    		; 
				    return LocalDate.parse(result);
				} else {
					String result = date.getYear()  +	seasonsstartdate[ lasmonthvalue ] 		    		; 
				    return LocalDate.parse(result);
				}
			}
			public static LocalDate getLastSeasonEndDate( LocalDate date ) {
				int lasmonthvalue = date.getMonthValue() - 3;
				if(lasmonthvalue <= 0) {
					lasmonthvalue = lasmonthvalue + 11;
					String result = date.getYear() -1 +	seasonsenddate[ lasmonthvalue]		    		; 
					return LocalDate.parse(result);
				} else {
					String result = date.getYear() 	+	seasonsenddate[ lasmonthvalue]		    		; 
					return LocalDate.parse(result);
				}
				
			}

}
