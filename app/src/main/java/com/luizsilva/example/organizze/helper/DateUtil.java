package com.luizsilva.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateUtil {

    public static String dataAtual(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(date);
        return dataString;
    }

    public static String mesAnoDataEscolhida(String data){
        String dataReturn[] =data.split("/");
        String dia = dataReturn[0];
        String mes = dataReturn[1];
        String ano = dataReturn[2];

        String mesAno = mes+ano;
        return  mesAno;
    }

}
