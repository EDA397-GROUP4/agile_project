package se.chalmers.group4.codenavigator;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CommitDetailActivity extends Activity {
	private TextView     textViewCommitId;
	private TextView     textViewCommitDetails;
    private String       commitId;
 	private GHRepository githubRepository;
 	private GitHub 		 githubObject;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_commit_detail);
		NavigationBar.load_navbar(this,4);
		NavigationBar.insert_main_layout(this, R.layout.activity_commit_detail);
		
		// Try to read the sent CommitId
				commitId = getIntent().getStringExtra("COMMIT_ID");
				Log.d("tag","recieved ID="+commitId);

				// Show the commitId to retrieve details about		
				textViewCommitId = (TextView)findViewById(R.id.textCommitId);
				textViewCommitId.setText(commitId);


				
				CodeNavigatorApplication app              = (CodeNavigatorApplication)getApplication();   
		        this.githubRepository = app.getGithubRepository();    
				Log.d("tag","githubRepository: "+githubRepository)	;	
				this.githubObject           = app.getGithubObject();
		        
				// Create and launch the ProjectsLoad AsyncTask
				// (network activities cannot be done in the main thread)
				new DetailedCommitLoadTask().execute();
		
	}
		public void getfile(View v) throws Exception
	{
		Log.d("file","clicked");
			Intent intent = new Intent(this, DisplayShowFilesActivity.class);
		
		if (commitId==null)
		{
			commitId="hai";
			
		}
		intent.putExtra("COMMIT_ID", commitId);
		Log.d("file","startactivity:"+commitId);
		Log.d("file","ready");
		startActivity(intent);
	}
	
	private class DetailedCommitLoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... v) {
			StringBuilder detailsText = new StringBuilder(); // Commit detail's information flat text for the UI
			detailsText.append("Detailed information:\n");	            
			try {
				Log.d("tag","githubObject: "+githubObject)	;			

				GHMyself myself = githubObject.getMyself();
				Log.d("tag", "myself "+myself.getName());	            	
				
					GHCommit       theCommit     = githubRepository.getCommit(commitId);
					//////GHCommit theCommit = (GHCommit)allCommitsIter.next();
					Log.d("tag","theCommit:"+theCommit);
					
					// TODO : Fix LastStatus
					
					//GHCommitStatus theLastStatus = theCommit.getLastStatus();

					detailsText.append("Committer: "+theCommit.getCommitter());
					detailsText.append(" / Author: "+theCommit.getAuthor() + "\n");
					
			}    		             		
			catch (Exception e) {
    			// Oops, something bad happened
    			Log.d("error", "GithubDetailedCommitList, Exception during detailedCommitInformation loading : + " + e.getMessage());
    			// Write this in the UI Text instead of the repositories lists
    			return "Unexpected exception...";
			} 
			Log.d("tag", "leaving DetailedCommitLoadTask.doInBackground..:"+detailsText.toString() );	            	
	        return detailsText.toString();
        }
        
        @Override
        protected void onPostExecute(String result) {
        	// Write the result to the UI.
        	((TextView)findViewById(R.id.textViewCommitDetails)).setText(result);
        }

    }


	
	
	
}
