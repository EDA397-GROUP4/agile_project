package se.chalmers.group4.codenavigator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kohsuke.github.GHEvent;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestCommitDetail.Commit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitUser;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DetailedCommitView extends Activity {
	private TextView textViewProjects;
	private HashMap<String, GHRepository> repoList;
	private GitHub githubObject;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		
		// Project List TextView (ui)
		this.textViewProjects = (TextView)findViewById(R.id.TextViewProjects);
		
		// Initialize an HashMap "ID"/Repository, to be fill up by the ProjectsLoadTask
		this.repoList = new HashMap<String, GHRepository>();
		
		// Get the global Github Object from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
        this.githubObject = app.getGithubObject();
		
		// Create and launch the ProjectsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new DetailedCommitLoadTask().execute();
		
	}
		
	private class DetailedCommitLoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... v) {
			StringBuilder finalText = new StringBuilder(); // Project list flat text for the UI
			finalText.append("\nCommitters..");	            
			try {
				/* 
				 * Github Myself Object : allows to access to user personal repositories
				 * and user organizations 
				 */
				GHMyself myself = githubObject.getMyself();
				Log.d("Details", "myself "+myself.getName());	            	
				// Getting User Commits (.. now at first my own..)

				// Get User organizations
				Iterator<GHOrganization> userOrganizations = myself.getAllOrganizations().iterator();
				while(userOrganizations.hasNext()){
					GHOrganization myOrganization = userOrganizations.next();
					PagedIterable<GHEventInfo> myEventList = myOrganization.listEvents();
				
					Log.d("Details", "after listEvents, size :"+myEventList.asList().size());	            	
				
					// Get Requests in this organization
					Iterator myEventsIter = myEventList.iterator();
					while(myEventsIter.hasNext()){	 	            			
						GHEventInfo myEvent = (GHEventInfo)myEventsIter.next();	        			
						GHEvent theEventType = myEvent.getType();
						GHUser  theActor     = myEvent.getActor();
						Log.d("Details", "after gettint event type : "+theEventType.name()+" actor: "+theActor.getName());	            	
				
						finalText.append("\n"+theEventType.name()+" by "+theActor.getName());
				
					}
				}
            	
			}    		             		
			catch (Exception e) {
    			// Oops, something bad happened
    			Log.d("error", "GithubProjects, Exception during project loading : + " + e.toString());
    			// Write this in the UI Text instead of the repositories lists
    			return "Unexpected exception...";
			} 
			Log.d("Details", "leaving DetailedCommitLoadTask.doInBackground..:"+finalText.toString() );	            	
	        return finalText.toString();
        }
        
        @Override
        protected void onPostExecute(String result) {
        	// Write the result to the UI.
        	textViewProjects.setText(result);
        }

    }
	
	public void doSelectProject(View v) throws Exception {
		Log.d("Tag", "Click on Project Select button");
		
		// Retrieve the EditText in the UI
		EditText projectID_ed = (EditText)findViewById(R.id.selectedProject);
		// Get the project ID value
		String projectID = projectID_ed.getText().toString();
		
		// Retrieve the project corresponding to this ID
		GHRepository repo = this.repoList.get(projectID);
		
		// Check if this given ID corresponds to a listed project
		if(repo == null) {
			// Doesn't exist error
			// TODO display an error message ???
			return;
		}		
		
	}
	

	public void home(View caller)
	{
		finish();
	}
	
}