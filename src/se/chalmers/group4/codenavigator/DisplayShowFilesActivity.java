package se.chalmers.group4.codenavigator;

import java.io.IOException;
import java.util.List;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DisplayShowFilesActivity extends Activity {
	
	private GHRepository githubRepository;
 	private GHCommit thecommit = null;
 	private List <GHCommit.File> thefilelist;
 	private TextView fileviewProjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("file","startingoncreate="+"");
		ActionBar bar = getActionBar();
		bar.setTitle("Files");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		
		//setContentView(R.layout.activity_display_show_files);
		NavigationBar.load_navbar(this,1);
		NavigationBar.insert_main_layout(this, R.layout.activity_display_show_files);
		
		this.fileviewProjects=(TextView)findViewById(R.id.ViewFilesList);
				String commitId = getIntent().getStringExtra("COMMIT_ID");
		
		Log.d("file","recieved ID="+commitId);

		
		CodeNavigatorApplication app              = (CodeNavigatorApplication)getApplication();   
        this.githubRepository = app.getGithubRepository(); 
                Log.d("file","beforegetcommit");
		try {
			thecommit = githubRepository.getCommit(commitId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       thefilelist = thecommit.getFiles();
        Log.d("file","fileretrieved:"+thefilelist.size());
        for (GHCommit.File fileitem: thefilelist)
        {
        	Log.d("file","In loop...:");
        	Log.d("file","githubrepository: "+fileitem.getFileName());
        	
        }
		new FileLoadTask().execute();
	}
	

	 private class FileLoadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... v) {
            int i = 0; // Repository ID counter
            StringBuilder finalText = new StringBuilder(); // Project list flat text for the UI
            
            try {
            	
            	finalText.append("# Committed Files\n");
            	            	for (GHCommit.File fileitem: thefilelist)
                {
                	Log.d("file","In loop...:");
                	Log.d("file","githubrepository: "+fileitem.getFileName());
                	i++;
                	finalText.append(Integer.toString(i) +"- "+ fileitem.getFileName() + "\n");
            	
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
        	fileviewProjects.setVisibility(View.VISIBLE);
        	TextView loadingfiles = (TextView) findViewById(R.id.ViewFiles);
        	loadingfiles.setText("");
        	        	
            fileviewProjects.setText(result);
        	
        	}
        		
       	}

}
