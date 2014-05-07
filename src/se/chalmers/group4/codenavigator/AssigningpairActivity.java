package se.chalmers.group4.codenavigator;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class AssigningpairActivity extends Activity {


private GHRepository githubRepository;
	private GitHub 		 githubObject;
	private Set<String> themembernamelist;
	private TextView memberlistProjects;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	Log.d("file","startingoncreate="+"");
	
	
	//setContentView(R.layout.activity_display_show_files);
	NavigationBar.load_navbar(this,3);
	NavigationBar.insert_main_layout(this, R.layout.activity_assigningpair);
	
	this.memberlistProjects=(TextView)findViewById(R.id.team_membersList);
			String commitId = getIntent().getStringExtra("COMMIT_ID");
	
	Log.d("file","recieved ID="+commitId);

	
	CodeNavigatorApplication app              = (CodeNavigatorApplication)getApplication();   
    this.githubRepository = app.getGithubRepository(); 
            Log.d("file","beforegetcommit");
	try {
		this.themembernamelist = githubRepository.getCollaboratorNames();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       Log.d("file","membersretrieved:"+themembernamelist.size());
    for (String memberitem: themembernamelist)
    {
    	Log.d("file","In loop...:");
    	Log.d("file","githubrepository: "+memberitem);
    	
    }
	new MemberLoadTask().execute();
}


 private class MemberLoadTask extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... v) {
        int i = 0; // Repository ID counter
        StringBuilder finalText = new StringBuilder(); // Project list flat text for the UI
        
        try {
        	
        	finalText.append("# Committed Files\n");
        	            	for (String memberitem: themembernamelist)
            {
            	Log.d("file","In loop...:");
            	Log.d("file","githubrepository: "+memberitem);
            	i++;
            	finalText.append(Integer.toString(i) +"- "+ memberitem + "\n");
        	

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
    	Log.d("file","onPostExecute:" +"result");
    	memberlistProjects.setVisibility(View.VISIBLE);
    	TextView loadingfiles = (TextView) findViewById(R.id.team_membersList);
    	loadingfiles.setText("");
    	        	
         	memberlistProjects.setText(result);
    	
    	}
 }
}
    	

