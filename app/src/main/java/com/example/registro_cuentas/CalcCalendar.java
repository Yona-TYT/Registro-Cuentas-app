package com.example.registro_cuentas;

import static com.example.registro_cuentas.StartVar.appDBfecha;

import android.content.Context;
import android.widget.Toast;

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

    public static void startCalList(Context mContext) {
        //Inicia la fecha actual
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate currdate = LocalDate.now();
            AppDBfec appDBfecha = StartVar.appDBfecha;
            List<Fecha> listFecha = appDBfecha.daoUser().getUsers();
            for (Fecha d : listFecha){
                String f = d.date;
                LocalDate date = LocalDate.parse(f);
                if (currdate.getMonth().equals(date.getMonth()) && currdate.getYear() == date.getYear()) {
                    return;
                }
            }
            LocalTime currtime = LocalTime.now();
            Fecha obj = new Fecha("dateID" + (listFecha.size() - 1), "" + currdate.getYear(), currdate.getMonth().toString(), "" + currdate.getDayOfMonth(), CalcCalendar.getTime(currtime.toString()), currdate.toString());
            appDBfecha.daoUser().insetUser(obj);
            //Recarga La lista de la DB ----------------------------
            StartVar var = new StartVar(mContext);
            var.getFecListDB();
            //-------------------------------------------------------
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
    public static String getDateMinus(String txDate, int minus, int selec) {
        String newDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (!txDate.isEmpty()) {
                //Convierte Sting  a forrmato de fecha
                LocalDate date = LocalDate.parse(txDate);
                //Para Dias
                if (selec == 1) {
                    newDate = date.minusDays(minus).toString();
                }
                //Para meses
                else if (selec == 2) {
                    newDate = date.minusMonths(minus).toString();
                }
                //Para años
                else if (selec == 3) {
                    newDate = date.minusYears(minus).toString();
                }
            }
        }
        return newDate;
    }
}



//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private LocalDate validateDate(int year, int moth, int day){
//        Log.d("PhotoPicker", "1-->>>>>>>>>>>>>>>>>>>>>>>>>>>> year: " + year + " mes: "+ moth);
//
//        //Esto saca un aproximado de los meses restantes pero no es perfecto
//        if(moth > 12) {
//            float myFloat =  ((float)(moth-1) / (float)12);
//            year = ((int)myFloat)+1;
//            moth = getFloatPart(myFloat)+1;
//
//            Log.d("PhotoPicker", "-->>>>>>>>>>>>>>>>>>>>>>>>>>>> year: " + year + " mes: "+ moth);
//        }
//        boolean result = true;
//        try{
//            LocalDate.of(year, moth, day);
//        }
//        catch(DateTimeException e) {
//            result = false;
//        }
//        if(result){
//            return LocalDate.of(year, moth, day);
//        }
//        else {
//            return LocalDate.of(1, 1, 1);
//        }
//    }

//    private int getFloatPart(float numero) {
//
//        Log.d("", String.format("El número originalmente es: %f\n", numero));
//
//        int parteEntera = (int)numero; // Le quitamos la parte decimal pasando a int
//
//        float parteDecimal = (numero - (float)parteEntera); // restamos la parte entera
//
//        String text =  Float.toString(parteDecimal); //Convertimos los decimales a string
//
//        text = text.replace('.', '0');
//        text = ""+(text.length() > 2? text.charAt(2): 0);
//        Log.d("", String.format("Parte entera: %d. Parte decimal: %s\n", parteEntera, text));
//
//        return Integer.parseInt(text);
//
//    }