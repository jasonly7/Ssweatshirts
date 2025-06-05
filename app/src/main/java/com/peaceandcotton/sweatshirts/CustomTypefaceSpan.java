package com.peaceandcotton.sweatshirts;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

// This custom span allows applying a Typeface object directly,
// making it compatible with older Android versions where TypefaceSpan(Typeface) is not available.
public class CustomTypefaceSpan extends MetricAffectingSpan {

    private final Typeface typeface;

    public CustomTypefaceSpan(Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(typeface);
        // p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT); // Optional: for smoother text
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(typeface);
        // tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT); // Optional: for smoother text
    }
}