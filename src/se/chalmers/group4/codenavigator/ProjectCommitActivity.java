package se.chalmers.group4.codenavigator;

import java.util.HashMap;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProjectCommitActivity extends Activity {

	private TextView textViewCommits;
	private HashMap<String, String> commitList;
	private GHRepository githubRepository;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_commit);
		
		// Commit List TextView (ui) 
		this.textViewCommits = (TextView)findViewById(R.id.TextViewCommits);
		
		// Initialize an HashMap "ID"/Commit, to be fill up by the ProjectsLoadTask
		this.commitList = new HashMap<String, String>();
		
		// Get the selected repository from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		this.githubRepository = app.getGithubRepository();
		
		// Create and launch the CommitsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new CommitsLoadTask().execute();
	}

	public void doSelectCommit(View v) throws Exception {
		
		// Retrieve the EditText in the UI
				EditText commitID_ed = (EditText)findViewById(R.id.selectedCommit);
				// Get the project ID value
				String projectID = commitID_ed.getText().toString();
				
				// Retrieve the project corresponding to this ID
				String sha1 = this.commitList.get(projectID);
				
				// Check if this given ID corresponds to a listed project
				if(sha1 == null) {
					// Doesn't exist error
					// TODO display an error message ???
					return;
				}
				
				// Start the ProjectCommit Activity
				
				//Intent intent = new Intent(this, DetailedCommitView.class);
				//intent.putExtra("COMMIT_ID", sha1);
				//startActivity(intent);
	}
	
	/**
	 * Private inner class inherited from AsyncTask
	 * used for loading the commit list in a new thread.
	 * 
	 * This is made with a loop on the commit list 
	 * (retrieved from the global Github Repository Object stored in the app class)
	 */
    private class CommitsLoadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... v) {
            StringBuilder finalText = new StringBuilder();
            int i = 1;
            try {
            	
            	// Repositories Commit List iterator
            	PagedIterator<GHCommit> repoCommits = githubRepository.listCommits().iterator();
    			
            	for (PagedIterator<GHCommit> commits =  repoCommits; commits.hasNext();){
    				// Get the next commit 
    				GHCommit thisCommit = commits.next();
    			
    				commitList.put(Integer.toString(i), thisCommit.getSHA1());
    				
    				// Add it to the UI text
    				String[] lines = thisCommit.getCommitShortInfo().getMessage().split("\n");
    				finalText.append(Integer.toString(i) + " => "+lines[0]+'\n');
    				i++;
            	}
            	
            	// Return the commit list flat text to be written in the UI
            	return  finalText.toString();

    		}
    		catch (Exception e) {
    			Log.d("GithubProject", "Exception during project loading : + " + e.toString());
    			// Exception
    			return "Unexpected exception...";
    			
    		} 
            
        }
        
        @Override
        protected void onPostExecute(String result) {
        	// Write the result to the UI.
        	textViewCommits.setText(result);
        	
       }
    }

}
