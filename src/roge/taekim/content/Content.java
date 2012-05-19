package roge.taekim.content;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import roge.androidextended.Border;
import roge.androidextended.ELinearLayout;
import roge.androidextended.ETextView;
import roge.androidextended.Padding;
import roge.androidextended.SwipeDetector;
import roge.androidextended.Tools;
import roge.taekim.Main;
import roge.taekim.R;
import roge.taekim.content.span.AudioSpan;
import roge.taekim.content.span.ContentlinkSpan;
import roge.taekim.content.span.Span;
import roge.taekim.content.span.SpanParser;
import roge.taekim.content.span.StyleSpan;
import roge.taekim.content.span.VocabSpan;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.InflateException;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author Nicholas Rogé
 *
 * A type of View which holds Content...  Yeah...  Content...  TODO:  Give this a better description.
 */
public class Content extends ELinearLayout{      
	/**Describes the namespace of the content to be read in from the content XML file.*/
	public enum ContentType{
		/**Specifies the "lesson" namespace*/
		LESSON,
		
		/**Specifies the "quiz" namespace*/
		QUIZ,
		
		/**Specifies the "untyped" namespace*/
		UNTYPED
	};
	
	/*Begin Constants*/
	/**Defines the largest size that the history can grow to.*/
	public static final int MAX_HISTORY_SIZE=10;
	/*End Constants*/
	
	
	/*Begin Member Variables*/
	private AdView          _ad;
	/**Used to allow the ad to know what to do on certain events.*/
    private AdListener      _ad_actions=new AdListener(){
        /*Begin Constants*/
        /**Used to specify the max number of attempts at loading an ad before we just stop trying to retrieve an ad.*/
        private final int MAX_FAILED_ATTEMPTS=3;
        /*End Constants*/
        
        /*Begin Member Variables*/
        private int _failed_attempts=0;
        /*End Member Variables*/
        
        public void onDismissScreen(Ad ad){}

        public void onFailedToReceiveAd(Ad ad,ErrorCode e){
            if(this._failed_attempts>this.MAX_FAILED_ATTEMPTS){  //We don't want the ad to just keep trying to get a new ad over and over again.  If they can't get the ad, they can't get it.
                ad.loadAd(_getNewAdRequest());
                
                this._failed_attempts++;
            }
        }

        public void onLeaveApplication(Ad ad) {
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.ad_thankyou),Toast.LENGTH_LONG).show();  //Thank the user for their kindness.  :3
            
            ad.loadAd(_getNewAdRequest());
        }

        public void onPresentScreen(Ad ad){}

        public void onReceiveAd(Ad ad){
            this._failed_attempts=0;
        }
    };
	private LinearLayout    _content;
	private LinearLayout    _content_body;
	private ProgressBar     _content_dialog_progressbar;
	private ETextView       _content_dialog_text;
	private List<String>    _content_history;
	private ETextView       _content_title;
	private Dialog          _content_dialog;
	private String          _ad_keywords[];
	private boolean         _ad_keywords_checked;
	private LinearLayout    _navbar;
	private OnTouchListener _navbutton_next=new OnTouchListener(){
        public boolean onTouch(View this_view,MotionEvent event){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    this_view.setBackgroundColor(((ETextView)this_view).getStyle().getBackgroundColorOntouch());
                    break;
                case MotionEvent.ACTION_UP:
                    if(_next_content!=null){
                        try{
                            setContent(_next_content);
                        }catch(Exception e){
                            Tools.logException(e);
                        }
                    }else{
                        Toast.makeText(this_view.getContext(),this_view.getContext().getResources().getString(R.string.nav_next_not_found),Toast.LENGTH_LONG).show();
                    }
                case MotionEvent.ACTION_CANCEL:
                    this_view.setBackgroundColor(((ETextView)this_view).getStyle().getBackgroundColor());
                    break;
            }
            return true;
        }
    };
    private OnTouchListener _navbutton_previous=new OnTouchListener(){
        public boolean onTouch(View this_view,MotionEvent event){           
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    this_view.setBackgroundColor(((ETextView)this_view).getStyle().getBackgroundColorOntouch());
                    break;
                case MotionEvent.ACTION_UP:
                    if(_previous_content!=null){
                        try{
                            setContent(_previous_content);
                        }catch(Exception e){
                            Tools.logException(e);
                        }
                    }else{
                        Toast.makeText(this_view.getContext(),this_view.getContext().getResources().getString(R.string.nav_previous_not_found),Toast.LENGTH_LONG).show();
                    }
                case MotionEvent.ACTION_CANCEL:
                    this_view.setBackgroundColor(((ETextView)this_view).getStyle().getBackgroundColor());
                    break;
            }
            
            return true;
        }
    };
	private String          _next_content;
	private SpanParser      _parser;
	private String          _previous_content;
	private int             _xml_resource;
	/*End Member Variables*/
	
	
	/*Begin Styles*/
	private TypedArray _content_content_style;
	private TypedArray _content_header_style;
	private TypedArray _content_note_style;
	private TypedArray _content_note_content_style;
	private TypedArray _content_note_title_style;
	private TypedArray _content_warning_style;
	/*End Styles*/
	
	
	/*Begin Initializer Methods*/
	/**
	 * Sets all the attributes of this object to their default value.
	 */
	private void _initialize(){
		ScrollView content_scroller=null; //I see no good reason to keep this as a member variable.  It can be (relatively) easily accessed by accessing the first child of this object.
		
		
		/*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
		//Initialize This Object
		this.setOrientation(LinearLayout.VERTICAL);
		this._ad_keywords_checked=false;
		
		//Initialize the content
		this.getContent().addView(this.getContentTitle(),new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		this.getContent().addView(this.getContentBody(),new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
		
		//Initialize the content ScrollView
		content_scroller=new ScrollView(this.getContext());
		content_scroller.setFillViewport(true);
		content_scroller.addView(this.getContent());
		
		//Add the children to this view
		super.addView(content_scroller,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
		super.addView(this.getNavbar(),new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	
	/**
	 * Sets all the attributes of this object to their default value using the attributes given to it.  Upon successful completion, this method calls <code>{@link Content#_initialize}<code>
	 * 
	 * @param attributes Any attributes this objects should be initialized with.
	 * 
	 * @throws InflateException Throws an InflateException if the "default_content" or "content_xml" attribute is not specified.
	 */
	private void _initialize(AttributeSet attributes) throws InflateException{
		Padding    padding=null;
		TypedArray resource=null;
		TypedArray resources=null;
		
		
		/*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
		resources=getContext().obtainStyledAttributes(attributes,R.styleable.Content);
		
		//Check to see if this view was passed teh information it needs to continue
		if(!resources.hasValue(R.styleable.Content_default_content)){
			throw new InflateException("You must include the \"default_content\" attribute when inflate a Content object from XML.");
		}
		if(!resources.hasValue(R.styleable.Content_content_xml)){
		    throw new InflateException("You must include the \"content_xml\" attribute when inflate a Content object from XML.");
		}
		
		//Set up the base styles.
		if(resources.hasValue(R.styleable.Content_content_title_style)){
			this.getContentTitle().setStyle(this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_title_style,-1),R.styleable.ETextView));
		}
		if(resources.hasValue(R.styleable.Content_content_body_style)){
			resource=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_body_style,-1),R.styleable.ELinearLayout);
			
			this.getContentBody().setBackgroundColor(resource.getColor(R.styleable.ELinearLayout_background_color,0));
			padding=Padding.getFromAttribute(resource.getString(R.styleable.ELinearLayout_padding));
			this.getContentBody().setPadding(padding.getLeft(),padding.getTop(),padding.getRight(),padding.getBottom());
			
			resource.recycle();
			resource=null;
		}
		if(resources.hasValue(R.styleable.Content_navbar_style)){
			resource=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_navbar_style,-1),R.styleable.ELinearLayout);
			
			
			this.getNavbar().setBackgroundColor(resource.getColor(R.styleable.ELinearLayout_background_color,0));
			this.getNavbar().getChildAt(1).setBackgroundColor(resource.getColor(R.styleable.ELinearLayout_background_color,0));
			padding=Padding.getFromAttribute(resource.getString(R.styleable.ELinearLayout_padding));
			this.getNavbar().setPadding(padding.getLeft(),padding.getTop(),padding.getRight(),padding.getBottom());
			
			resource.recycle();
			resource=null;
		}
		if(resources.hasValue(R.styleable.Content_navbutton_next_style)){
			((ETextView)this.getNavbar().getChildAt(2)).setStyle(this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_navbutton_next_style,-1),R.styleable.ETextView));
		}
		if(resources.hasValue(R.styleable.Content_navbutton_previous_style)){
			((ETextView)this.getNavbar().getChildAt(0)).setStyle(this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_navbutton_previous_style,-1),R.styleable.ETextView));
		}
		
		//Set the styles which will allow for future styling
		if(resources.hasValue(R.styleable.Content_content_content_style)){
			this._content_content_style=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_content_style,-1),R.styleable.ETextView);
		}else{
			this._content_content_style=null;
		}
		if(resources.hasValue(R.styleable.Content_content_header_style)){
			this._content_header_style=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_header_style,-1),R.styleable.ETextView);
		}else{
			this._content_header_style=null;
		}
		if(resources.hasValue(R.styleable.Content_content_note_style)){
			this._content_note_style=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_note_style,-1),R.styleable.ELinearLayout);
		}else{
			this._content_note_style=null;
		}
		if(resources.hasValue(R.styleable.Content_content_note_content_style)){
			this._content_note_content_style=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_note_content_style,-1),R.styleable.ETextView);
		}else{
			if(this._content_content_style==null){
				this._content_note_content_style=null;
			}else{
				this._content_note_content_style=this._content_content_style;
			}
		}
		if(resources.hasValue(R.styleable.Content_content_note_title_style)){
			this._content_note_title_style=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_note_title_style,-1),R.styleable.ETextView);
		}else{
			this._content_note_title_style=null;
		}
		if(resources.hasValue(R.styleable.Content_content_warning_style)){
			this._content_warning_style=this.getContext().obtainStyledAttributes(resources.getResourceId(R.styleable.Content_content_warning_style,-1),R.styleable.ETextView);
		}else{
			this._content_warning_style=null;
		}
		
		
		this._xml_resource=resources.getResourceId(R.styleable.Content_content_xml,-1);
		try{
			this.setContent(resources.getString(R.styleable.Content_default_content));
		}catch(Exception e){
			Tools.logException(e);
		}
		
		resources.recycle();
		
		
		this._initialize();
	}
	/*End Initializer Methods*/
	
	/*Begin Constructors*/
	/**
	 * Constructs the Object
	 * 
	 * @param context The context in which the object is being constructed in.
	 */
	public Content(Context context){
		super(context);
		
		try{
			this._initialize();
		}catch(Exception e){
			Tools.logException(e);
		}
	}
	
	/**
	 * Constructs the Object
	 * 
	 * @param context The context in which the object is being constructed in.
	 * @param attributes Any attributes this objects should be initialized with.
	 */
	public Content(Context context,AttributeSet attributes){
		super(context,attributes);
		
		try{
			this._initialize(attributes);
		}catch(Exception e){
			Tools.logException(e);
		}
	}
	/*End Constructors*/
	
	/*Begin Finalize Method*/
	/**
	 * Performs any cleanup that needs to be done for this object.
	 */
	protected void finalize(){
		this._ad.destroy();
		
		if(this._content_content_style!=null){
			if(this._content_note_content_style==this._content_content_style){
				this._content_content_style.recycle();
				this._content_content_style=null;
				this._content_note_content_style=null;
			}else{
				this._content_content_style.recycle();
				this._content_content_style=null;
			}
		}
		if(this._content_header_style!=null){
			this._content_header_style.recycle();
			this._content_header_style=null;
		}
		if(this._content_note_style!=null){
			this._content_note_style.recycle();
			this._content_note_style=null;
		}
		if(this._content_note_content_style!=null){
			this._content_note_content_style.recycle();
			this._content_note_content_style=null;
		}
		if(this._content_note_title_style!=null){
			this._content_note_title_style.recycle();
			this._content_note_title_style=null;
		}
		if(this._content_warning_style!=null){
			this._content_warning_style.recycle();
			this._content_warning_style=null;
		}
		
		try{
		    super.finalize();
		}catch(Throwable e){
		    //TODO_HIGH:  Decide what to do with the Throwable
		}
	}
	/*End Finalize Method*/
	
	/*Begin Overridden Methods*/
	@Override public void addView(View view){
		//We don't want anyone accidentally adding any views to this object, so we're just going to not let it run properly.
	}
	
	@Override public void addView(View view,ViewGroup.LayoutParams params){
		//See addView(View view)
	}
	/*End Overridden Methods*/
	
	/*Begin Getter Methods*/

	/**
	 * Gets the private member variable "content".  If the content variable hasn't been instantiated yet, this method will instantiate the object.
	 */
	public LinearLayout getContent(){
		if(this._content==null){
			this._content=new LinearLayout(this.getContext());
			this._content.setOrientation(LinearLayout.VERTICAL);  //Should I set this here?  Should that be a function of the initialization of content?
		}
		
		return this._content;
	}
	
	/**
     * Gets the private member variable "content_body".  If the content variable hasn't been instantiated yet, this method will instantiate the object.
     */
	public LinearLayout getContentBody(){
		if(this._content_body==null){
			this._content_body=new LinearLayout(this.getContext());
			this._content_body.setOrientation(LinearLayout.VERTICAL);  //Again, should this be set here?
		}
		
		return this._content_body;
	}
	
	/**
     * Gets the private member variable "content_title".  If the content variable hasn't been instantiated yet, this method will instantiate the object.
     */
	public ETextView getContentTitle(){
		if(this._content_title==null){
			this._content_title=new ETextView(this.getContext());
		}

		return this._content_title;
	}
	
	/**
     * Gets the private member variable "navbar".  If the content variable hasn't been instantiated yet, this method will instantiate the object.  This method will also initialize any Views it contains (I.E.: the navigation buttons and the ad).
     */
	public LinearLayout getNavbar(){
	    int         display_width=-1;
	    FrameLayout framelayout=null;
		ETextView   textview=null;
		
		
		/*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
		if(this._navbar==null){
			this._navbar=new LinearLayout(this.getContext());
			this._navbar.setOrientation(LinearLayout.HORIZONTAL);  //And again...  SHOULD THIS BE SET HERE?
			
			textview=new ETextView(this.getContext());
			textview.setText("Previous");
			textview.setOnTouchListener(this._navbutton_previous);
			this._navbar.addView(textview,new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1));
			textview=null;
			
			//Set up teh add
			framelayout=new FrameLayout(this.getContext());
			//Let's check if ads are enabled.
			if(Main.preferences.getBoolean("ads_enabled",true)){
				display_width=((WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()/2;  //I don't want the add to ever take up more than half of the user's available screen width.
				
				if(display_width>=340&&display_width<488){
					this._ad=new AdView(Main.activity,com.google.ads.AdSize.BANNER,"a14f0de96a8ab7d");
				}else if(display_width>=488&&display_width<728){
					this._ad=new AdView(Main.activity,com.google.ads.AdSize.IAB_BANNER,"a14f0de96a8ab7d");
				}else if(display_width>=728){
					this._ad=new AdView(Main.activity,com.google.ads.AdSize.IAB_LEADERBOARD,"a14f0de96a8ab7d");
				}
				
				if(this._ad!=null){  //If the user's screen is too small for the ad, we just won't show an ad.
					this._ad.setGravity(Gravity.BOTTOM);
					this._ad.setAdListener(_ad_actions);
					
					this._ad.loadAd(this._getNewAdRequest());
					
					framelayout.addView(this._ad);
					framelayout.setPadding(10,10,10,0);
				}
			}

			this._navbar.addView(framelayout,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT));
			
			textview=new ETextView(this.getContext());
			textview.setText("Next");
			textview.setOnTouchListener(this._navbutton_next);
			this._navbar.addView(textview,new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1));
			textview=null;
		}
		
		return this._navbar;
	}
	
	/**
     * Gets the private member variable "content_history".  If the content variable hasn't been instantiated yet, this method will instantiate the object.
     */
	public List<String> getContentHistory(){
		if(this._content_history==null){
			this._content_history=new ArrayList<String>();
		}
		
		return this._content_history;
	}
	
	/**
     * Gets a new request for an ad.
     */
	private AdRequest _getNewAdRequest(){ //TODO:  Move this (and all the stuff that goes with it) to a Class of its own.
		int       keywords_resid=-1;
	    AdRequest request=null;
		
		
	    /*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
	    request=new AdRequest();
	    
	    //Check to see if the user has defined keywords for the ad, and if they have cache them, and add them to the request.
        if(!this._ad_keywords_checked){
    	    keywords_resid=this.getContext().getResources().getIdentifier("ad_keywords","string-array",this.getContext().getPackageName());
    	    if(keywords_resid!=0){
    	        this._ad_keywords=this.getContext().getResources().getStringArray(keywords_resid);
    	    }
    	    
    	    this._ad_keywords_checked=true;
        }
        
        if(this._ad_keywords!=null){
            for(String keyword:this._ad_keywords){
                request.addKeyword(keyword);
            }
        }
            
		return request;
	}
	
	private SpanParser getParser(){
	    if(this._parser==null){
	        this._parser=new SpanParser();
	        
	        //TODO:  find a better way to do this than manually adding these span handlers like this
	        this._parser.registerSpan("audio",AudioSpan.class);
	        this._parser.registerSpan("contentlink",ContentlinkSpan.class);
	        this._parser.registerSpan("style",StyleSpan.class);
	        this._parser.registerSpan("vocab",VocabSpan.class);
	    }
	    
	    return this._parser;
	}
	/*End Getter Methods*/
	
	/*Begin Other Essential Methods*/
	/**
	 * Parses the string that was passed to it and passes the parsed information onto <code>setContent(ContentType,String)</code>
	 * 
	 * @param content Content to set this View to.  Should be in the form of "namespace:item_name" or simply "item_name" and the namespace will default to "untyped"
	 * 
	 * @throws Exception Throws an Exception if the content string is improperly formatted.
	 * 
	 * @see Content#setContent(ContentType, String)
	 */
	public void setContent(String content) throws Exception{
		String      content_name=null;
		ContentType content_namespace=Content.ContentType.UNTYPED;  //Recall that the namespace will default to "untyped" if one isn't given.
		String      exploded_content[]=null;
		
		
		/*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
		exploded_content=content.split(":");
		if(exploded_content.length>2){
			throw new Exception("Incorrectly formated string input as parameter to setContent(String).");
		}
		
		//NOTE:  If content only has one field, that field is assumed to be the content's name.  And if that's the case, the content's namespace should be assumed to be of type untyped.
		if(exploded_content.length==2){
			if(exploded_content[0].equalsIgnoreCase("untyped")){
				content_namespace=Content.ContentType.UNTYPED;
			}else if(exploded_content[0].equalsIgnoreCase("lesson")){
				content_namespace=Content.ContentType.LESSON;
			}else if(exploded_content[0].equalsIgnoreCase("quiz")){
				content_namespace=Content.ContentType.QUIZ;
			}
			
			content_name=exploded_content[1];
		}else{
			content_name=content;
		}
		
		this.setContent(content_namespace,content_name);
	}
	
	
	/**
	 * Parses the contents of the XML that was given to it at construction, and sets the contents of this View to reflect the particular item in the XML whose details matches those passed into this method.
	 * 
	 * @param namespace The top level content type.
	 * @param content_name The field which should be matched when looking for a particular item.
	 */
	public void setContent(final ContentType namespace,final String content_name){
	    boolean                   continue_looping=true;
        boolean                   correct_namespace=false;
        LinearLayout.LayoutParams default_layout_params=null;
        boolean                   note_parsed=false;
        boolean                   table_parsed=false;
        XmlResourceParser         xml=null;
        
        
        /*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
        //Some temporary resource objects   
        LinearLayout linearlayout=null;
        int          number=-1;
        Padding      padding=null;
        TableLayout  tablelayout=null;
        TableRow     tablerow=null;
        ETextView    textview=null;
		
        
        try{
            /*  TODO:  Add this back in once it's fixed
		    if(this._content_dialog==null){
		        this._content_dialog=new Dialog(this.getContext());
		        linearlayout=(LinearLayout)Main.activity.getLayoutInflater().inflate(R.layout.content_wait_dialog,null);
		        
		        this._content_dialog_progressbar=(ProgressBar)linearlayout.getChildAt(0);
		        this._content_dialog_text=(ETextView)linearlayout.getChildAt(1);
		        
		        this._content_dialog.setTitle("Loading Requested Content");
		        this._content_dialog.addContentView(linearlayout,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		    }
		    this._content_dialog.show();
		    */
		    
			this.scrollToTop();
			
			default_layout_params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			xml=this.getContext().getResources().getXml(this._xml_resource);
			
			//NOTE:  In an effort to keep this a bit more condensed/readable, I'm going to do this in two seperate phases.  A phase in which the loops finds the content, and a phase in which the loop parses the content.
			
			//PHASE ONE
			/*  TODO:  Add this back in once it's fixed
			this._content_dialog_text.setText("Checking for the requested content.");
			*/
			continue_looping=true;  //NOTE:  Because this is set when the method is called, this isn't really needed. Only adding for program readability.
			while(continue_looping){
				switch(xml.next()){
					case XmlPullParser.START_TAG:
						//Begin checking the current namespace
						if(xml.getName().equals("untyped")){
							if(namespace==Content.ContentType.UNTYPED){
								correct_namespace=true;
							}else{
								correct_namespace=false;
							}
						}else if(xml.getName().equals("lesson")){
							if(namespace==Content.ContentType.LESSON){
								correct_namespace=true;
							}else{
								correct_namespace=false;
							}
						}else if(xml.getName().equals("quiz")){
							if(namespace==Content.ContentType.QUIZ){
								correct_namespace=true;
							}else{
								correct_namespace=false;
							}
						}
						
						if(!correct_namespace){  //It doesn't matter what the current tag is, break if we aren't in teh correct namespace
							break;
						}
						//End Checking the current namespace
						
						
						if(xml.getName().equals("item")){                                //If the XML element we're looking at is "item",
							if(xml.getAttributeValue(null,"name").equals(content_name)){ //And the name attribute of that element equals the parameter "content_name"
								continue_looping=false;                                  //We've found out element and should stop this loop, and move onto the next loop
							}
						}
						break;
					case XmlPullParser.END_DOCUMENT:  //As long as the content is found, the parser will never reach this point.
					    /*  TODO:  Add this back in once it's fixed
					    this._content_dialog_text.setText("Content could not be found.  Setting up 404 page.");
					    */
					    Log.e("JP","User encountered a 404 page when trying to access namespace=\""+namespace+"\" and content_name=\""+content_name+"\".");
					    
						this.getContentTitle().setText(this.getContext().getResources().getString(R.string.content_not_found_title));
						
						textview=new ETextView(this.getContext());
						textview.setText(this.getContext().getResources().getString(R.string.content_not_found_message));
						textview.setStyle(this._content_content_style);
						
						this.getContentBody().removeAllViews();
						this.getContentBody().addView(textview,default_layout_params);
						
						this.getContentHistory().add("");  //The reason we're adding a blank string is, is so that when the user presses the back button, it will actually go back to teh correct element.
						this._next_content=null;     //Set both of these to null so that the user
						this._previous_content=null; //doesn't keep pressing these buttons.
						
						this._content_dialog.dismiss();
						
						return;  //If we reach this point, we can't do anything else in this method.  Just exit it.
				}
			}
			
			//NOTE:  At this point, the XML variable will be at the beginning of the item tag corresponding to the content with content_name and in the correct namespace.
			//PHASE TWO
			/*  TODO:  Add this back in once it's fixed
			this._content_dialog_text.setText("Content Found.  Parsing data content data.");
			*/
			this.getContentBody().removeAllViews();  //Clear out the current items in "content_body"
			
			this._previous_content=xml.getAttributeValue(null,"previous_item");
			this._next_content=xml.getAttributeValue(null,"next_item");
			
			continue_looping=true;
			while(continue_looping){
				switch(xml.next()){
					case XmlPullParser.START_TAG:
						if(xml.getName().equals("title")){
							this.getContentTitle().setText(xml.nextText().trim());
						}else if(xml.getName().equals("header")){
						    textview=new ETextView(this.getContext());
							this.addSpans(textview,xml.nextText().trim());
							if(this._content_header_style!=null){
								textview.setStyle(this._content_header_style);
							}
							this.getContentBody().addView(textview,default_layout_params);
						}else if(xml.getName().equals("content")){
							textview=new ETextView(this.getContext());
							//textview.setText(xml.nextText().trim(),TextView.BufferType.SPANNABLE);
							this.addSpans(textview,xml.nextText().trim());
							if(this._content_content_style!=null){
								textview.setStyle(this._content_content_style);
							}
							this.getContentBody().addView(textview,default_layout_params);
							textview=null;  //NOTE:  Not really needed
						}else if(xml.getName().equals("warning")){
							textview=new ETextView(this.getContext());
							//textview.setText(xml.nextText().trim(),TextView.BufferType.SPANNABLE);
							this.addSpans(textview,xml.nextText().trim());
							if(this._content_warning_style!=null){
								textview.setStyle(this._content_warning_style);
							}
							this.getContentBody().addView(textview,default_layout_params);
							textview=null;  //NOTE:  Not really needed
						}else if(xml.getName().equals("note")){
							linearlayout=new LinearLayout(getContext());
							linearlayout.setOrientation(LinearLayout.VERTICAL);
							if(this._content_note_style!=null){
								linearlayout.setBackgroundColor(this._content_note_style.getColor(R.styleable.ELinearLayout_background_color,0));
								padding=Padding.getFromAttribute(this._content_note_style.getString(R.styleable.ELinearLayout_padding));
								linearlayout.setPadding(padding.getLeft(),padding.getTop(),padding.getRight(),padding.getBottom());
							}
							
							note_parsed=false;
							while(!note_parsed){
								switch(xml.next()){
									case XmlPullParser.START_TAG:
										if(xml.getName().equals("title")){
											textview=new ETextView(this.getContext());
											//textview.setText(xml.nextText().trim(),TextView.BufferType.SPANNABLE);
											this.addSpans(textview,xml.nextText().trim());
											if(this._content_note_title_style!=null){
												textview.setStyle(this._content_note_title_style);
											}
											linearlayout.addView(textview,default_layout_params);
											textview=null;  //NOTE:  Not really needed
										}else if(xml.getName().equals("content")){
											textview=new ETextView(this.getContext());
											//textview.setText(xml.nextText().trim(),TextView.BufferType.SPANNABLE);
											this.addSpans(textview,xml.nextText().trim());
											if(this._content_note_content_style!=null){
												textview.setStyle(this._content_note_content_style);
											}
											linearlayout.addView(textview,default_layout_params);
											textview=null;  //NOTE:  Not really needed
										}else if(xml.getName().equals("table")){
										    tablelayout=new TableLayout(this.getContext());
										    
										    table_parsed=false;
										    while(!table_parsed){
										        switch(xml.next()){
										            case XmlPullParser.START_TAG:
										                if(xml.getName().equals("row")){
										                    tablerow=new TableRow(this.getContext());
										                }else if(xml.getName().equals("cell")){
										                    if(tablerow==null){
										                        Log.e("JP","Cells may only be added to \"rows\".");
										                        
										                        break;
										                    }
										                    number=xml.getAttributeIntValue(null,"span",1);
										                    
										                    //TODO_HIGH:  Find out why the Audio span isn't clickable within a table.
										                    textview=new ETextView(this.getContext());
										                    textview.setStyle(this._content_note_content_style);
										                    textview.getStyle().setBorder(new Border(1,0xFF000000));
										                    textview.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
										                    this.addSpans(textview,xml.nextText());
										                    
										                    tablerow.addView(textview,new TableRow.LayoutParams(0,TableRow.LayoutParams.MATCH_PARENT,number));
										                }
										                
										                break;
										            case XmlPullParser.END_TAG:
										                if(xml.getName().equals("table")){
										                    table_parsed=true;
										                }else if(xml.getName().equals("row")){
										                    if(tablerow.getChildCount()==1){  //If the row only has one child, just add it directly to the table to save resources.
										                        tablelayout.addView(tablerow.getChildAt(0));
										                    }else{
										                        tablelayout.addView(tablerow);
										                    }
										                    
										                    tablerow=null;
										                }
										                
										                break;
										        }
										    }
										    
										    if(tablelayout.getChildCount()>0){
										        linearlayout.addView(tablelayout,default_layout_params);
										    }
										}
										
										break;
									case XmlPullParser.END_TAG:
										if(xml.getName().equals("note")){
											note_parsed=true;
										}
										
										break;
								}
							}
							
							this.getContentBody().addView(linearlayout,default_layout_params);
							linearlayout=null;
						}
						
						break;
					case XmlPullParser.END_TAG:
						if(xml.getName().equals("item")){
							continue_looping=false;
						}
						
						break;
				}
			}	
			/*  TODO:  Add this back in once it's fixed
			this._content_dialog_text.setText("Adding this page to the history.");
			*/
			this._addHistoryElement(namespace,content_name);  //We've added all our content.  There's no way anything can go wrong if we hit this point.  Add this history item to the history.
			/*  TODO:  Add this back in once it's fixed
			this._content_dialog.dismiss();
			*/
        }catch(Exception e){
            Tools.logException(e);
        }
    }
	
	/**
	 * Adds an item to this View's view history.
	 * 
	 * @param type Top level content type that the parameter <code>name</code> is located in.
	 * @param name The value to match against the XML item element's "name" attribute
	 */
	private void _addHistoryElement(ContentType type,String name){
		String       element=null;
		List<String> history=null;
		
		
		/*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
		switch(type){
			case UNTYPED:
				element="untyped:"+name;  //Though simply putting element=name would have worked, I want to go ahead and add teh fully qualified name.
				break;
			case LESSON:
				element="lesson:"+name;
				break;
			case QUIZ:
				element="quiz:"+name;
				break;
		}
		
		history=this.getContentHistory();
		if(history.size()==0){
			history.add(new String(element));
		}else if(!history.get(history.size()-1).equals(element)){  //Make sure the last element in the list isn't the current page.
			if(history.size()==Content.MAX_HISTORY_SIZE){
				history.remove(0);
			}
			
			history.add(new String(element));
		}
	}
	
	/**
	 * Adds spans to the TextView passed to it.  Will return immediately if the TextView passed to it is null.
	 * 
	 * @param view TextView whose text should have spans added to it.
	 */
	private void addSpans(TextView view,String text){			
		/*-------------------------*\
        |     Begin Method Code     |
        \*-------------------------*/
		if(view==null){
			return;
		}
		
		view.setText(this.getParser().parseSpans(text),TextView.BufferType.SPANNABLE);
		view.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	/**
	 * Causes the ScrollView to scroll to its topmost point.
	 */
	public void scrollToTop(){
	    ScrollView scrollview=((ScrollView)this.getChildAt(0));
	    
	    if(scrollview!=null){  //the only time it should equal null is during the initial construction of this object.
	        //TODO:  Figure out how to stop the scrollview from scrolling after this method gets called.
	        scrollview.scrollTo(0,0);
	    }
	}
	/*End Other Essential Methods*/
}