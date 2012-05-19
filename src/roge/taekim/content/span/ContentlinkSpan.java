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
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Nicholas Rogé
 *
 */
public class ContentlinkSpan extends Span{
    private class Contentlink extends ClickableSpan{
        private String _location;
        
        /**
         * Constructs the object.
         * 
         * @param location Should be in the form described by Content#setContent(String)
         * 
         * @see roge.jpanese.content.Content#setContent(String)
         */
        public Contentlink(String location){
            this._location=location;
        }
        
        @Override public void onClick(View view){
            try{
                Main.content.setContent(this._location);
            }catch(Exception e){
                Tools.logException(e);
            }
        }
        
        @Override public void updateDrawState(TextPaint paint){
            paint.setColor(0xFFB100C4);  //TODO:  Find a better way to set these values.
            paint.setUnderlineText(false);
            paint.setTypeface(Typeface.create((String)null,Typeface.BOLD));  //Have to add that cast to avoid ambiguity.
        }
    }
    
    /**
     * @see roge.taekim.content.span.Span#parseSpan(java.lang.CharSequence, int, int, roge.taekim.content.span.SpanAttributes)
     */
    @Override
    public CharSequence parseSpan(CharSequence string, int span_start, int span_end,SpanAttributes attributes){
        if(!attributes.hasAttribute("link")){
            Log.d("JP","The link attribute for the \""+this.getTag()+"\" span was not set.");
            
            return string;
        }else if(attributes.getAttribute("link").equals("")){
            Log.d("JP","The link attribute for the \""+this.getTag()+"\" span was empty.");
            
            return string;
        }
        
        ((Spannable)string).setSpan(new Contentlink(attributes.getAttribute("link")),span_start,span_end,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        return string;
    }

}
