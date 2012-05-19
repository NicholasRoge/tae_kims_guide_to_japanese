/**
 * 
 */
package roge.taekim.content.span;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roge.androidextended.Tools;
import android.text.SpannableString;
import android.util.Log;
import android.view.InflateException;
import android.widget.TextView;

/**
 * @author Nicholas Rogé
 *
 * Intent of this class is to provide a more seamless interface for adding spans to text.
 */
public final class SpanParser{
    private List<Span> _spans;
    private Map<String,Integer> _hashed_location;
    
    public void registerSpan(String tag,Class<?> handler){
        Object object=null;
        
        try{
            object=handler.newInstance();
        
            if(!(object instanceof Span)){
                throw new Exception("Only Objects who inherit from the Span class may be passed into this method.");
            }
        }catch(Exception e){
            Tools.logException(e);
            
            return;
        }
        
        
        //Make sure that _spans is initialized
        if(this._spans==null){
            this._spans=new ArrayList<Span>();
        }
        
        //If the _spans wasn't initialized, it's guaranteed that _hashed_location won't be.  But for the hell of it, let's make sure and check anyways.
        if(this._hashed_location==null){
            this._hashed_location=new HashMap<String,Integer>();
        }
        
        this._hashed_location.put(tag,this._spans.size());
        this._spans.add((Span)object);
        
        ((Span)object).setTag(tag);
    }
    
    private String[] explodeSpanContents(String content_string){
        List<String> explosion;
        boolean      in_string=false;
        String       tmp_string="";
        
        
        if(content_string==null){
            return null;
        }else if(content_string.equals("")){
            return null;
        }
        
        explosion=new ArrayList<String>();
        for(int index=0;index<content_string.length();index++){
            if(content_string.charAt(index)==' '&&!in_string){
                explosion.add(new String(tmp_string));
                tmp_string="";
                
                continue;
            }
            
            if(content_string.charAt(index)=='\\'){
                index++;
                tmp_string+=content_string.charAt(index);
            }else if(content_string.charAt(index)=='"'){
                in_string=!in_string;  //This just inverts teh in_string.  If we are in the string, then this will set in_string to false.  The reverse, of course, is true as well.
            }else{
                tmp_string+=content_string.charAt(index);
            }
        }
        explosion.add(tmp_string); //There will always be one string that isn't added.  (The one at the very end, of course.)
        
        return explosion.toArray(new String[explosion.size()]);
    }
    
    /**
     * Finds the next full span in the given string, then explodes the text into the format specified below.
     * 
     * @param string The string to find a span in.
     * 
     * @return If a span can be found, this method will return <code>null</code>.  Otherwise, it will return a string array with five elements.<br />
     *     <br />
     *     <strong>Element 1</strong> Text before span open tag<br />
     *     <strong>Element 2</strong> Span open tag and all contents<br />
     *     <strong>Element 3</strong> Text inside of span, or <code>null</code> if...  TODO:  fill in<br />
     *     <strong>Element 4</strong> Span close tag and all contents<br />
     *     <strong>Element 5</strong> Text after span close tag
     */
    private String[] findNextSpan(final String string){
        String span_broken_string[];
        int    span_open_start=-1;
        int    span_open_length=-1; 
        int    span_close_start=-1;
        int    span_close_length=-1;
        Integer last_find_location=0;
        
        
        if(string==null){
            return null;
        }else if(string.equals("")){
            return null;
        }
        
        while(true){
            last_find_location=string.indexOf("[[",last_find_location);
            if(last_find_location==-1||string.length()-last_find_location==2){ //The only time the latter should happen is if the last two characters are "[["
                return null;
            }else if(string.charAt(last_find_location+2)=='/'){  //We've encountered a closing tag before any open tags.  This is an error if it happens.
                throw new InflateException("Unopened span found when parsing string:  \n"+string);
            }
            span_open_start=last_find_location+2;
            
            last_find_location=string.indexOf("]]",last_find_location);
            if(last_find_location==-1){
                throw new InflateException("Unclosed opening span found when parsing string:  \n"+string);
            }else if(last_find_location-2==span_close_start){  //We just found a random [[]]
                continue;
            }
            span_open_length=last_find_location-span_open_start;
            
                        
            last_find_location=string.indexOf("[[",last_find_location);
            if(last_find_location==-1||string.length()-last_find_location==2){ //Again we have to check to see if the "[[" is the last two characters in teh string
                throw new InflateException("Unclosed span found when parsing string:  \n"+string);
            }else if(string.charAt(last_find_location+2)!='/'){  //This just means that we've found another opening tag.
                continue;  
            }            
            span_close_start=last_find_location+3;//In this case we advance the index by 3 to get past teh "/" as well.
            
            last_find_location=string.indexOf("]]",last_find_location);
            if(last_find_location==-1){
                throw new InflateException("Unclosed closing span found when parsing string:  \n"+string);
            }else if(last_find_location-2==span_close_start){  //We just found a random [[]]
                continue;
            }
            span_close_length=last_find_location-span_close_start;
            
            break;  //If we got to this point, we succesfully found a span.
        }
        
        span_broken_string=new String[5];
        span_broken_string[0]=string.substring(0,span_open_start-2);
        span_broken_string[1]=string.substring(span_open_start,span_open_start+span_open_length).trim();
        span_broken_string[2]=string.substring(span_open_start+span_open_length+2,span_close_start-3);
        span_broken_string[3]=string.substring(span_close_start,span_close_start+span_close_length).trim();  //TODO:  Find out why I don't have to do span_close_length here.  
        span_broken_string[4]=string.substring(span_close_start+span_close_length+2,string.length());
        
        return span_broken_string;
    }
    
    /**
     * Parses through the specified string, and adds any spans that are found to the CharSequence.
     * 
     * @param string This should be a string which contains spans.
     * 
     * @return Returns a <code>CharSequence</code> which contains all the parsed spans.
     */
    public CharSequence parseSpans(String string){
        String         attribute[]=null;
        SpanAttributes attributes=null;
        List<SpanAttributes> attributes_list=null;
        CharSequence   c_string=null;
        String         span_contents[]=null;
        String         span_broken_string[]=null;
        List<Integer>  span_end_index_list=null;
        List<Integer>  span_start_index_list=null;
        String         span_tag="";
        List<String>   tags_list=null;
        while(true){
            span_broken_string=this.findNextSpan(string);
            
            if(span_broken_string==null){
                break;
            }
            
            
            //Check if there is a span open/close mismatch
            if(!span_broken_string[1].split(" ")[0].equals(span_broken_string[3].split(" ")[0])){
                throw new InflateException("Span tag mismatch found!  Opening tag:  "+span_broken_string[1].split(" ")[0]+"/Closing tag:  "+span_broken_string[3].split(" ")[0]);
            }
            
            //Break the span up into it's components
            span_contents=this.explodeSpanContents(span_broken_string[1]);//NOTE:  even if the tag is simply [[TAG]] (e.g.:  No spaces), span_contents[0] will still be TAG
            span_tag=span_contents[0];
            
            //Parse the attributes
            attributes=new SpanAttributes();
            
            for(int index=1;index<span_contents.length;index++){  //Recall that the first element of the span_contents array will always be the span's tag
                attribute=span_contents[index].split("=");
                
                if(attribute.length!=2){
                    Log.e("JP","Extraneous attribute found while parsing span with name=\""+span_contents[0]+"\".  Attribute:  "+span_contents[index]);
                    
                    continue;
                }
                
                attributes.setAttribute(attribute[0],attribute[1]);
            }
            
            //Remove teh span
            string=span_broken_string[0]+span_broken_string[2]+span_broken_string[4];
            
            //And update teh lists
            if(tags_list==null){
               tags_list=new ArrayList<String>(); 
            }
            tags_list.add(span_tag);

            if(span_start_index_list==null){
                span_start_index_list=new ArrayList<Integer>();
            }
            this.updateSpanIndices(span_start_index_list,span_broken_string[0].length()+span_broken_string[1].length()+4,0-(span_broken_string[1].length()+4));
            span_start_index_list.add(new Integer(span_broken_string[0].length()));
             
            if(span_end_index_list==null){
                span_end_index_list=new ArrayList<Integer>();
            }
            this.updateSpanIndices(span_end_index_list,span_broken_string[0].length()+span_broken_string[1].length()+4+span_broken_string[2].length(),0-(span_broken_string[1].length()+4));
            span_end_index_list.add(new Integer(span_broken_string[0].length()+span_broken_string[2].length()));
            
            if(attributes_list==null){
                attributes_list=new ArrayList<SpanAttributes>();
            }
            attributes_list.add(attributes);
        }
        
        if(tags_list!=null){  //If tags_list is null, that means that the rest of the [x]_lists will be null as well.  It also means that we have no business trying to add spans to the text for the given string.
            c_string=new SpannableString(string);
            
            for(int index=0;index<tags_list.size();index++){
                if(this._getHashedLocation().containsKey(tags_list.get(index))){
                    c_string=this._spans.get(this._getHashedLocation().get(tags_list.get(index))).parseSpan(c_string,span_start_index_list.get(index),span_end_index_list.get(index),attributes_list.get(index));
                }else{
                    Log.d("JP","No Span handler found for span with tag=\""+tags_list.get(index)+"\".");
                }
            }
        }
        //End Code Segment:  ADD_SPANS
        
        //Finally we get to leave this method!
        if(c_string==null){
            return string;
        }else{
            return c_string;
        }
    }
    
    private Map<String,Integer> _getHashedLocation(){
        if(this._hashed_location==null){
            this._hashed_location=new HashMap<String,Integer>();
        }
        
        return this._hashed_location;
    }
    
    private void updateSpanIndices(List<Integer> index_list,int update_indices_after,int adjustment){       
        if(index_list==null){
            return;
        }
        
        for(int index=0;index<index_list.size();index++){
            if(update_indices_after<=index_list.get(index)){
                index_list.set(index,index_list.get(index)+adjustment);
            }
        }
    }
}
