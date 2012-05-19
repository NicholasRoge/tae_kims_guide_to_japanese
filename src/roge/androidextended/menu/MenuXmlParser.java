/**
 * 
 */
package roge.androidextended.menu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import roge.androidextended.menu.MenuItem.MenuItemParameters;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author Nicholas Rogé
 *
 */
public class MenuXmlParser{
    public static abstract class ItemData{
        private ArrayList<MenuItemData> _children;
        private String                  _menu_title;
        
        
        /*Begin Overridden Methods*/
        @Override public String toString(){
            final String DATA_SEPERATOR=":";
            final String FIELD_SEPERATOR="::";
            final String NULL_CASE=Character.toString((char)0x0);
            
            Class<?> menu_data=null;
            Object data=null;
            String string="";
            
            
            menu_data=this.getClass();
            for(Field field:menu_data.getDeclaredFields()){
                try {
                    data=field.get(this);
                    
                    string+=field.getName()+DATA_SEPERATOR+(data==null?NULL_CASE:data)+FIELD_SEPERATOR;
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            string=string.substring(0,string.length()-FIELD_SEPERATOR.length());
            
            return string;
        }
        /*End Overridden Methods*/
        
        /*Begin Getter Methods*/
        /**
         * Gets this object's children.
         * 
         * @return Returns this object's children.
         */
        public ArrayList<MenuItemData> getChildren(){
            if(this._children==null){
                this._children=new ArrayList<MenuItemData>();
            }
            
            return this._children;
        }
        
        /**
         * Gets this object's title.
         * 
         * @return Returns this object's title.
         */
        public String getTitle(){
            if(this._menu_title==null){
                this._menu_title="Menu";
            }
            
            return _menu_title;
        }
        /*End Getter Methods*/
        
        /*Begin Setter Methods*/
        /**
         * Sets this object's title.
         * 
         * @param title String to set this object's title to.
         */
        public void setTitle(String title){
            if(title==null){
                this._menu_title="Menu";
            }else{
                this._menu_title=title;
            }
        }
        /*End Setter Methods*/
        
        /*Begin Boolean Check Methods*/
        /**
         * Check to see whether his object has children.
         * 
         * @return Returns <code>true</code> if this object has children, and <code>false</code> otherwise.
         */
        public boolean hasChildren(){
            return (this.getChildren().size()==0?false:true);
        }
        /*End Boolean Check Methods*/
        
        /**
         * Adds a child to this object.
         * 
         * @param data Child to add to this object.
         */
        public void addChild(MenuItemData data){
            this.getChildren().add(data);
        }
    }
    public static class MenuData extends ItemData{        
        
    }
    
    public static class MenuItemData extends ItemData{        
        private final MenuItemParameters _params=new MenuItemParameters();
        
        private int _index_path[]=null;
        
        
        /**
         * Gets all the MenuItemParameters object which contains all information about this object that was parsed in from the XML file.
         * 
         * @return Returns all the MenuItemParameters object which contains all information about this object that was parsed in from the XML file.
         */
        public MenuItemParameters getParameters(){
            return this._params;
        }
        
        /**
         * Gets the path the Menu should take when navigating through the MenuData object.
         * 
         * @return Returns the path the Menu should take when navigating through the MenuData object.
         */
        public int[] getIndexPath(){
            return this._index_path;
        }
        
        /**
         * This sets the path the Menu should take when navigating through the MenuData object.
         * 
         * @param path The path the Menu should take when navigating through the MenuData object.  If this is a top-level element, this should be <code>null</code>.
         */
        public void setIndexPath(int path[]){
            this._index_path=path;
        }
    }
    
    enum Tags{
        CHILDREN,
        ITEM,
        MENU,
        UNDEFINED
    }
    
    private Context _context;
    private MenuData _menu_data;
    private boolean _parse_called;
    private int _xml_resource;
    
    
    public MenuXmlParser(Context context,int xml_resource){
        this._context=context;
        this._parse_called=false;
        this._xml_resource=xml_resource;
    }
    
    public boolean parse(){
        XmlResourceParser parser=null;
        
        
        this._parse_called=true;
        try{
            parser=this._context.getResources().getXml(this._xml_resource);
            
            return this._parseMenu(parser);
        }catch(NotFoundException e){
            Log.e("JP","The specified resource could not be found.");
            
            return false;
        }
    }
    
    protected boolean _parseMenu(XmlResourceParser parser){
        boolean children_parsed=false;
        int event=-1;
        int index=0;
        
        
        this._menu_data=new MenuData();
        try{
            do{
                event=parser.next();
                        
                switch(event){
                    case XmlPullParser.START_TAG:
                        switch(this._tagToEnum(parser.getName())){
                            case MENU:
                                this._menu_data.setTitle(parser.getAttributeValue(null,"title"));
                                break;
                            case ITEM:
                                this._menu_data.addChild(this._parseMenuItem(parser,new int[]{index}));
                                index++;
                                
                                break;
                        }
                        break;
                }
            }while(event!=XmlPullParser.END_DOCUMENT);
        }catch(Exception e){
            Log.e("JP","ERROR");
            
            return false;
        }
        
        return true;
    }
    
    protected MenuItemData _parseMenuItem(XmlResourceParser parser,int item_path[]){
        boolean children_parsed=false;
        int index=0;
        MenuItemData menu_item=null;
        boolean parsed=false;
        int path[]=null;
        
        
        menu_item=new MenuItemData();
        try{
            menu_item.getParameters().setAction(parser.getAttributeValue(null,"action"));
            menu_item.getParameters().setId(parser.getAttributeValue(null,"id"));
            menu_item.getParameters().setTitle(parser.getAttributeValue(null,"title"));

            menu_item.setIndexPath(item_path);
            
            while(!parsed){
                switch(parser.next()){
                    case XmlPullParser.START_TAG:
                        switch(this._tagToEnum(parser.getName())){
                            case ITEM:
                                path=new int[item_path.length+1];            
                                for(int sub_index=0;sub_index<item_path.length;sub_index++){
                                    path[sub_index]=item_path[sub_index];
                                }
                                path[path.length-1]=index;
                                
                                menu_item.addChild(this._parseMenuItem(parser,path));
                                
                                index++;
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        switch(this._tagToEnum(parser.getName())){
                            case ITEM:
                                parsed=true;
                                break;
                        }
                        break;
                }
            }
        }catch(Exception e){
            Log.e("JP","ERROR");
            
            return null;
        }
        
        return menu_item;
    }
    
    /**
     * Converts the "tag" parameter to its enumerated <code>Tag</code> equivalent.
     * 
     * @param tag The string to convert to 
     * @return Returns the correct member of the <code>Tags</code> enumeration if the specified tag is found, and <code>Tags.UNDEFINED</code> otherwise. 
     */
    private Tags _tagToEnum(String tag){
        Tags values[]=Tags.values();
        
        
        for(int index=0;index<values.length;index++){
            if(values[index].name().equalsIgnoreCase(tag)){
                return values[index];
            }
        }
        
        return Tags.UNDEFINED;
    }
    
    
    /**
     * Gets the parsed menu's data.   
     * 
     * @return Returns the parsed menu's data.
     * 
     * @throws RuntimeException Throws a <code>RuntimeException</code> if the {@link #parse()} method was not called beforehand.
     */
    public MenuData getMenuData(){
        if(!this._parse_called){
            throw new RuntimeException("You must call this object's parse() method before using the getMenuData() method.");
        }
        
        return this._menu_data;
    }
}
