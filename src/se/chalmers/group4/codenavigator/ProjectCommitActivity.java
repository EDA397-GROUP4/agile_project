package se.chalmers.group4.codenavigator;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitStatus;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
		
		// Scheduling recurrent task for once per 1 minute
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new RecurrentTask(), 0, 1, TimeUnit.MINUTES);
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
            	
            	// Save the current time as the latest update time of the commit view
            	CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication(); 
            	app.setUpdateTime(System.currentTimeMillis());
            	Log.d("TIME NOW", "" + app.getUpdateTime());
            	
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
    
    /**
	 * Private inner class implementing Runnable
	 * used for recurrently checking for new commits.
	 * This is made with a loop on the commit list, comparing each commit's creation date 
	 * to the latest update date of the commit view
	 */	
	private class RecurrentTask implements Runnable {

		@Override
		public void run() {
			try {
            	
            	// Repositories Commit List iterator
            	PagedIterator<GHCommit> repoCommits = githubRepository.listCommits().iterator();
            	Log.d("GET REPOS", repoCommits.toString());
            	
            	// Get the time of the latest update
            	CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();   
            	Long updateTime = app.getUpdateTime();
            	Log.d("LAST TIME", "" + updateTime);
            	
            	int nrNewCommits = 0;
            	
            	for (PagedIterator<GHCommit> commits =  repoCommits; commits.hasNext();){
    				// Get the next commit 
    				GHCommit thisCommit = commits.next();
    				List<GHCommitStatus> list = thisCommit.listStatuses().asList();
    				Log.d("COMM STATUS LIST", "" + list.size());
    				if(thisCommit.getLastStatus() == null) {
    					Log.d("COMM STATUS", "NO STATUS");
    					continue;
    				}
    				Log.d("COMM STATUS", thisCommit.getLastStatus().toString());
    				// Count the new commits
    				if (thisCommit.getLastStatus().getCreatedAt().getTime() > updateTime){
    					nrNewCommits = + 1;
    				}
            	}
            	Log.d("NEW COMMITS", ""+ nrNewCommits);
            	//Updating the UI with a message showing the number of new commits, if any
            	if (nrNewCommits > 0) {
            		final int nr = nrNewCommits;
            		runOnUiThread(new Runnable(){
            			public void run(){
            				TextView update = (TextView)findViewById(R.id.textViewUpdates);
            				update.setText(getText(R.string.commits_update) + " " + nr);
            				update.setVisibility(View.VISIBLE);		
            			}
            		});
            	}
            }
    		catch (Exception e) {
    			Log.d("GithubNotificationUpdate", "Exception during commit notification loading : + " + e.toString());
 			} 
			
		}
		
	}
	
}
