package se.chalmers.group4.codenavigator;



import java.util.HashMap;
import java.util.Iterator;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProjectListActivity extends Activity {
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
		new ProjectsLoadTask().execute();
		
	}

	/**
	 * Event triggered by the select project button.
	 * Retrieves the project ID field,
	 * and starts a ProjectCommitActivity if the project exists
	 */
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
		
		// Save this new GitHub Repository in the Application class 
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		app.setGithubRepository(repo);
		
		// Start the ProjectCommit Activity
		Intent intent = new Intent(this, ProjectCommitActivity.class);
		startActivity(intent);
	}


	
    /**
	 * Private inner class inherited from AsyncTask
	 * used for loading the project list in a new thread.
	 * 
	 * This will load the personal user repositories and also the repositories of
	 * all the organizations which the user belongs to.
	 * 
	 * This is made by three loops (2+1):
	 * 1)    Loop on the user repositories
	 * 2) a- Loop on the user organizations
	 *    b- Loop on the organizations repositories
	 */
    private class ProjectsLoadTask extends AsyncTask<Void, Void, String> {
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
            	
            	// User repositories iterator
            	PagedIterator<GHRepository> userRepositories = myself.listAllRepositories().iterator();
            	// User organizations iterator
            	Iterator<GHOrganization> userOrganizations = myself.getAllOrganizations().iterator();
            	
            	/* 
            	 * #1 - Loop on user repositories
            	 */
            	finalText.append("# User Repositories\n");
            	
            	for (PagedIterator<GHRepository> repositories = userRepositories; repositories.hasNext();){
        			// Get the next repository
            		GHRepository thisRepo =  repositories.next();
        			
            		// Add it to the UI text
        			finalText.append(Integer.toString(i) +"- "+ thisRepo.getName() + "\n");
        			// Add it to the repoList
        			repoList.put(Integer.toString(i), thisRepo);
        			i++;
        		}
            	
            	
            	/* 
            	 * #2a - Loop on user organizations
            	 */
            	
            	finalText.append("\n# Organizations Repositories");
            	for (Iterator<GHOrganization> organizations = userOrganizations; organizations.hasNext();){
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

            	// Return the repositories list flat text to be written in the UI
                return  finalText.toString();
    		}
    		catch (Exception e) {
    			// Oops, something bad happened
    			Log.d("error", "GithubProjects, Exception during project loading : + " + e.toString());
    			// Write this in the UI Text instead of the repositories lists
    			return "Unexpected exception...";
    		} 
        }
        
        @Override
        protected void onPostExecute(String result) {
        	// Write the result to the UI.
        	textViewProjects.setText(result);
       }
    }
    


}