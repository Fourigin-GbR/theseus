package com.fourigin.argo.template.engine.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatterUtility implements ThymeleafTemplateUtility {

    private String compilerBase;

    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);

    private final Logger logger = LoggerFactory.getLogger(FormatterUtility.class);

    public String formatPrice(String unformattedPrice){
        if (logger.isDebugEnabled()) logger.debug("Using compiler base '{}'", compilerBase);

        try {
            double value = Double.parseDouble(unformattedPrice);
            return formatter.format(value);
        }
        catch(NumberFormatException ex){
            if (logger.isErrorEnabled()) logger.error("Unable to parse price value '" + unformattedPrice + "'!", ex);
            return unformattedPrice;
        }
    }

    @Override
    public void setCompilerBase(String compilerBase) {
        this.compilerBase = compilerBase.toLowerCase(Locale.US);

        this.formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        this.formatter.setMaximumFractionDigits(0);
    }

//    public static void main(String[] args){
//        FormatterUtility util = new FormatterUtility();
//        String unformattedPrice = "279990";
//        {
//            util.setCompilerBase("DE");
//            String formattedPrice = util.formatPrice(unformattedPrice);
//            System.out.println("unformatted: '" + unformattedPrice + "', formatted: '" + formattedPrice + "'!");
//        }
//    }
}
