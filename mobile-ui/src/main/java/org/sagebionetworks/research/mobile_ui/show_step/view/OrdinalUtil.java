package org.sagebionetworks.research.mobile_ui.show_step.view;


public class OrdinalUtil {
    private OrdinalUtil() {}

    public static String getNumberOrdinal(Number number) {
        if (number == null) {
            return null;
        }

        String format = "{0,ordinal}";
        return com.ibm.icu.text.MessageFormat.format(format, number);
    }
}
