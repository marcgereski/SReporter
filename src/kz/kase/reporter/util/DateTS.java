package kz.kase.reporter.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateTS implements Comparable<DateTS> {

    private int year;
    private int month;
    private int day;

    public DateTS(long date) {
        if (date > 0) {
            year = (int) (date >> 16);
            month = (int) ((date >> 8) & 0xFF);
            day = (int) (date & 0xFF);
        }
    }

    public DateTS(Date date) {
        this(calendarFromDate(date));
    }

    public DateTS(Calendar cal) {
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
    }


    public DateTS(int day, int mouth, int year) {
        this.year = year;
        this.month = mouth;
        this.day = day;
    }


    /**
     * Формат даты должен быть следующего вида день.месяц.год 01.12.08
     *
     * @param date
     */
    public DateTS(String date) throws NumberFormatException {
        int day;
        int month;
        int year;
        date = date.replace(".", ":");
        day = Integer.parseInt(date.split(":")[0]);
        month = Integer.parseInt(date.split(":")[1]);
        year = Integer.parseInt(date.split(":")[2]);

        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public Integer getInt() {
        return converToInt(day, month, year);
    }

    public String toString() {
        if (year <= 0) {
            return "--,--,----";
        }
//        return String.format("%04d-%02d-%02d", year, month, day);
        return String.format("%02d-%02d-%04d", day, month, year);
    }


    public Calendar getCalendar() {
        if (year == 0 && month == 0 && day == 0) {
            return null;
        }
        return new GregorianCalendar(year, month - 1, day);
    }

    public Date getDate() {
        Calendar cal = getCalendar();
        return cal != null ? cal.getTime() : null;
    }

    /**
     * Прибавляет либо отнимает опеределенное кол-во дней
     *
     * @param count_day - положительное либо отрицательное значение (кол-во дней)
     */
    public void addDay(int count_day) {
        Calendar current = Calendar.getInstance();
        current.set(getYear(), getMonth() - 1, getDay());
        current.add(Calendar.DATE, count_day);
        this.year = current.get(Calendar.YEAR);
        this.month = current.get(Calendar.MONTH) + 1;
        this.day = current.get(Calendar.DATE);
    }

    /**
     * Вычитает из хранимой даты передоваемую дату в аргументах
     *
     * @param sub_date
     * @return - кол-во дней
     */
    public int SubDate(DateTS sub_date) {
        Calendar old_date = Calendar.getInstance();
        old_date.set(sub_date.getYear(), sub_date.getMonth() - 1, sub_date.getDay());
        Calendar current = Calendar.getInstance();
        current.set(getYear(), getMonth() - 1, getDay());
        int year = current.get(Calendar.YEAR) - old_date.get(Calendar.YEAR);
        int view_day = 0;
        if (year > 0) {
            view_day += (old_date.getMaximum(Calendar.DAY_OF_YEAR) - old_date.get(Calendar.DAY_OF_YEAR));
            year--;
            old_date.add(Calendar.YEAR, 1);
        }
        for (int i = 0; i < year; i++) {
            view_day += old_date.getMaximum(Calendar.DAY_OF_YEAR);
            old_date.add(Calendar.YEAR, 1);
        }
        view_day += current.get(Calendar.DAY_OF_YEAR);
        return view_day;
    }

    public boolean equals(Object obj) {
        return ((Integer) converToInt(day, month, year)).equals(((DateTS) obj).getInt());
    }

    public int compareTo(DateTS obj) {
        return ((Integer) converToInt(day, month, year)).compareTo(obj.getInt());
    }

    public static int compare(long first, long second) {
        Date date1 = new DateTS(first).getDate();
        Date date2 = new DateTS(second).getDate();
        if (date1 == null || date2 == null) return 0;
        return date1.compareTo(date2);
    }

    public static int converToInt(int day, int month, int year) {
        int i = day & 0xFF;
        i += month << 8;
        i += year << 16;
        return i;
    }

    public static Calendar calendarFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}