package org.sagebionetworks.research.mobile_ui.show_step.view;

import android.os.Build;

public class OrdinalUtil {
    private OrdinalUtil() {}

    public static String getNumberOrdinal(Number number) {
        if (number == null) {
            return null;
        }

        String format = "{0,ordinal}";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return android.icu.text.MessageFormat.format(format, number);
        } else {
            return com.ibm.icu.text.MessageFormat.format(format, number);
        }
    }
}
