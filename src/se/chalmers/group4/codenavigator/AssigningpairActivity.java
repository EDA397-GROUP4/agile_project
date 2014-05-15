package se.chalmers.group4.codenavigator;

import java.util.ArrayList;
import java.util.Set;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTeam;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AssigningpairActivity extends Activity {

	public static final String NAVIGATOR_LABEL  = "Navigator : ";
	public static final String PROGRAMMER_LABEL = "Programmer: ";
	public static final String CHOOSE_PROGRAMMER_HINT = "Choose programmer from the list";

	private GHRepository         githubRepository;
	private GitHub 		         githubObject;
	
	// selectable scrollable list
	private ArrayAdapter<String> adapter;
	private Spinner              memberListSpinner;
	private int	                 choosenProgrammerIndex;
//	private String[]             availableProgrammers;
	private TextView             loadingMemberList;
	private ArrayList<String>	 listOfAvailableProgrammers;
	private String               theNavigator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getActionBar();
		bar.setTitle("Assigning Pairs");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));	
		//setContentView(R.layout.activity_display_show_files);
		NavigationBar.load_navbar(this,2);
		NavigationBar.insert_main_layout(this, R.layout.activity_assigningpair);
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();   
		this.githubObject            = app.getGithubObject();
	    this.githubRepository        = app.getGithubRepository(); 
		
		// find the layout field for the retrieved member's names
	    loadingMemberList = (TextView)findViewById(R.id.loading_membersList);
		new MemberLoadTask().execute();
	}
	
	private void setUpCurrencyMemberListSpinner() {
	    	
	    	// Define spinner as a drop-down list
	    	adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listOfAvailableProgrammers);
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	    	
	    	// Find the all spinners and connect to adapter to it, and define listener
	    	memberListSpinner = (Spinner)findViewById(R.id.team_membersList);
	    	memberListSpinner.setAdapter(adapter);
	    	memberListSpinner.setOnItemSelectedListener(new MemberListListener());
	    	memberListSpinner.setVisibility(View.VISIBLE);	        
	    }
	
	
	 private class MemberLoadTask extends AsyncTask<Void, Void, String> {
	    @Override
	    protected String doInBackground(Void... v) {
	    	
	 		try {
	 			// Tell the user that it's loading...
	 			loadingMemberList.setText(R.string.members_loading);
	 			loadingMemberList.setVisibility(View.VISIBLE);
	
	 		   	// From the GitHubRepository we can get a list of all teams
	 			Set<GHTeam> allTheTeams = githubRepository.getTeams();
				Log.d("assigning","all teams: "+allTheTeams.size());
	
				// Which team do I belong to? the GHMyself represents the account that's logging into GitHub
				GHMyself myself = githubObject.getMyself();					
				if (myself == null ) Log.d("assigning","myself is NULL");
				else {
					Log.d("assigning"," myself is:"+myself.getName());
				}
				theNavigator = myself.getName();
				
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
//				availableProgrammers       = new String[theMemberList.size()+1]; //first empty!
				listOfAvailableProgrammers = new ArrayList<String>();
				listOfAvailableProgrammers.add(" ");
	        	for (GHUser aMember: theMemberList)
	            {
	            	Log.d("assigning","In loop...memblerlist nr: "+i);
	            	Log.d("assigning","githubrepository: "+aMember.getName());
	            	
	            	if (!theNavigator.equals(aMember.getName()) ) {    //don't show myself in the list
//		            	availableProgrammers[i] = aMember.getName();
		            	listOfAvailableProgrammers.add(aMember.getName());
	            		i++;
	            	}
	    		}
	
	        	return "";
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

	    	setUpCurrencyMemberListSpinner();    	
	    	
	    	Log.d("assigning","after setUpCurrentMemberList.....");
	    	
		}
	}  // end of inner class - MemberLoadTask

	 
	    private class MemberListListener implements OnItemSelectedListener {
	  	   
	    	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    		choosenProgrammerIndex = memberListSpinner.getSelectedItemPosition();
	    		TextView navigator     = (TextView)findViewById(R.id.navigator);
	    		navigator.setText(AssigningpairActivity.NAVIGATOR_LABEL+theNavigator);
	    		TextView programmer    = (TextView)findViewById(R.id.programmer);
//	    		programmer.setText(AssigningpairActivity.PROGRAMMER_LABEL+availableProgrammers[choosenProgrammerIndex]);
	    		programmer.setText(AssigningpairActivity.PROGRAMMER_LABEL+listOfAvailableProgrammers.get(choosenProgrammerIndex));
	    		loadingMemberList.setText(R.string.selectedPair);
	    	}
	    	
	    	public void onNothingSelected(AdapterView<?> parent) {
	    	}
	    }  // end of inner class - MemberListListener
	 
}
    	

