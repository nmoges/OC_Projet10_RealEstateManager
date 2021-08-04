package com.openclassrooms.realestatemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return : date
     */
   /* public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }*/
    @SuppressLint("SimpleDateFormat")
    public static String getTodayDate(Date date) {
        DateFormat dateFormat;
        if (Locale.getDefault().getLanguage() == "en")
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        else
            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
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
