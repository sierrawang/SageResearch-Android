/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.mobile_ui.recorder.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatHelper {

    private static final String COUNTRY_US      = "US";
    private static final String COUNTRY_LIBERIA = "LR";
    private static final String COUNTRY_BURMA   = "MM";

    private static final double FEET_PER_METER = 3.28084;

    public static final int NONE = -1;
    public static final String DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_SIMPLE_DATE = "yyyy-MM-dd";
    public static final String TIME_FORMAT_SIMPLE_DATE = "HH:mm:ss.sss";
    public static final ThreadLocal<SimpleDateFormat> DEFAULT_FORMAT;
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_FORMAT_DATE;
    public static final ThreadLocal<SimpleDateFormat> SIMPLE_FORMAT_TIME;
    static {
        DEFAULT_FORMAT = new ThreadLocal<>();
        DEFAULT_FORMAT.set(new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601, Locale.getDefault()));
        SIMPLE_FORMAT_DATE = new ThreadLocal<>();
        SIMPLE_FORMAT_DATE.set(new SimpleDateFormat(DATE_FORMAT_SIMPLE_DATE, Locale.getDefault()));
        SIMPLE_FORMAT_TIME = new ThreadLocal<>();
        SIMPLE_FORMAT_TIME.set(new SimpleDateFormat(TIME_FORMAT_SIMPLE_DATE, Locale.getDefault()));
    }

    /**
     * Returns a DateFormat object based on the dateStyle and timeStyle params
     *
     * @param dateStyle style for the date defined by static constants within {@link DateFormat}
     * @param timeStyle style for the time defined by static constants within {@link DateFormat}
     * @return DateFormat object
     */
    public static DateFormat getFormat(int dateStyle, int timeStyle) {
        // Date & Time format
        if (isStyle(dateStyle) && isStyle(timeStyle)) {
            return DateFormat.getDateTimeInstance(dateStyle, timeStyle);
        }

        // Date format
        else if (isStyle(dateStyle) && !isStyle(timeStyle)) {
            return DateFormat.getDateInstance(dateStyle);
        }

        // Time format
        else if (!isStyle(dateStyle) && isStyle(timeStyle)) {
            return DateFormat.getTimeInstance(timeStyle);
        }

        // Else crash since the styles are invalid
        else {
            throw new IllegalArgumentException("dateStyle and timeStyle cannot both be ");
        }
    }

    public static boolean isStyle(int style) {
        return style >= DateFormat.FULL && style <= DateFormat.SHORT;
    }
}
