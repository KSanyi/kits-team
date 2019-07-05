package hu.kits.team.common;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DateInterval implements Comparable<DateInterval> {

    private static final LocalDate MAX = LocalDate.of(2050,1,1);
    
	public static DateInterval defaultValue() {
	    return new DateInterval(Clock.today().withDayOfYear(1), Clock.today());
	}
	
	public static DateInterval thisMonth(LocalDate today) {
        return new DateInterval(today.withDayOfMonth(1), today.plusMonths(1).withDayOfMonth(1).minusDays(1));
    }
	
	public static DateInterval thisYear(LocalDate today) {
        return new DateInterval(today.withDayOfYear(1), today);
    }
	
	public static DateInterval openUntilToday() {
        return new DateInterval(LocalDate.MIN, Clock.today());
    }
	
	public LocalDate from;
	
	public LocalDate to;

	public DateInterval(LocalDate from, LocalDate to) {
		if(from == null) from = LocalDate.MIN;
		if(to == null) to = MAX;
		if(to.isBefore(from)) {
			throw new IllegalArgumentException("Invalid interval: " + from + " - " + to);
		}
		this.from = from;
		this.to = to;
	}
	
	public boolean contains(LocalDate value) {
		return !value.isBefore(from) && !value.isAfter(to);
	}
	
	public boolean contains(DateInterval other) {
        return !this.from.isAfter(other.from) && !this.to.isBefore(other.to);
    }
	
	public List<LocalDate> days() {
	    List<LocalDate> days = new ArrayList<>();
	    LocalDate date = from;
	    while(!date.isAfter(to)) {
	        days.add(date);
	        date = date.plusDays(1);
	    }
	    return Collections.unmodifiableList(days);
	}
	
	public int numberOfDays() {
	    return (int)Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other == this) return true;
		if(other == null || !(other instanceof DateInterval)) return false;
		DateInterval otherInterval = (DateInterval)other;
		return from == otherInterval.from && to == otherInterval.to;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}
	
	@Override
    public String toString() {
		String fromString = from .equals(LocalDate.MIN) ? "" : from.toString();
		String toString = from .equals(MAX) ? "" : to.toString();
        return "[" + fromString + " - " + toString + "]";
    }

	@Override
	public int compareTo(DateInterval other) {
		return from.compareTo(other.from);
	}
	
}
	
