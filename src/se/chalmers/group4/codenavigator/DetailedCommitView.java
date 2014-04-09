package se.chalmers.group4.codenavigator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestCommitDetail;
import org.kohsuke.github.GHPullRequestCommitDetail.Commit;
import org.kohsuke.github.GHRepository;
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
	            int i = 1; // Repository ID counter
	            StringBuilder finalText = new StringBuilder(); // Project list flat text for the UI
	            
	            try {
	            	/* 
	            	 * Github Myself Object : allows to access to user personal repositories
	            	 * and user organizations 
	            	 */
	            	GHMyself myself = githubObject.getMyself();
	            	
	            	// User Commits iterator
	       //Iterator<GHPullRequestCommitDetail>

	            	
	           ///<GHRepository> userRepositories = myself.listAllRepositories().iterator();

	           	
	            	// User organizations iterator
	 	            	Iterator<GHOrganization> userOrganizations = myself.getAllOrganizations().iterator();
	 	            	while(userOrganizations.hasNext()){
	 	            		GHOrganization myOrganization = userOrganizations.next();
	 	            		List<GHPullRequest> myRequest = myOrganization.getPullRequests();
	 	            		Iterator<GHPullRequest> myPullRequest = myRequest.iterator();
	 	            		while(myPullRequest.hasNext()){
	 	            			
	 	            			GHPullRequest myPullReq = myPullRequest.next();
	 	            			
	 	            			PagedIterable<GHPullRequestCommitDetail> myCommitDetail = myPullReq.listCommits();
	 	            			Iterator<GHPullRequestCommitDetail> myPullR = myCommitDetail.iterator();
	 	            			while(myPullR.hasNext()){
	 	            				GHPullRequestCommitDetail myPullCommitDetails = myPullR.next();
	 	            				Commit mycommit = myPullCommitDetails.getCommit();
	 	            				GitUser myGitUser = mycommit.getCommitter();
	 	            				String mystring = myGitUser.getName();
	 	            				System.out.println (" ***user: "+mystring);
	 	            				
	 	            			 	            				
	 	            				
	 	            				
	 	            			}
	 	            			
	 	            		}
	 	            		
	 	            		
	 	            	}
	            		            			
	            	            	
	            	
	            	
	            	
	            	/* 
	            	 * #1a - Loop on user organizations
	            	 */
	            	
	            	finalText.append("\n# Organizations Repositories");
	            	for (Iterator<GHOrganization> organizations = userOrganizations; organizations.hasNext();) {
	            	
	            		
	            		// Get the next organization
	            		GHOrganization thisOrga = organizations.next();
	            		
	            		// Write it to the UI text
	            		finalText.append("\n- "+ thisOrga.getLogin() +" -\n");
	            		
	            		// This organization repositories iterator
	            		PagedIterator<GHRepository> thisOrgaRepositories = thisOrga.listRepositories().iterator();
	            		
	            		/* 
	                	 * #2b - Loop on organization repositories
	                	 */
	            		for (PagedIterator<GHRepository> orgaRepositories = thisOrgaRepositories; orgaRepositories.hasNext();) {
	            			// Get the next repository
	            			GHRepository thisOrgaRepo =  orgaRepositories.next();
	            			
	            			// Add it to the UI text
	            			finalText.append(Integer.toString(i) +"- "+ thisOrgaRepo.getName() + "\n");
	            			
	            			// Add it to the repoList
	            			repoList.put(Integer.toString(i), thisOrgaRepo);
	            			i++;
	            		}
	            	
	            	}
	            
	            	          	
	            	           		
	            		
	            	
	            		            	
	            	    
	            }    	
	             		
    		catch (Exception e) {
    			// Oops, something bad happened
    			Log.d("error", "GithubProjects, Exception during project loading : + " + e.toString());
    			// Write this in the UI Text instead of the repositories lists
    			return "Unexpected exception...";
    		} 
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
	/* @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
	} */
	

	public void home(View caller)
{
	finish();
	}
}
	            

