package se.chalmers.group4.codenavigator;

import java.util.Set;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AssigningpairActivity extends Activity {


	private GHRepository githubRepository;
	private GitHub 		 githubObject;
	private TextView     teamMemberList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		//setContentView(R.layout.activity_display_show_files);
		NavigationBar.load_navbar(this,4);
		NavigationBar.insert_main_layout(this, R.layout.activity_assigningpair);
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();   
		this.githubObject            = app.getGithubObject();
	    this.githubRepository        = app.getGithubRepository(); 
		
		// find the layout field for the retrieved member's names
		this.teamMemberList = (TextView)findViewById(R.id.team_membersList);	
	
		new MemberLoadTask().execute();
	}
	
	
	 private class MemberLoadTask extends AsyncTask<Void, Void, String> {
	    @Override
	    protected String doInBackground(Void... v) {
	    	
	 		try {
	
	 		   	// From the GitHubRepository we can get a list of all teams
	 			Set<GHTeam> allTheTeams = githubRepository.getTeams();
				Log.d("assigning","all teams: "+allTheTeams.size());
	
				// Which team do I belong to? the GHMyself represents the account that's logging into GitHub
				GHMyself myself = githubObject.getMyself();					
				if (myself == null ) Log.d("assigning","myself is NULL");
				else {
					Log.d("assigning"," myself is:"+myself.getName());
				}
							
				GHTeam   myTeam = null;
				// Find my team
				for ( GHTeam aTeam: allTheTeams ) {
					Log.d("assigning"," loop find team..."+aTeam.getName());
					if ( myself.isMemberOf(aTeam)) {
						// my team is found!
						myTeam = aTeam;  // takes the first found team....
						break;
					}
				}
	    	
				if (myTeam == null ) Log.d("assigning","myTeam is NULL");
				else Log.d("assigning","my team is found: "+myTeam.getName());
	    	
				// Retrieves the current members of this team
				Set<GHUser> theMemberList = myTeam.getMembers();
				Log.d("assigning","number of members: "+theMemberList.size());
				
				int           i         = 0; // Team member counter
				StringBuilder finalText = new StringBuilder(); // Project list flat text for the UI
	 
				finalText.append("# Navigator : " + myself.getName() + "\n");
				finalText.append("# Members in team : " + myTeam.getName() + "\n");
	        	for (GHUser aMember: theMemberList)
	            {
	            	Log.d("assigning","In loop...memblerlist nr: "+i);
	            	Log.d("assigning","githubrepository: "+aMember.getName());
	            	
	            	if (! myself.getName().equals(aMember.getName()) ) {    //don't show myself in the list
		            	i++;
		            	finalText.append(Integer.toString(i) +"- "+aMember.getName() +"\n");
	            	}
	    		}
	
	        	// Return the repositories list flat text to be written in the UI
	            return  finalText.toString();
			}
			
			catch (Exception e) {
				// Oops, something bad happened
				Log.d("error", "Member list, Exception during team member list loading : + " + e.getMessage());
				// Write this in the UI Text instead of the repositories lists
				return "Unexpected exception in AssigningpairActivity.....";
			} 
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	    	// Write the result to the UI.
	    	Log.d("assigning","onPostExecute:" +"result");
	    	teamMemberList.setVisibility(View.VISIBLE);
	    	TextView loadingmembers = (TextView) findViewById(R.id.loading_team_members);
	    	loadingmembers.setText("");
	    	        	
	    	teamMemberList.setText(result);
	    	
		}
	}  // end of inner class - MemberLoadTask
	 
}
    	

