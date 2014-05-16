package se.chalmers.group4.codenavigator;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssue.Label;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProjectStoriesActivity extends Activity {
	public final static int C_PAIR_PROGRAMMERS = 1;
	//private TextView textViewStories;
	//private HashMap<String, GHIssue> storyList;
	private GHRepository githubRepository;
	//private GHIssue story;
	//private List<LabelColor> labelColors;
	
	private StoryAdapter storyAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_project_stories);
		NavigationBar.load_navbar(this,2);
		NavigationBar.insert_main_layout(this, R.layout.activity_project_stories);
		
		// Story List TextView (ui)
		//this.textViewStories = (TextView) findViewById(R.id.TextViewStories);

		// Initialize an HashMap "ID"/Story, to be fill up by the
		// ProjectsLoadTask
		//this.storyList = new HashMap<String, GHIssue>();

		// Get the selected repository from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
		this.githubRepository = app.getGithubRepository();

		
		// Construct the data source
	    ArrayList<StoryListItem> storyListArray = new ArrayList<StoryListItem>();
	    // Create the adapter to convert the array to views
	    this.storyAdapter = new StoryAdapter(this, storyListArray);
	    
	    // Attach the adapter to a ListView
	    ListView listView = (ListView) findViewById(R.id.listview_story);
	    listView.setAdapter(this.storyAdapter);
		
	    // Create the click event on the ListView
	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final StoryListItem item = (StoryListItem) parent.getItemAtPosition(position);
				Log.d("Click",item.getTitle());
				selectStory(item.getStoryObject());
			}	
	    });
		
		
		// Create and launch the StoriesLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new StoriesLoadTask().execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.project_stories, menu);
		ActionBar bar = getActionBar();
		bar.setTitle("Stories");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.log_out:
			logOut();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logOut() {
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
	}
	
	
    private void showInformation(String message, boolean loading) {
    	TextView message_view = (TextView)findViewById(R.id.TextViewStoriesMessage);
    	ProgressBar loading_view = (ProgressBar)findViewById(R.id.ProgressBarStory);
    	LinearLayout layout_view = (LinearLayout)findViewById(R.id.LayoutStoryInfo);
    	message_view.setText(message);
    	if(loading) {
    		loading_view.setVisibility(View.VISIBLE);
    	}
    	else {
    		loading_view.setVisibility(View.GONE);
    	}
    	layout_view.setVisibility(View.VISIBLE);
    }
    
    private void hideInformation() {
    	LinearLayout layout_view = (LinearLayout)findViewById(R.id.LayoutStoryInfo);
    	layout_view.setVisibility(View.GONE);
    }
	
	private void selectStory(GHIssue story) {
		final ListView listview = (ListView)findViewById(R.id.listview_story);
    	listview.setVisibility(View.GONE);
    	showInformation("Loading selected story...", true);
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		app.setCurrentStory(story);
		
		// Start the StoryView Activity
		Intent intent = new Intent(this, StoryViewActivity.class);
		startActivity(intent);
		
		new Handler().postDelayed(new Runnable() {
            public void run() {
            	hideInformation();
            	listview.setVisibility(View.VISIBLE);
            }
        }, 4000);
	}
	
	/*
    public void doSelectStory(View v) throws Exception {

		// Retrieve the EditText in the UI
		EditText selectedStory = (EditText)findViewById(R.id.selectedStory);

		String storyId = selectedStory.getText().toString();

		this.story = this.storyList.get(storyId);
		
		// Check if this given ID corresponds to a listed story
		if(this.story == null) {
			// Doesn't exist error
			// display an error message ???
			return;
		}
		
		int      theChoice = Integer.parseInt(storyId);
		
		switch (theChoice) {
			case C_PAIR_PROGRAMMERS: {
				doShowAllTeamMembers(v);
				break;
			}
			default: {
				Log.d("error","The choice: "+theChoice+" is not implemented yet\n");
				break;
			}
		}
		
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		app.setCurrentStory(story);
		// Start the StoryView Activity
		Intent intent = new Intent(this, StoryViewActivity.class);

		startActivity(intent);

	}
	*/
    

/*
	private void doShowAllTeamMembers(View v) {
		Log.d("assigning","show all members clicked");
		Intent intent = new Intent(this, AssigningpairActivity.class);
		startActivity(intent);
	}	
	
*/
	
	/**
	 * Private inner class inherited from AsyncTask used for loading the stories
	 * list in a new thread.
	 * 
	 * This is made with a loop on the stories list (retrieved from the global
	 * Github Repository Object stored in the app class)
	 */
	private class StoriesLoadTask extends AsyncTask<Void, Void, ArrayList<StoryListItem>> {
		@Override
		protected ArrayList<StoryListItem> doInBackground(Void... v) {
			//StringBuilder finalText = new StringBuilder();
			ArrayList<StoryListItem> storyListArray = new ArrayList<StoryListItem>();
			try {
				
				// Repositories Stories List iterator
				PagedIterator<GHIssue> repoStories = githubRepository.listIssues(GHIssueState.OPEN).iterator();
				
				//labelColors = new ArrayList<LabelColor>();
				
				for (PagedIterator<GHIssue> stories =  repoStories; stories.hasNext();){
    				// Get the next story
    				GHIssue thisStory = stories.next();
    			
    				int id = thisStory.getNumber();
    				String title = thisStory.getTitle();
    				List<Label> labelList = (List<Label>) thisStory.getLabels();
    				storyListArray.add(new StoryListItem(id, title, labelList, thisStory));
            	}

				// Return the stories list flat text to be written in the UI
				//return finalText.toString();
				return storyListArray;

			} catch (Exception e) {
				Log.d("GithubProject", "Exception during stories loading : + " + e.toString());
				
				// Exception
				ArrayList<StoryListItem> errorArray = new ArrayList<StoryListItem>();
				errorArray.add(new StoryListItem(0,"Unexpected exception...", null, null).setError());
				return errorArray;

			}

		}

		@Override
		protected void onPostExecute(ArrayList<StoryListItem> result) {
			// Clear the previous results in the Listview  
			storyAdapter.clear();
			
			if(result.get(0).isError()) {
				// Write the error in the information box and don't display results
        		showInformation(result.get(0).getTitle(), false);
			}
			else {
				// Hide the infomation box
        		hideInformation();
        		// Display all the results
				storyAdapter.addAll(result);
			}
			
			
			// Write the result to the UI.

			/*
		    SpannableString ssb = new SpannableString(result);
		    for (LabelColor l: labelColors){
		    	BackgroundColorSpan bcs = new BackgroundColorSpan(l.getColor());
		    	ssb.setSpan(bcs, l.getStartColor(), l.getEndColor(),
			            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    	
		    	ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE);
		    	ssb.setSpan(fcs, l.getStartColor(), l.getEndColor(),
			            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    }
			textViewStories.setText(ssb);
			*/

		}
	}
	
	
	private class StoryListItem {

		private String title;
		private String id;
		private List<Label> labelList;
		private GHIssue storyObject;
		
		private boolean error;

		public StoryListItem(int id, String title, List<Label> labelList, GHIssue storyObject) {
			this.title = title;
			this.id = Integer.toString(id);
			this.labelList = labelList;
			this.storyObject = storyObject;
			this.error = false;
		}

		public String getTitle() {
			return this.title;
		}
		public String getID() {
			return this.id;
		}
		public SpannableString getColoredLabels() {
			
			// Flatten label text
			StringBuilder labels = new StringBuilder();
			ArrayList<LabelColor> labelColors = new ArrayList<LabelColor>();
			
			for (Label label: this.labelList){
				//	labels.append("[");
					int startColor = labels.length();
					labels.append(label.getName());
					labels.append(" ");
					int endColor = startColor + label.getName().length();
					LabelColor l = new LabelColor();
					l.setStartColor(startColor);
					l.setEndColor(endColor);
					l.setColor(Color.parseColor("#" + label.getColor()));
					labelColors.add(l);
			}
			
			
			SpannableString ssb = new SpannableString(labels);
		    for (LabelColor l: labelColors){
		    	BackgroundColorSpan bcs = new BackgroundColorSpan(l.getColor());
		    	ssb.setSpan(bcs, l.getStartColor(), l.getEndColor(),
			            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    	
		    	ForegroundColorSpan fcs = new ForegroundColorSpan(Color.WHITE);
		    	ssb.setSpan(fcs, l.getStartColor(), l.getEndColor(),
			            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		    }
			return ssb;
		}
		public GHIssue getStoryObject() {
			return this.storyObject;
		}
		public StoryListItem setError() {
			this.error = true;
			return this;
		}
		public boolean isError() {
			return this.error;
		}

	}
	
	private class StoryAdapter extends ArrayAdapter<StoryListItem> {
		private final Context context;
		public StoryAdapter(Context context, ArrayList<StoryListItem> users) {
			super(context, android.R.layout.simple_list_item_1, users);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			StoryListItem item = getItem(position);    

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_story, parent, false);

			// Lookup view for data population
			TextView titleview = (TextView) rowView.findViewById(R.id.StoryTitle);
			TextView labelsview = (TextView) rowView.findViewById(R.id.StoryLabels);
			TextView idview = (TextView) rowView.findViewById(R.id.StoryId);

			// Populate the data into the template view using the data object
			titleview.setText(item.getTitle());
			labelsview.setText(item.getColoredLabels());
			idview.setText(item.getID());

			// Return the completed view to render on screen
			return rowView;
		}
	}

	private class LabelColor {
		private int startColor;
		private int endColor;
		private int color;
		
		public int getStartColor() {
			return startColor;
		}
		public void setStartColor(int startColor) {
			this.startColor = startColor;
		}
		public int getEndColor() {
			return endColor;
		}
		public void setEndColor(int endColor) {
			this.endColor = endColor;
		}
		public int getColor() {
			return color;
		}
		public void setColor(int color) {
			this.color = color;
		}
	}


}
