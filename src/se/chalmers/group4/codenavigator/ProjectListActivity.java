package se.chalmers.group4.codenavigator;



import java.util.Iterator;
import java.util.Map;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;



import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ProjectListActivity extends Activity {
	private TextView textViewProjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_list);
		
		// Show the Up button in the action bar.
		//setupActionBar();
		
		textViewProjects = (TextView)findViewById(R.id.TextViewProjects);
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		String user = app.getGithubUser();
		String pass = app.getGithubPassword();
		new ProjectsLoadTask().execute(user,pass);
		
		/*
		try {
			CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
			GitHub github = app.getGithub();
			GHMyself myself = github.getMyself();
			// Create the text view
		    TextView textView = new TextView(this);
		    textView.setTextSize(40);
		    //textView.setText(myself.getName());
		    textView.setText(myself.getName());

		    // Set the text view as the activity layout
		    setContentView(textView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		*/
		
		/*
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		GitHub github = app.getGithub();
		
		GHMyself myself;
		try {
			myself = github.getMyself();
			// Create the text view
		    TextView textView = new TextView(this);
		    textView.setTextSize(40);
		    //textView.setText(myself.getName());
		    textView.setText("Test");

		    // Set the text view as the activity layout
		    setContentView(textView);
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
    private class ProjectsLoadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... credentials) {
        	String user = credentials[0];
            String pass = credentials[1];
            StringBuilder builder = new StringBuilder();
            
            try {
            	GitHub github = GitHub.connectUsingPassword(user, pass);
            	GHMyself myself = github.getMyself();
            	Map<String, GHRepository> RepMap = myself.getAllRepositories();
            	int i =1;
            	
            	builder.append("# User Projects\n");
            	for (Map.Entry<String, GHRepository> entry : RepMap.entrySet())
            	{
            		builder.append(Integer.toString(i) +"- "+ entry.getKey() + "\n");
            	    i++;
            	}
            	builder.append("\n# Organisations projects");
            	
            	GHPersonSet<GHOrganization> orga = myself.getAllOrganizations();
            	for (Iterator<GHOrganization> orgaIter = orga.iterator(); orgaIter.hasNext();){
            		GHOrganization thisOrga = orgaIter.next();
            		builder.append("\n- "+ thisOrga.getLogin() +" -\n");
            		for (PagedIterator<GHRepository> thisOrgaIter = thisOrga.listRepositories(100).iterator(); thisOrgaIter.hasNext();){
            			GHRepository thisOrgaRep =  thisOrgaIter.next();
            			builder.append(Integer.toString(i) +"- "+ thisOrgaRep.getName() + "\n");
            			i++;
            		}
            	      
            	    }
    		}
    		catch (Exception e) {
    			Log.d("GithubProject", "Exception during project loading : + " + e.toString());
    			// Exception
    			return "Unexpected exception...";
    			
    		}
            
            return  builder.toString();
        }
        
        @Override
        protected void onPostExecute(String result) {
        	textViewProjects.setText(result);
       }
    }

}
