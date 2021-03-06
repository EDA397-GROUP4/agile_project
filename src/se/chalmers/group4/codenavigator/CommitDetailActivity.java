package se.chalmers.group4.codenavigator;

import java.util.List;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitStatus;
import org.kohsuke.github.GHRepository;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class CommitDetailActivity extends Activity {
    private String               commitId;
 	private GHRepository         githubRepository;
 	private String               detailedCommitInformation;
	private List <GHCommit.File> thefilelist;
 	private TextView             fileviewProjects;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_commit_detail);
		NavigationBar.load_navbar(this,1);
		NavigationBar.insert_main_layout(this, R.layout.activity_commit_detail);
		
		// Try to read the sent CommitId
		Log.d("tag","start of commitdetailedactivity innan h�mta commit_id");
		commitId = getIntent().getStringExtra("COMMIT_ID");
		Log.d("tag","recieved ID="+commitId);

				
		CodeNavigatorApplication app              = (CodeNavigatorApplication)getApplication();   
		this.githubRepository = app.getGithubRepository();    
		Log.d("tag","githubRepository: "+githubRepository)	;	

		// Create and launch the ProjectsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new DetailedCommitLoadTask().execute();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.commit_detail, menu);
		ActionBar bar = getActionBar();
		bar.setTitle("Commit Details");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.log_out:
			logOut();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void logOut() {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, LoginActivity.class);
		startActivity(i);
	}
	
	private class DetailedCommitLoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... v) {
			StringBuilder detailsText = new StringBuilder(); // Commit detail's information flat text for the UI
			StringBuilder finalText = new StringBuilder();
			detailsText.append("Detailed information:\n");	            
			try {
				
				GHCommit       theCommit     = githubRepository.getCommit(commitId);
				Log.d("tag","theCommit:"+theCommit);

				// TODO : Fix LastStatus				
	//			GHCommitStatus theLastStatus = theCommit.getLastStatus();
	//			detailsText.append("Description: "+theLastStatus.getDescription());
				detailsText.append("Committer: "+theCommit.getCommitter());
				detailsText.append(" / Author: "+theCommit.getAuthor() + "\n");
				detailedCommitInformation = detailsText.toString();
				
				// Retrieve file information
	            finalText = new StringBuilder(); // Comitted filelist flat text for the UI
		            
                thefilelist = theCommit.getFiles();

            	finalText.append("# Committed Files\n");
            	int i = 0;
            	for (GHCommit.File fileitem: thefilelist)
                {
                	Log.d("file",""+i+" : " +fileitem.getFileName());
                	i++;
                	finalText.append(Integer.toString(i) +"- "+ fileitem.getFileName() + "\n");          	
        		}
			}    		             		
			catch (Exception e) {
    			// Oops, something bad happened
    			Log.d("error", "GithubDetailedCommitList, Exception during detailedCommitInformation loading : + " + e.getMessage());
    			// Write this in the UI Text instead of the repositories lists
    			return "Unexpected exception...";
			} 
			Log.d("tag", "leaving DetailedCommitLoadTask.doInBackground..:"+detailsText.toString() );	            	
	        return finalText.toString();
        }
        
        @Override
        protected void onPostExecute(String result) {
        	// Write the result to the UI.
        	((TextView)findViewById(R.id.textViewCommitDetails)).setText(detailedCommitInformation);
        	fileviewProjects=(TextView)findViewById(R.id.textViewCommittedFileList);
        	fileviewProjects.setVisibility(View.VISIBLE);
        	TextView loadingfiles = (TextView) findViewById(R.id.textViewLoadingFiles);
        	loadingfiles.setText("");
        	fileviewProjects.setText(result);
        }

    }


	
	
	
}
