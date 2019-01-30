package com.ptoceti.osgi.obix.impl.transverse;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION : ptoceti
 * PROJECT : Obix-Lib
 * FILENAME : Iso8601DateParser.java
 * 
 * This file is part of the Ptoceti project. More information about
 * this project can be found here: http://www.ptoceti.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2013 - 2015 ptoceti
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

//DateParser.java
//$Id: DateParser.java,v 1.5 2005/05/16 10:19:19 ylafon Exp $
//(c) COPYRIGHT MIT, INRIA and Keio, 2000.
//Please first read the full copyright statement in file COPYRIGHT.html

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Date parser for ISO 8601 format
 * http://www.w3.org/TR/1998/NOTE-datetime-19980827
 * 
 * @version $Revision: 1.5 $
 * @author Beno�t Mah� (bmahe@w3.org)
 * @author Yves Lafon (ylafon@w3.org)
 */
public class Iso8601DateParser {

	private static boolean check(StringTokenizer st, String token)

	{
		try {
			if (st.nextToken().equals(token)) {
				return true;
			} else {
				throw new RuntimeException("Missing [" + token + "]");
			}
		} catch (NoSuchElementException ex) {
			return false;
		}
	}

	private static Calendar getCalendar(String isodate) {
		// YYYY-MM-DDThh:mm:ss.sTZD
		StringTokenizer st = new StringTokenizer(isodate, "-T:.+Z", true);

		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.clear();
		try {
			// Year
			if (st.hasMoreTokens()) {
				int year = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.YEAR, year);
			} else {
				return calendar;
			}
			// Month
			if (check(st, "-") && (st.hasMoreTokens())) {
				int month = Integer.parseInt(st.nextToken()) - 1;
				calendar.set(Calendar.MONTH, month);
			} else {
				return calendar;
			}
			// Day
			if (check(st, "-") && (st.hasMoreTokens())) {
				int day = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.DAY_OF_MONTH, day);
			} else {
				return calendar;
			}
			// Hour
			if (check(st, "T") && (st.hasMoreTokens())) {
				int hour = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.HOUR_OF_DAY, hour);
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar;
			}
			// Minutes
			if (check(st, ":") && (st.hasMoreTokens())) {
				int minutes = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.MINUTE, minutes);
			} else {
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar;
			}

			//
			// Not mandatory now
			//

			// Secondes
			if (!st.hasMoreTokens()) {
				return calendar;
			}
			String tok = st.nextToken();
			if (tok.equals(":")) { // secondes
				if (st.hasMoreTokens()) {
					int secondes = Integer.parseInt(st.nextToken());
					calendar.set(Calendar.SECOND, secondes);
					if (!st.hasMoreTokens()) {
						return calendar;
					}
					// frac sec
					tok = st.nextToken();
					if (tok.equals(".")) {
						// bug fixed, thx to Martin Bottcher
						String nt = st.nextToken();
						while (nt.length() < 3) {
							nt += "0";
						}
						nt = nt.substring(0, 3); // Cut trailing chars..
						int millisec = Integer.parseInt(nt);
						// int millisec = Integer.parseInt(st.nextToken()) * 10;
						calendar.set(Calendar.MILLISECOND, millisec);
						if (!st.hasMoreTokens()) {
							return calendar;
						}
						tok = st.nextToken();
					} else {
						calendar.set(Calendar.MILLISECOND, 0);
					}
				} else {
					throw new RuntimeException("No secondes specified");
				}
			} else {
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
			// Timezone
			if (!tok.equals("Z")) { // UTC
				if (!(tok.equals("+") || tok.equals("-"))) {
					throw new RuntimeException("only Z, + or - allowed");
				}
				boolean plus = tok.equals("+");
				if (!st.hasMoreTokens()) {
					throw new RuntimeException("Missing hour field");
				}
				int tzhour = Integer.parseInt(st.nextToken());
				int tzmin = 0;
				if (check(st, ":") && (st.hasMoreTokens())) {
					tzmin = Integer.parseInt(st.nextToken());
				} else {
					throw new RuntimeException("Missing minute field");
				}
				if (plus) {
					calendar.add(Calendar.HOUR, -tzhour);
					calendar.add(Calendar.MINUTE, -tzmin);
				} else {
					calendar.add(Calendar.HOUR, tzhour);
					calendar.add(Calendar.MINUTE, tzmin);
				}
			}
		} catch (NumberFormatException ex) {
			throw new RuntimeException("[" + ex.getMessage()
					+ "] is not an integer");
		}
		return calendar;
	}

	/**
	 * Parse the given string in ISO 8601 format and build a Date object.
	 * 
	 * @param isodate
	 *            the date in ISO 8601 format
	 * @return a Date instance
	 */
	public static Date parse(String isodate) {
		Calendar calendar = getCalendar(isodate);
		return calendar.getTime();
	}

	private static String twoDigit(int i) {
		if (i >= 0 && i < 10) {
			return "0" + String.valueOf(i);
		}
		return String.valueOf(i);
	}

	/**
	 * Generate a ISO 8601 date
	 * 
	 * @param date
	 *            a Date instance
	 * @return a string representing the date in the ISO 8601 format
	 */
	public static String getIsoDate(Date date) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		StringBuffer buffer = new StringBuffer();
		buffer.append(calendar.get(Calendar.YEAR));
		buffer.append("-");
		buffer.append(twoDigit(calendar.get(Calendar.MONTH) + 1));
		buffer.append("-");
		buffer.append(twoDigit(calendar.get(Calendar.DAY_OF_MONTH)));
		buffer.append("T");
		buffer.append(twoDigit(calendar.get(Calendar.HOUR_OF_DAY)));
		buffer.append(":");
		buffer.append(twoDigit(calendar.get(Calendar.MINUTE)));
		buffer.append(":");
		buffer.append(twoDigit(calendar.get(Calendar.SECOND)));
		buffer.append(".");
		buffer.append(twoDigit(calendar.get(Calendar.MILLISECOND) / 10));
		buffer.append("Z");
		return buffer.toString();
	}

	/**
	 * Generate a ISO 8601 date
	 * 
	 * @param date
	 *            a Date instance
	 * @return a string representing the date in the ISO 8601 format
	 */
	public static String getIsoDateNoMillis(Date date) {
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.setTime(date);
		StringBuffer buffer = new StringBuffer();
		buffer.append(calendar.get(Calendar.YEAR));
		buffer.append("-");
		buffer.append(twoDigit(calendar.get(Calendar.MONTH) + 1));
		buffer.append("-");
		buffer.append(twoDigit(calendar.get(Calendar.DAY_OF_MONTH)));
		buffer.append("T");
		buffer.append(twoDigit(calendar.get(Calendar.HOUR_OF_DAY)));
		buffer.append(":");
		buffer.append(twoDigit(calendar.get(Calendar.MINUTE)));
		buffer.append(":");
		buffer.append(twoDigit(calendar.get(Calendar.SECOND)));
		buffer.append("Z");
		return buffer.toString();
	}

	public static void test(String isodate) {
		System.out.println("----------------------------------");
		Date date = parse(isodate);
		System.out.println(">> " + isodate);
		System.out.println(">> " + date.toString() + " [" + date.getTime()
				+ "]");
		System.out.println(">> " + getIsoDate(date));
		System.out.println("----------------------------------");
	}

	public static void test(Date date) {
		String isodate = null;
		System.out.println("----------------------------------");
		System.out.println(">> " + date.toString() + " [" + date.getTime()
				+ "]");
		isodate = getIsoDate(date);
		System.out.println(">> " + isodate);
		date = parse(isodate);
		System.out.println(">> " + date.toString() + " [" + date.getTime()
				+ "]");
		System.out.println("----------------------------------");
	}

	public static void main(String args[]) {
		test("1997-07-16T19:20:30.45-02:00");
		test("1997-07-16T19:20:30+01:00");
		test("1997-07-16T19:20:30+01:00");
		test("1997-07-16T12:20:30-06:00");
		test("1997-07-16T19:20");
		test("1997-07-16");
		test("1997-07");
		test("1997");
		test(new Date());
	}

}
