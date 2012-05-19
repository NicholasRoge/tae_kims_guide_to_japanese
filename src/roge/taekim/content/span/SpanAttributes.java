/**
 * 
 */
package roge.taekim.content.span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * @author Nicholas Rogé
 *
 */
public class SpanAttributes{
    private Map<String,String> _hash_map;
    
    public String getAttribute(String name){
        String attribute_value=this.getHashMap().get(name);
        
        if(attribute_value==null){
            return null;
        }
        
        return attribute_value;
    }
    
    /**
     * Gets the specified attribute, and converts its value to a boolean
     * 
     * @param name Attribute to be retrieved
     * 
     * @return Returns <code>true</code> if the attribute equals "true" (case insensitive), and <code>false</code> if the attribute equals "false" (case insensitive) or the attribute doesn't exist.  Will also return false if the attribute is a non-boolean value.  In this case, however, the method will add a string to the log explaining this. 
     */
    public boolean getAttributeAsBoolean(String name){
        String attribute=null;
        
        if(!this.getHashMap().containsKey(name)){
            return false;
        }
        
        attribute=this.getHashMap().get(name);
        if(attribute.equalsIgnoreCase("true")){
            return true;
        }else if(attribute.equalsIgnoreCase("false")){
            return false;
        }else{
            Log.e("JP","The attribute for \""+name+"\"=\""+attribute+"\" is not a valid boolean value!");
            
            return false;
        }
    }
    
    /**
     * Gets the specified attribute, and converts its value to an Integer
     * 
     * @param name Attribute to be retrieved
     * 
     * @return Returns the integer version of value at the given attribute, or <code>null</code> if the attribute given is an empty string, or is not a number.
     */
    public Integer getAttributeAsInteger(String name){
        String attribute_value=null;
        
        
        attribute_value=this.getAttribute(name);
        if(attribute_value==null){
            return null;
        }else if(attribute_value.equals("")){
            return null;
        }
        
        try{
            return (int)Long.parseLong(attribute_value);  //You have to cast to get rid of the "number too large" exception that is raised when you try to parse a string using Integer when the most significant bit is set
        }catch(NumberFormatException e){
            Log.e("JP","The attribute for \""+name+"\"=\""+attribute_value+"\" is not a valid boolean value!");
            
            return null;
        }
    }
    
    public Integer getAttributeAsColor(String name){
        String attribute_value=null;
        
        
        attribute_value=this.getAttribute(name);
        if(attribute_value==null){
            return null;
        }else if(attribute_value.equals("")){
            return null;
        }
        
        if(attribute_value.charAt(0)=='#'){  //Allows for input of color.
            attribute_value=attribute_value.substring(1,attribute_value.length());
        }
        
        try{
            return (int)Long.parseLong(attribute_value,16);  //You have to cast to get rid of the "number too large" exception that is raised when you try to parse a string using Integer when the most significant bit is set
        }catch(NumberFormatException e){
            Log.e("JP","The attribute for \""+name+"\"=\""+attribute_value+"\" is not a valid color value!");
            
            return null;
        }
    }
    
    private Map<String,String> getHashMap(){
        if(this._hash_map==null){
            this._hash_map=new HashMap<String,String>();
        }
        
        return this._hash_map;
    }
    
    public boolean hasAttribute(String name){
        if(this.getHashMap().containsKey(name)){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 
     * 
     * @return Returned value indicates whether the attribute being set had previously taken a value.
     */
    public boolean setAttribute(String key,String value){
        if(this.hasAttribute(key)){
            this.getHashMap().put(key,value);
            
            return true;
        }else{
            this.getHashMap().put(key,value);
            
            return false;
        }
    }
}
