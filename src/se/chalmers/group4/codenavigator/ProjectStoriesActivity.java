package se.chalmers.group4.codenavigator;

import java.util.HashMap;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssue.Label;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;

import android.widget.TextView;

public class ProjectStoriesActivity extends Activity {
	public final static int C_PAIR_PROGRAMMERS = 1;
	private TextView textViewStories;
	private HashMap<String, GHIssue> storyList;
	private GHRepository githubRepository;
	private GHIssue story;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_project_stories);
		NavigationBar.load_navbar(this,3);
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
				for (PagedIterator<GHIssue> stories =  repoStories; stories.hasNext();){
    				// Get the next story
    				GHIssue thisStory = stories.next();
    			
    				storyList.put(Integer.toString(i), thisStory);
    				
    				// Add it to the UI text
    				List<Label> lines = (List<Label>) thisStory.getLabels();
    				StringBuilder labels = new StringBuilder();
    				for (Label label: lines){
    					labels.append("[");
    					labels.append(label.getName());
    					labels.append("] ");
    				}
    				
    				finalText.append(Integer.toString(i) + " => " + thisStory.getTitle() + " " +labels.toString()+'\n');
    				i++;
            	}

				// Return the stories list flat text to be written in the UI
				return finalText.toString();

			} catch (Exception e) {
				Log.d("GithubProject", "Exception during project loading : + "
						+ e.toString());
				// Exception
				return "Unexpected exception...";

			}

		}

		@Override
		protected void onPostExecute(String result) {
			// Write the result to the UI.
			textViewStories.setText(result);

		}
	}


}
