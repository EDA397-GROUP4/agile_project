package se.chalmers.group4.codenavigator;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitStatus;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
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
		setContentView(R.layout.activity_commit_detail);
		
		// Try to read the sent CommitId
				commitId = getIntent().getStringExtra("COMMIT_ID");
				Log.d("tag","recieved ID="+commitId);

				// Show the commitId to retrieve details about		
				textViewCommitId = (TextView)findViewById(R.id.textCommitId);
				textViewCommitId.setText(commitId);


				// Initialize an HashMap "ID"/Repository, to be fill up by the ProjectsLoadTask
//				this.repoList = new HashMap<String, GHRepository>();

				// Get the global Github Object from the app class
				CodeNavigatorApplication app              = (CodeNavigatorApplication)getApplication();   
		        this.githubRepository = app.getGithubRepository();    
				Log.d("tag","githubRepository: "+githubRepository)	;	
				this.githubObject           = app.getGithubObject();
		        
				// Create and launch the ProjectsLoad AsyncTask
				// (network activities cannot be done in the main thread)
				new DetailedCommitLoadTask().execute();
		
	}
	
	
	
	private class DetailedCommitLoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... v) {
			StringBuilder detailsText = new StringBuilder(); // Commit detail's information flat text for the UI
			detailsText.append("Detailed information:\n");	            
			try {
				Log.d("tag","githubObject: "+githubObject)	;			
//----------
/*				GHRepository githubRepository = null;    
				//Log.d("tag","githubRepository: "+githubRepository)	;			
			   	try {
			   		githubRepository = githubObject.getRepository("EDA397-GROUP4/agile_project");
			   	} catch (IOException e) {
			   		// TODO Auto-generated catch block
			   		Log.d("XXX","exp getRepositoyr "+e.getMessage());
			   		e.printStackTrace();
			   	} //MM
			   		Log.d("XXX","after githubRepository: "+githubRepository)	;	//MM		
*/
				//			   		GitHub githubObject           = app.getGithubObject();//MM
//			   		Log.d("XXX","githubObject: "+githubObject)	;			//MM

//-------------				
				GHMyself myself = githubObject.getMyself();
				Log.d("tag", "myself "+myself.getName());	            	
				// Getting User Commits (.. now at first my own..)

/*				// Get User organizations
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
				}*/
		/*		PagedIterable<GHCommit> allCommits = githubRepository.listCommits();
				Log.d("tag","allCommits: "+allCommits);
				Iterator  allCommitsIter = allCommits.iterator();
				while (allCommitsIter.hasNext()) {*/
					GHCommit       theCommit     = githubRepository.getCommit(commitId);
					//////GHCommit theCommit = (GHCommit)allCommitsIter.next();
					Log.d("tag","theCommit:"+theCommit);
					
					// TODO : Fix LastStatus
					
					//GHCommitStatus theLastStatus = theCommit.getLastStatus();

					detailsText.append("Committer: "+theCommit.getCommitter());
					detailsText.append(" / Author: "+theCommit.getAuthor() + "\n");
					//detailsText.append("created : "+theLastStatus.getCreatedAt().toGMTString() + " by " + theLastStatus.getCreator()+ "\n");

					//detailsText.append("updated : "+theLastStatus.getUpdatedAt().toGMTString());
					//detailsText.append("state: "+ theLastStatus.getState().name());
		//		}
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
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.commit_detail, menu);
		return true;
	}
*/

	
	
	
}
