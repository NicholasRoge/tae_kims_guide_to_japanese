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
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author Nicholas Rogé
 *
 */
public class VocabSpan extends Span{
    private class Vocab extends ClickableSpan{
        private String _meaning;
        private String _reading;
        private String _word;
        
        public Vocab(String word,String meaning,String reading){
            if(meaning==null){
                this._meaning="";
            }else{
                this._meaning=meaning;
            }
            
            if(reading==null){
                this._reading="";
            }else{
                this._reading=reading;
            }
            
            this._word=word;
        }
        
        @Override public void onClick(View view) {
            if(_dialog==null){ //This has to be done here, as we need access to a context (view's context, to be precise).
                _inflated_dialog_span=(LinearLayout)Main.activity.getLayoutInflater().inflate(R.layout.kanji_span_dialog,null);
                
                _dialog=new Dialog(view.getContext());
                _dialog.setTitle("Kanji Details (In Context)");
                _dialog.addContentView(_inflated_dialog_span,new ViewGroup.LayoutParams(300,ViewGroup.LayoutParams.WRAP_CONTENT));
            }  //Don't want to keep recreating that over and over again, right?
            
            ((ETextView)_inflated_dialog_span.getChildAt(0)).setText(this._word);
            ((ETextView)_inflated_dialog_span.getChildAt(1)).setText(this._reading);
            ((ETextView)_inflated_dialog_span.getChildAt(2)).setText(this._meaning);
            
            _dialog.show();
        }
        
        @Override public void updateDrawState(TextPaint paint){
            paint.setColor(0xFFB100C4);  //TODO:  Find a better way to set these values.
            paint.setUnderlineText(false);
            paint.setTypeface(Typeface.create((String)null,Typeface.BOLD));  //Have to add that cast to avoid ambiguity.
        }
    }
    
    private static Dialog       _dialog=null;
    private static LinearLayout _inflated_dialog_span=null;
    
    /**
     * @see roge.taekim.content.span.Span#parseSpan(CharSequence, int, int, SpanAttributes)
     */
    @Override public CharSequence parseSpan(CharSequence string,int span_start,int span_end,SpanAttributes attributes){
        if(!attributes.hasAttribute("word")){
            Log.e("JP","The "+this.getTag()+" tag MUST have the \"word\" attribute set.");
            
            return string;
        }

        ((Spannable)string).setSpan(
            new Vocab(
                attributes.getAttribute("word"),
                attributes.getAttribute("meaning"),
                attributes.getAttribute("reading")
            ),
            span_start,
            span_end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        
        return string;
    }
}
