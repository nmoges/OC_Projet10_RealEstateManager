package com.openclassrooms.realestatemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars : dollar value
     * @return : converted value
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    /**
     * Euro to dollar converter
     * @param euros : price in euros
     * @return converted value in dollars
     */
    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros/0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return : date
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Converts a date to a specific format according to the system language.
     * @param date : Date to convert
     * @return : Converted date (String)
     */
    @SuppressLint("SimpleDateFormat")
    public static String convertDateToFormat(Date date) {
        DateFormat dateFormat;
        if (Locale.getDefault().getLanguage().equals("en"))
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        else
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }


    /**
     * Converts a formatted date into a Date object according to the system language.
     * @param date : Formatted date to convert
     * @return : Converted date (Date)
     */
    @SuppressLint("SimpleDateFormat")
    public static Date convertFormatToDate(String date) throws java.text.ParseException{
        DateFormat dateFormat;
        if (Locale.getDefault().getLanguage().equals("en"))
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        else
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return dateFormat.parse(date);
    }

    /**
     * Converts a selected date to a SQL compatible date-format
     * @param date : Date to convert
     * @return : Converted date
     */
    public static String convertStringToSQLiteFormat(String date) {
        String day;
        String month;
        String year;
        if (Locale.getDefault().getLanguage().equals("en")) {
            month = date.substring(0,2);
            day = date.substring(3,5);
        }
        else {
            day = date.substring(0,2);
            month = date.substring(3,5);
        }
        year = date.substring(6);
        return year + "-" + month + "-" + day;
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context : context
     * @return : network status
     */
    public static Boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                                             context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean status;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            status = connectivityManager.getActiveNetwork() != null;
        else
            status = connectivityManager.getActiveNetworkInfo() != null;
        return status;
    }
}
