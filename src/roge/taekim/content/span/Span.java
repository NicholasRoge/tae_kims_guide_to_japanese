/**
 * 
 */
package roge.taekim.content.span;

import android.util.Log;

/**
 * @author Nicholas Rogé
 *
 * 
 */
public abstract class Span{
    private String _tag;
    
    /*Begin Setter Methods*/
    public void setTag(String tag){
        this._tag=tag;
    }
    
    public String getTag(){
        return this._tag;
    }
    /*End Setter Methods*/
    
    /**
     * 
     * 
     * @param string The character sequence which should have strings added to it.
     * @param span_start The index location where the span starts
     * @param span_end The index location where the span ends
     * @param attributes The set of attributes to be processed by the Span
     * 
     * @return A character sequence that has been processed, and had spans added.  Most of the time, the Span should return the parameter "string"
     */
    public abstract CharSequence parseSpan(CharSequence string,int span_start,int span_end,SpanAttributes attributes);
}
