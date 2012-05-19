/**
 * 
 */
package roge.taekim.content.span;

import roge.androidextended.ETextView;
import roge.androidextended.Tools;
import roge.taekim.Main;
import roge.taekim.R;
import android.app.Dialog;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Nicholas Rogé
 *
 */
public class StyleSpan extends Span{
    private class Style extends CharacterStyle{
        private SpanAttributes _attributes;
        
        public Style(SpanAttributes attributes){
            this._attributes=attributes;
        }
        
        /**
         * @see android.text.style.CharacterStyle#updateDrawState(android.text.TextPaint)
         */
        @Override public void updateDrawState(TextPaint paint){
            if(paint.getTypeface()==null){
                paint.setTypeface(Typeface.create((Typeface)null,Typeface.NORMAL));
            }
            
            if(this._attributes.hasAttribute("bold")){
                if(this._attributes.getAttributeAsBoolean("bold")){     
                    paint.setTypeface(Typeface.create(paint.getTypeface(),paint.getTypeface().getStyle()|Typeface.BOLD));
                }else{
                    paint.setTypeface(Typeface.create(paint.getTypeface(),paint.getTypeface().getStyle()^Typeface.BOLD));
                }
            }
            
            if(this._attributes.hasAttribute("italic")){
                if(this._attributes.getAttributeAsBoolean("italic")){
                    paint.setTypeface(Typeface.create(paint.getTypeface(),paint.getTypeface().getStyle()|Typeface.ITALIC));
                }else{
                    paint.setTypeface(Typeface.create(paint.getTypeface(),paint.getTypeface().getStyle()^Typeface.ITALIC));  //If the attribute is false, we don't want italics.  Just XOR it out.   
                }
            }
            
            if(this._attributes.hasAttribute("font_color")){
                paint.setColor(this._attributes.getAttributeAsColor("font_color"));
            }
            
            if(this._attributes.hasAttribute("font_size")){
                paint.setTextSize(this._attributes.getAttributeAsInteger("font_size"));
            }
        }        
    }
    
    /**
     * @see roge.taekim.content.span.Span#parseSpan(java.lang.CharSequence, int, int, roge.taekim.content.span.SpanAttributes)
     */
    @Override public CharSequence parseSpan(CharSequence string, int span_start, int span_end,SpanAttributes attributes){        
        ((Spannable)string).setSpan(new Style(attributes),span_start,span_end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        return string;
    }

}
