package com.example.registro_cuentas;

import android.content.Context;

import com.example.registro_cuentas.db.Fecha;
import com.example.registro_cuentas.db.dao.DaoDat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcCalendar {
    public CalcCalendar() {
    }

    public static String dataConverted(String text, int selec) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Convierte Sting  a forrmato de fecha
            LocalDate date = LocalDate.parse(text);
            //Inicia la fecha actual
            LocalDate currdate = LocalDate.now();

            long vlresult = 0;
            //Para años
            if (selec == 0) {
                vlresult = ChronoUnit.YEARS.between(date, currdate);
            }
            //Para meses
            else if (selec == 1) {
                vlresult = ChronoUnit.MONTHS.between(date, currdate);
            }
            //Para Dias
            else if (selec == 2) {
                vlresult = ChronoUnit.DAYS.between(date, currdate);
            }
            //Para Formato de fecha
            else if (selec == 3) {
                Period result = date.until(currdate);
                return result.getDays() + "-" + result.getMonths() + "-" + result.getYears();
            }
            return "" + (vlresult < 0 ? 1 : vlresult);
        }
        return "1";
    }

    public static String[] dataValidate(String text) {
        Pattern patt = Pattern.compile("(^(\\d{1,2})(/)(\\d{1,2})(/)(\\d{1,3})$)|(^(\\d{1,2})(-)(\\d{1,2})(-)(\\d{1,3})$)|(^(\\d{1,2})(\\.)(\\d{1,2})(\\.)(\\d{1,3})$)");
        Matcher matcher = patt.matcher(text);
        if (matcher.find()) {
            if (text.contains("-")) {
                return text.split("-");
            } else if (text.contains("/")) {
                return text.split("/");
            } else if (text.contains(".")) {
                return text.split("\\.");
            } else {
                return null;
            }
        }
        return null;
    }

    public static String getTime(String value) {
        String text = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalTime mTime = LocalTime.parse(value);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return formatter.format(mTime);
        }
        return text;
    }

    public static void addCurrentMonthIfAbsent(Context mContext) {
        DaoDat daoFecha = StartVar.appDBall.daoDat();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Inicia la fecha actual
            LocalDate currdate = LocalDate.now();
            List<Fecha> listFecha = daoFecha.getUsers();
            boolean exists = false;
            for (Fecha d : listFecha) {
                String f = d.date;
                LocalDate date = LocalDate.parse(f);
                if (currdate.getMonth().equals(date.getMonth()) && currdate.getYear() == date.getYear()) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                return;
            }
            LocalTime currtime = LocalTime.now();
            Fecha obj = new Fecha("dateID" + listFecha.size(), "" + currdate.getYear(),
                    currdate.getMonth().toString(), "" + currdate.getDayOfMonth(),
                    CalcCalendar.getTime(currtime.toString()), currdate.toString());
            daoFecha.insertUser(obj);
            // Recarga la lista de la DB ----------------------------
            StartVar var = new StartVar(mContext);
            var.getFecListDB();
            // -------------------------------------------------------
        }
    }

    public static int getRangeMultiple(String txDate, int selec) {
        long num = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (!txDate.isEmpty()) {
                //Convierte Sting  a forrmato de fecha
                LocalDate date = LocalDate.parse(txDate);
                //Inicia la fecha actual
                LocalDate currdate = LocalDate.now();
                //Para Dias
                if (selec == 1) {
                    num = ChronoUnit.DAYS.between(date, currdate);
                }
                //Para meses
                else if (selec == 2) {
                    LocalDate mDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
                    num = ChronoUnit.MONTHS.between(mDate, currdate);
                }
                //Para años
                else if (selec == 3) {
                    num = ChronoUnit.YEARS.between(date, currdate);
                }
            }
        }
        //Basic.msg(""+num);
        return (int)num;
    }

    public static Object[] dateToMoney(String startDate, int select, float rent, float paid) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (rent <= 0) {
                return null;
            }
            if (!startDate.isEmpty()) {
                LocalDate originalDate = LocalDate.parse(startDate);
                LocalDate currdate = LocalDate.now();

                int result = currdate.compareTo(originalDate);
                if (result < 1) {
                    return null;
                }

                long numOwed = 0;
                if (select == 1) {
                    // Días: solo completos transcurridos (sin extra)
                    numOwed = ChronoUnit.DAYS.between(originalDate, currdate);
                } else if (select == 2) {
                    // Meses: incluye período actual iniciado
                    originalDate = LocalDate.of(originalDate.getYear(), originalDate.getMonth(), 1);
                    numOwed = ChronoUnit.MONTHS.between(originalDate, currdate);
                } else if (select == 3) {
                    originalDate = LocalDate.of(originalDate.getYear(), 1, 1);
                    numOwed = ChronoUnit.YEARS.between(originalDate, currdate);
                } else {
                    return new Object[]{0f, 0f, "", 1};
                }
                if (numOwed < 1) {
                    numOwed = 0;
                }

                // Simula pagos desde la fecha original (garantiza date >= startDate)
                LocalDate date = originalDate;
                int count = 0;
                float currentPaid = paid;
                //Basic.msg("currentPaid "+currentPaid+" numOwed: "+numOwed+ "count: "+count);

                for (long i = numOwed; i > 0; i--) {
                    if (currentPaid >= rent) {
                        currentPaid -= rent;
                        if (select == 1) {
                            date = date.plusDays(1);
                        } else if (select == 2) {
                            date = date.plusMonths(1);
                        } else {  // select == 3
                            date = date.plusYears(1);
                        }
                    } else {
                        count++;
                    }
                }
                //Basic.msg("startDate "+startDate+" date: "+date.toString()+ "count: "+count);

                float debt = Math.max(0f, (rent * count) - currentPaid);
                return new Object[]{debt, currentPaid, date.toString(), 0};
            }
        }
        return null;
    }

    public static String getDatePlus(String txDate, int sum, int selec) {
        String newDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (!txDate.isEmpty()) {
                //Convierte Sting  a forrmato de fecha
                LocalDate date = LocalDate.parse(txDate);
                //Para Dias
                if (selec == 1) {
                    newDate = date.plusDays(sum).toString();
                }
                //Para meses
                else if (selec == 2) {
                    newDate = date.plusMonths(sum).toString();
                }
                //Para años
                else if (selec == 3) {
                    newDate = date.plusYears(sum).toString();
                }
            }
        }
        return newDate;
    }
//    public static String getCorrectDate(String txDate, int minus, int selec) {
//        String newDate = "";
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            if (!txDate.isEmpty()) {
//                //Convierte Sting  a forrmato de fecha
//                LocalDate date = LocalDate.parse(txDate);
//                //Para Dias
//                if (selec == 1) {
//                    newDate = date.minusDays(minus).toString();
//                }
//                //Para meses
//                else if (selec == 2) {
//                    newDate = date.minusMonths(minus).toString();
//                }
//                //Para años
//                else if (selec == 3) {
//                    newDate = date.minusYears(minus).toString();
//                }
//            }
//        }
//        return newDate;
//    }

    public static String getCorrectDate(String txDate, int selec) {
        String newDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (!txDate.isEmpty()) {
                //Convierte Sting  a forrmato de fecha
                LocalDate date = LocalDate.parse(txDate);
                //Basic.msg("selec"+selec+" txDate "+txDate);
                //Para Dias
                if (selec == 1) {
                    return txDate;
                }
                //Para meses
                else if (selec == 2) {
                    newDate = LocalDate.of(date.getYear(), date.getMonth(), 1).toString();

                }
                //Para años
                else if (selec == 3) {
                    newDate = LocalDate.of(date.getYear(), 1, 1).toString();
                }
            }
        }
        return newDate;
    }
}
