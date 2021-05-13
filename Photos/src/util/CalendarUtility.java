package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utility class to contain helper functions
 *
 * @author Dev Patel and Eric Chan
 */
public class CalendarUtility {

	/**
	 * Method to convert a calendar instance to a string date representation
	 * @param cal calendar
	 * @return String representation of calendar
	 */
	public static String calendarToDate(Calendar cal) {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		return format1.format(cal.getTime());
	}

}
