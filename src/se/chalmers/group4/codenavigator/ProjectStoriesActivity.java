package se.chalmers.group4.codenavigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssue.Label;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProjectStoriesActivity extends Activity {
	public final static int C_PAIR_PROGRAMMERS = 1;
	private TextView textViewStories;
	private HashMap<String, GHIssue> storyList;
	private GHRepository githubRepository;
	private GHIssue story;
	private List<LabelColor> labelColors; 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_project_stories);
		NavigationBar.load_navbar(this,2);
		NavigationBar.insert_main_layout(this, R.layout.activity_project_stories);
		
		// Story List TextView (ui)
		this.textViewStories = (TextView) findViewById(R.id.TextViewStories);

		// Initialize an HashMap "ID"/Story, to be fill up by the
		// ProjectsLoadTask
		this.storyList = new HashMap<String, GHIssue>();

		// Get the selected repository from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
		this.githubRepository = app.getGithubRepository();

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
		// TODO Auto-generated method stub
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
	}
    public void doSelectStory(View v) throws Exception {

		// Retrieve the EditText in the UI
		EditText selectedStory = (EditText)findViewById(R.id.selectedStory);

		String storyId = selectedStory.getText().toString();

		this.story = this.storyList.get(storyId);
		
		// Check if this given ID corresponds to a listed story
		if(this.story == null) {
			// Doesn't exist error
			// TODO display an error message ???
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
    


	private void doShowAllTeamMembers(View v) {
		Log.d("assigning","show all members clicked");
		Intent intent = new Intent(this, AssigningpairActivity.class);
		startActivity(intent);
	}	
	

	
	/**
	 * Private inner class inherited from AsyncTask used for loading the stories
	 * list in a new thread.
	 * 
	 * This is made with a loop on the stories list (retrieved from the global
	 * Github Repository Object stored in the app class)
	 */
	private class StoriesLoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... v) {
			StringBuilder finalText = new StringBuilder();

			try {
				
				// Repositories Stories List iterator
				PagedIterator<GHIssue> repoStories = githubRepository.listIssues(GHIssueState.OPEN).iterator();
				
				int i = 1;
				labelColors = new ArrayList<LabelColor>();
				
				for (PagedIterator<GHIssue> stories =  repoStories; stories.hasNext();){
    				// Get the next story
    				GHIssue thisStory = stories.next();
    			
    				storyList.put(Integer.toString(i), thisStory);
    				
    				// Add it to the UI text
    				List<Label> lines = (List<Label>) thisStory.getLabels();
    				StringBuilder labels = new StringBuilder();
    				finalText.append(Integer.toString(i) + " => " + thisStory.getTitle() + " " );
    				int startColor = finalText.length();
    				for (Label label: lines){
    				//	labels.append("[");
    					labels.append(label.getName());
    					labels.append(" ");
    					int endColor = startColor + label.getName().length();
    					LabelColor l = new LabelColor();
    					l.setStartColor(startColor);
    					l.setEndColor(endColor);
//    					if (label.getName().equalsIgnoreCase("Bug")){
//    						l.setColor(Color.rgb(185, 96, 96));
//    					}
//    					else if (label.getName().equalsIgnoreCase("Story")){
//    						l.setColor(Color.rgb(95, 125, 185));
//    					}
//    					else {
//    						l.setColor(Color.rgb(125, 185, 96));
//    					}
    					l.setColor(Color.parseColor("#" + label.getColor()));
    					labelColors.add(l);
    					startColor = startColor + label.getName().length() + 1;
    				}
    				
    				finalText.append(labels.toString()+'\n');
    				
    				i++;
            	}

				// Return the stories list flat text to be written in the UI
				return finalText.toString();

			} catch (Exception e) {
				Log.d("GithubProject", "Exception during project loading : "
						+ e.getMessage());
				// Exception
				return "Unexpected exception...";

			}

		}

		@Override
		protected void onPostExecute(String result) {
			// Write the result to the UI.

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

		}
	}


}
