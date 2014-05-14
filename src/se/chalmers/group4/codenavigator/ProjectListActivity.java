package se.chalmers.group4.codenavigator;



import java.util.ArrayList;
import java.util.Iterator;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProjectListActivity extends Activity {
	private GitHub githubObject;
	private ProjectAdapter projectAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		ActionBar bar = getActionBar();
		bar.setTitle("Projects");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		

		// Get the global Github Object from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		this.githubObject = app.getGithubObject();
		
		// Create an empty ArrayList to store the ProjectListItems
		ArrayList<ProjectListItem> ProjectListArray = new ArrayList<ProjectListItem>();
		// Initialize a ProjectAdaptater to be fill up by the ProjectLoadTask
		this.projectAdapter = new ProjectAdapter(this, ProjectListArray);
		
		// Attach the adapter to a ListView
	    ListView listView = (ListView) findViewById(R.id.listview_project);
	    listView.setAdapter(this.projectAdapter);
		
	    // Create the click event on the ListView
	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final ProjectListItem item = (ProjectListItem) parent.getItemAtPosition(position);
				if(item.isClickable()) {
					Log.d("Click",item.getText());
					try {
						selectProject(item.getRepo());
					} catch (Exception e) {}
				}
			}	
	    });
		
				
		// Create and launch the ProjectsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new ProjectsLoadTask().execute();
	}


	/**
	 * Event triggered by selecting a project.
	 * Save this repository in the main App class
	 * and starts a ProjectCommitActivity
	 */
    private void selectProject(GHRepository repo) throws Exception {
    	ListView listview = (ListView)findViewById(R.id.listview_project);
    	listview.setVisibility(View.GONE);
    	showInformation("Loading selected project...", true);
    	
		// Save this new GitHub Repository in the Application class 
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		app.setGithubRepository(repo);
		
		// Start the ProjectCommit Activity
		Intent intent = new Intent(this, ProjectCommitActivity.class);
		startActivity(intent);
	}
    
    private void showInformation(String message, boolean loading) {
    	TextView message_view = (TextView)findViewById(R.id.TextViewProjectsMessage);
    	ProgressBar loading_view = (ProgressBar)findViewById(R.id.ProgressBarProject);
    	LinearLayout layout_view = (LinearLayout)findViewById(R.id.LayoutProjectInfo);
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
    	LinearLayout layout_view = (LinearLayout)findViewById(R.id.LayoutProjectInfo);
    	layout_view.setVisibility(View.GONE);
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
    private class ProjectsLoadTask extends AsyncTask<Void, Void, ArrayList<ProjectListItem>> {
        @Override
        protected ArrayList<ProjectListItem> doInBackground(Void... v) {
            ArrayList<ProjectListItem> ProjectListArray = new ArrayList<ProjectListItem>();
            
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
            	// Write "User Repositories" Title
            	ProjectListArray.add(new ProjectListItem("User Repositories", ProjectListItem.TITLE, null));
            	
            	for (PagedIterator<GHRepository> repositories = userRepositories; repositories.hasNext();){
        			// Get the next repository
            		GHRepository thisRepo =  repositories.next();
            		// Write the project name and link with the repository object
        			ProjectListArray.add(new ProjectListItem(thisRepo.getName(), ProjectListItem.PROJECT_NAME, thisRepo));
        		}
            	
            	
            	/* 
            	 * #2a - Loop on user organizations
            	 */
            	// Write "Organization Repositories" Title
            	ProjectListArray.add(new ProjectListItem("Organization Repositories", ProjectListItem.TITLE, null));
            	
            	for (Iterator<GHOrganization> organizations = userOrganizations; organizations.hasNext();){
            		// Get the next organization
            		GHOrganization thisOrga = organizations.next();
            		
            		// Write the name of the organization
            		ProjectListArray.add(new ProjectListItem(thisOrga.getLogin(), ProjectListItem.ORGA_NAME, null));
            		
            		// This organization repositories iterator
            		PagedIterator<GHRepository> thisOrgaRepositories = thisOrga.listRepositories().iterator();
            		
            		/* 
                	 * #2b - Loop on organization repositories
                	 */
            		for (PagedIterator<GHRepository> orgaRepositories = thisOrgaRepositories; orgaRepositories.hasNext();) {
            			// Get the next repository
            			GHRepository thisOrgaRepo =  orgaRepositories.next();
            			
            			// Write the project name and link with the repository object          			
            			ProjectListArray.add(new ProjectListItem(thisOrgaRepo.getName(), ProjectListItem.PROJECT_NAME, thisOrgaRepo));
            		}
            	      
            	}

            	// Return the repositories list Array
            	return ProjectListArray;
    		}
    		catch (Exception e) {
    			// Oops, something bad happened
    			Log.d("error", "GithubProjects, Exception during project loading : + " + e.toString());
    			
    			// Return an Array with an Error type item at the first position instead
    			ArrayList<ProjectListItem> ErrorArray = new ArrayList<ProjectListItem>();
    			ErrorArray.add(new ProjectListItem("Unexpected exception...", ProjectListItem.ERROR, null));
    			return ErrorArray;
    		} 
        }
        
        @Override
        protected void onPostExecute(ArrayList<ProjectListItem> result) {
        	// Clear the previous results in the Listview     	
        	projectAdapter.clear();
        	
        	// Check if the first element is Error type
        	if(result.get(0).isError()) {
        		// Write the error in the information box and don't display results
        		showInformation(result.get(0).getText(), false);
        	}
        	// If all is OK
        	else {
        		// Hide the infomation box
        		hideInformation();
        		// Display all the results
        		projectAdapter.addAll(result);
        	}

        }

    }
    
    
    
    /**
	 * Private inner class 
	 * which represent an item in the project list
	 * 
	 * Could be a Project Name, an Organization Name or a Title
	 * 
	 */
    private class ProjectListItem {
    	// Item Types
    	public static final int ERROR = 0;
    	public static final int TITLE = 1;
    	public static final int ORGA_NAME = 2;
    	public static final int PROJECT_NAME = 3;
    	

    	private String text;
    	private int itemType;
    	private GHRepository repo;

    	public ProjectListItem(String text, int itemType, GHRepository repo) {
    		this.text = text;
    		this.itemType = itemType;
    		this.repo = repo;
    	}

    	public String getText() {
    		return this.text;
    	}

    	/**
    	 * Return the layout corresponding to the type of item
    	 * @return internal ID of the layout
    	 */
    	public int getLayout() {
    		switch (this.itemType) {
    		case TITLE:
    			return R.layout.list_project_title;
    		case ORGA_NAME :
    			return R.layout.list_project_organame;
    		default:
    			return R.layout.list_project_projectname;
    		} 
    	}
    	
    	/**
    	 * Used to allow processing the click event,
    	 * otherwise a title or an orga name would raise an exception
    	 * because they are not associated with a GHRepository
    	 * @return true if it is a project; false otherwise
    	 */
    	public boolean isClickable() {
    		if(this.itemType == PROJECT_NAME) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}

    	public GHRepository getRepo() {
    		return this.repo;
    	}
    	
    	public boolean isError() {
    		if(this.itemType == ERROR) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    }
    
    
    
    /**
	 * Private inner class 
	 * used to make the interface between a list of ProjectListItem
	 * and the ListView UI element
	 */
    private class ProjectAdapter extends ArrayAdapter<ProjectListItem> {
    	private final Context context;
    	public ProjectAdapter(Context context, ArrayList<ProjectListItem> users) {
    		super(context, android.R.layout.simple_list_item_1, users);
    		this.context = context;
    	}

    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// Get the data item for this position
    		ProjectListItem item = getItem(position);    

    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		View rowView = inflater.inflate(item.getLayout(), parent, false);

    		// Lookup view for data population
    		TextView textview = (TextView) rowView.findViewById(R.id.list_project_item_text);

    		// Populate the data into the template view using the data object
    		textview.setText(item.getText());

    		// Return the completed view to render on screen
    		return rowView;
    	}
    }
    
    
    /*
    public void startCommit(View view)
	{
    
    	Intent intent = new Intent(this, ProjectCommitActivity.class);
    	
    	 EditText editText = (EditText) findViewById(R.id.selectedProject);
    	 GHRepository selectedRepo = repoList.get(editText.getText().toString());
    	 Log.d("REPO", selectedRepo.toString());
    	 CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication(); 
    	 app.setGithubRepository(selectedRepo);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    //	EditText editText = (EditText) findViewById(R.id.edit_message);
    	
    	// String message = editText.getText().toString();
    	    	
    	
    	
    }
    
    */

    


}

