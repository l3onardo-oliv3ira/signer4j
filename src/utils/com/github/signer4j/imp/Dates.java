package com.github.signer4j.imp;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Dates {
	private Dates() {}
	
	public static final Locale BRAZIL = new Locale("pt", "BR");
	
  public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

  public static final ZoneId UTC_ZONEID = UTC_TIMEZONE.toZoneId();

  public static final ZoneOffset UTC_ZONEOFFSET = ZoneOffset.UTC;

  public static boolean isBetween(LocalTime target, LocalTime begin, LocalTime end) {
    return target.isAfter(begin) && target.isBefore(end);
  }

  public static LocalDate localDate(long date, ZoneId zoneId) {
    return localDateTime(date, zoneId).toLocalDate();
  }

  public static LocalDateTime localDateTime(long date, ZoneId zoneId) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), zoneId);
  }
  
	public static LocalDateTime LocalDateTime(long date) {
		return localDateTime(date, UTC_ZONEID);
	}
	
	public static String stringNow() {
	  return format("yyyy_MM_dd_HH'h'mm'm'ss's'S'ms'", new Date());
	}
	
  public static String defaultFormat(Date date) {
    return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", BRAZIL).format(date);
  }

  public static String format(String format, Date date) {
    return new SimpleDateFormat(format, BRAZIL).format(date);
	}
	
}
