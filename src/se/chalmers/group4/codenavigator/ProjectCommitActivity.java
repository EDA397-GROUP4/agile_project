package se.chalmers.group4.codenavigator;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

public class ProjectCommitActivity extends Activity {

	private TextView textViewCommits;
	private GHRepository githubRepository;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_commit);
		
		// Commit List TextView (ui) 
		this.textViewCommits = (TextView)findViewById(R.id.TextViewCommits);
		
		// Get the selected repository from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		this.githubRepository = app.getGithubRepository();
		
		// Create and launch the CommitsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new CommitsLoadTask().execute();
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
            
            try {
            	
            	// Repositories Commit List iterator
            	PagedIterator<GHCommit> repoCommits = githubRepository.listCommits().iterator();
    			
            	for (PagedIterator<GHCommit> commits =  repoCommits; commits.hasNext();){
    				// Get the next commit 
    				GHCommit thisCommit = commits.next();
    				
    				// Add it to the UI text
    				finalText.append(thisCommit.getCommitShortInfo().getMessage());
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
