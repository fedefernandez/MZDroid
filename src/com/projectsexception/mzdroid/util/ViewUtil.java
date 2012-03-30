package com.projectsexception.mzdroid.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class ViewUtil {
    
    public static SpannableString createSpannable(Context ctx, int title, int text) {
        String titleS = ctx.getString(title);
        SpannableString result = new SpannableString(titleS + ": " + text);
        result.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleS.length() + 2, 0);
        return result;
    }

}
