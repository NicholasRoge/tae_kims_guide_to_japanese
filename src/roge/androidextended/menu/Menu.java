package roge.androidextended.menu;

import java.util.ArrayList;

import roge.androidextended.Border;
import roge.androidextended.EColor;
import roge.androidextended.menu.MenuXmlParser.MenuData;
import roge.androidextended.menu.MenuXmlParser.MenuItemData;

import android.R;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.StateSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Item provides an activity to allow users to add a menu tree to their applications.
 * 
 * @author Nicholas Rogé
 */
public abstract class Menu extends Activity{
    /**Result code for finishing the activity when user presses a <code>MenuItem</code>.*/
    public static final int MENU_RESULT=200;
        
    private int          _current_path[]=null;
    private LinearLayout _menu_layout=null;
    private MenuData     _menu_data=null;
    private MenuInflater _menu_inflater=null;
    
    
    /*Begin Initializer Methods*/
    /**
     * Initializes the Menu Layout.
     */
    private void initializeMenuLayout(){
        this.getMenuLayout().setOrientation(LinearLayout.VERTICAL);
        this.stylizeMenu();
    }
    /*End Initializer Methods*/
    
    /*Begin Overridden Methods*/
    /**
     * Called when the activity is first created.
     * 
     *  @param bundle Contains anything passed to this activity by its parent.
     */
    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        /*Bundle extras=null;
        
        
        extras=this.getIntent().getExtras();
        if(extras!=null){
            if(extras.containsKey("menu_resource_id")){
                this._createFromXml(extras.getInt("menu_resource_id"),extras.getIntArray("menu_data_path"));
            }else if(extras.containsKey("menu_data")){
                this._createFromData((MenuData)extras.get("menu_data"),extras.getIntArray("menu_data_path"));
            }
        }*/
    }
    
    @Override public void onBackPressed(){
        int path[]=null;

        
        if(this._current_path==null){
            this.setResult(Activity.RESULT_CANCELED);
            super.onBackPressed();
        }else{           
            path=new int[this._current_path.length-1];
            for(int index=0;index<path.length;index++){
                path[index]=_current_path[index];
            }
            this._current_path=path;
            
            this.navigateTo(path);
        }
    }
    /*End Overridden Methods*/
    
    /*Begin Other Essential Class Methods*/  //TODO:  Please come up with a better name for this...  
    /**
     * Builds the menu using the data that was passed into it, and navigates to teh given path.
     * 
     * @param data Data for the Menu to use when building the Menu.
     * @param path Array of <code>int</code>s that specify the indices to follow when navigating the data. 
     */
    protected void _createFromData(MenuData data,int path[]){
        ScrollView container=null;
        TextView textview=null;
        

        this.initializeMenuLayout();
        
        this._menu_data=data;
        
        textview=new TextView(this.getApplicationContext());
        this.stylizeTitle(textview);
        textview.setText(this.getMenuData().getTitle());
        textview.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        this.getMenuLayout().addView(textview);
        
        container=new ScrollView(this.getApplicationContext());
        container.setFillViewport(true);
        container.addView(this.getMenuLayout(),new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        //container.setBackgroundColor(Color.GRAY);        
        
        this.setContentView(container,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        
        this.navigateTo(path);
    }
    
    /**
     * Builds the menu using the XML resource that was passed to it.
     * 
     * @param menu_resource The value of the resource to load.
     * @param path Array of <code>int</code>s that specify the indices to follow when navigating the data. 
     */
    protected void _createFromXml(int menu_resource,int path[]){
        MenuXmlParser parser=null;
        
        
        parser=new MenuXmlParser(this.getApplicationContext(),menu_resource);
        if(!parser.parse()){
            Log.e("JP","There was an error while creating the menu.  Check log output for details.");
            
            return;
        }
        
        this._createFromData(parser.getMenuData(),path);
    }
    
    /**
     * This method is called when a MenuItem is touched, and it doesn't have children.  The method is intended to be overridden by the developer to determine the action taken by MenuItems.
     * 
     * @param item The MenuItem that was touched.
     * @param event The MotionEvent that occured.
     * 
     * @return Returns a string which will be returned to the calling activity, and can be retrieved by using the key "result" with the returned intent.
     */
    public String menuItemOnTouch(MenuItem item,MotionEvent event){
        return null;
    }
    
    /**
     * Changes the location in the menu tree that this Menu object is currently at.
     * 
     * @param path Array of <code>int</code>s that specify the indices to follow when navigating the data.
     */
    public void navigateTo(int path[]){
        this._rebuild(path);        
        
        if(path==null){
            this._current_path=null;
        }else{
            if(path.length==0){
                this._current_path=null;
            }else{
                this._current_path=path;
            }
        }
    }
    
    /**
     * Builds the Menu for the specified path.
     * 
     * @param path The path to build the menu to.  If this parameter is null, then the menu will be built using the root elements.
     */
    protected void _rebuild(int path[]){
        MenuItemData child=null;
        ArrayList<MenuItemData> children=null;
        LinearLayout linear_layout=null;
        MenuItem menu_item=null;
        
        
        //Clean up the layout.  (I.E.:  Remove all views from this layout which aren't the title.)
        while(this.getMenuLayout().getChildCount()>1){
            this.getMenuLayout().removeViewAt(1);
        }
        
        //Find the appropriate set of children.
        children=this.getMenuData().getChildren();
        if(path!=null){
            for(int index:path){
                if(!children.get(index).hasChildren()){
                    throw new RuntimeException("That MenuItem has no children!");
                }
                
                children=children.get(index).getChildren();
            }
        }
        
        //Finally, actually build the menu.
        for(int index=0;index<children.size();index++){
            child=children.get(index);
            
            menu_item=new MenuItem(this.getApplicationContext(),this,child,child.getParameters());
            menu_item.setIndex(index);
            this.stylizeMenuItem(menu_item);
            
            this.getMenuLayout().addView(menu_item);
        }
    }
    
    /**
     * Adds styling to the Menu View.  Override to add your own style.
     */
    protected void stylizeMenu(){        
        this.getMenuLayout().setBackgroundColor(Color.GRAY);
    }
    
    /**
     * Adds styling to the MenuItem View.  Override to add your own style.
     * 
     * @param view The item which will be stylized.
     */
    protected void stylizeMenuItem(MenuItem view){
        LinearLayout.LayoutParams layout_params=null;
        
        layout_params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_params.setMargins(0,1,0,0);
        view.setLayoutParams(layout_params);
        
        view.setBackgroundColor(this.getApplicationContext().getResources().getColor(android.R.color.background_light));
        view.getStyle().setBackgroundColor(this.getApplicationContext().getResources().getColor(android.R.color.background_light));
        view.getStyle().setBackgroundColorOntouch(EColor.darkenColor(view.getStyle().getBackgroundColor(),.1f));
        
        view.setTextSize(22);
        view.setPadding(15,10,15,10);
        
        if(view.hasChildren()){
            view.setPadding(15,10,12,10);
            
            view.getStyle().getBorder().setWidth(Border.Side.RIGHT,3);
            view.getStyle().getBorder().setColor(Border.Side.RIGHT,0xFFFF0000);
        }
    }
    
    /**
     * Adds styling to the Menu's Title View.  Override to add your own style.
     * 
     * @param view Title View which will be stylized.
     */
    protected void stylizeTitle(TextView view){
        LinearLayout.LayoutParams layout_params=null;
        
        
        layout_params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_params.setMargins(0,0,0,2);
        view.setLayoutParams(layout_params);
        
        view.setTextSize(40);
        view.setTypeface(null,Typeface.BOLD);
        view.setBackgroundColor(this.getApplicationContext().getResources().getColor(android.R.color.background_light));
        view.setPadding(20,5,20,5);
    }
    /*End Other Essential Class Methods*/
    
    /*Begin Getter Methods*/
    public int[] getCurrentPath(){
        return this._current_path;
    }
    
    /**
     * Gets the data associated with this object.
     * 
     * @return Returns the data associated with this object.
     */
    public MenuData getMenuData(){
        return this._menu_data;
    }
    
    /**
     * Gets this object's MenuInflater object.  If the MenuInflater hasn't been instantiated yet, this method will initialize it with the default data.
     * 
     * @return Returns this object's MenuInflater object.
     */
    protected MenuInflater _getMenuInflater(){
        if(this._menu_inflater==null){
            this._menu_inflater=new MenuInflater(this.getApplicationContext(),this.getMenuData());
        }
        
        return this._menu_inflater;
    }
    
    /**
     * Gets the View for this object which holds all the MenuItems.
     * 
     * @return Returns the View for this object which holds all the MenuItems.
     */
    public LinearLayout getMenuLayout(){
        if(this._menu_layout==null){
            this._menu_layout=new LinearLayout(this.getApplicationContext());
        }
        
        return this._menu_layout;
    }
    
    protected View _getSubmenuIndicator(){
        ImageView indicator=null;
        
        
        indicator=new ImageView(this.getApplicationContext());
        indicator.setImageResource(R.drawable.ic_menu_more);
        
        return indicator;
    }
    /*End Getter Methods*/
}