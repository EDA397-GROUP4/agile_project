//package se.chalmers.group4.codenavigator;
//
//import java.util.HashMap;
//
//import org.kohsuke.github.GHCommit;
//import org.kohsuke.github.GHRepository;
//import org.kohsuke.github.PagedIterator;
//
//
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.app.Activity;
//import android.content.Intent;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//public class ProjectCommitActivity extends Activity {
//
//	private TextView textViewCommits;
//	private HashMap<String, String> commitList;
//	private GHRepository githubRepository;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_project_commit);
//		
//		// Commit List TextView (ui) 
//		this.textViewCommits = (TextView)findViewById(R.id.TextViewCommits);
//		
//		// Initialize an HashMap "ID"/Commit, to be fill up by the ProjectsLoadTask
//		this.commitList = new HashMap<String, String>();
//		
//		// Get the selected repository from the app class
//		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
//		this.githubRepository = app.getGithubRepository();
//		
//		// Create and launch the CommitsLoad AsyncTask
//		// (network activities cannot be done in the main thread)
//		new CommitsLoadTask().execute();
//	}
//
//	public void doSelectCommit(View v) throws Exception {
//		
//		// Retrieve the EditText in the UI
//				EditText commitID_ed = (EditText)findViewById(R.id.selectedCommit);
//				// Get the project ID value
//				String projectID = commitID_ed.getText().toString();
//				
//				// Retrieve the project corresponding to this ID
//				String sha1 = this.commitList.get(projectID);
//				
//				// Check if this given ID corresponds to a listed project
//				if(sha1 == null) {
//					// Doesn't exist error
//					// TODO display an error message ???
//					return;
//				}
//				
//				// Start the ProjectCommit Activity
//				
//				Intent intent = new Intent(this, CommitDetailActivity.class);
//				intent.putExtra("COMMIT_ID", sha1);
//				startActivity(intent);
//	}
//	
//	/**
//	 * Private inner class inherited from AsyncTask
//	 * used for loading the commit list in a new thread.
//	 * 
//	 * This is made with a loop on the commit list 
//	 * (retrieved from the global Github Repository Object stored in the app class)
//	 */
//    private class CommitsLoadTask extends AsyncTask<Void, Void, String> {
//        @Override
//        protected String doInBackground(Void... v) {
//            StringBuilder finalText = new StringBuilder();
//            int i = 1;
//            try {
//            	
//            	// Repositories Commit List iterator
//            	PagedIterator<GHCommit> repoCommits = githubRepository.listCommits().iterator();
//    			
//            	for (PagedIterator<GHCommit> commits =  repoCommits; commits.hasNext();){
//    				// Get the next commit 
//    				GHCommit thisCommit = commits.next();
//    			
//    				commitList.put(Integer.toString(i), thisCommit.getSHA1());
//    				
//    				// Add it to the UI text
//    				String[] lines = thisCommit.getCommitShortInfo().getMessage().split("\n");
//    				finalText.append(Integer.toString(i) + " => "+lines[0]+'\n');
//    				i++;
//            	}
//            	
//            	// Return the commit list flat text to be written in the UI
//            	return  finalText.toString();
//
//    		}
//    		catch (Exception e) {
//    			Log.d("GithubProject", "Exception during project loading : + " + e.toString());
//    			// Exception
//    			return "Unexpected exception...";
//    			
//    		} 
//            
//        }
//        
//        @Override
//        protected void onPostExecute(String result) {
//        	// Write the result to the UI.
//        	textViewCommits.setText(result);
//        	
//       }
//    }
//
//}
//=======

package se.chalmers.group4.codenavigator;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitStatus;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterator;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProjectCommitActivity extends Activity {

	private TextView textViewCommits;
	private GHRepository githubRepository;
	private HashMap<String, String> commitList;
	private String latestCom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navbar_layout);

		// Commit List TextView (ui)
		this.textViewCommits = (TextView) findViewById(R.id.TextViewCommits);

		//Initialize an HashMap "ID"/Commit, to be fill up by the ProjectsLoadTask
		this.commitList = new HashMap<String, String>();
		
		// Get the selected repository from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
		this.githubRepository = app.getGithubRepository();

		// Create and launch the CommitsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new CommitsLoadTask().execute();

		// Scheduling recurrent task for once per 1 minute
		ScheduledExecutorService scheduler = Executors
				.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new RecurrentTask(), 0, 1,
				TimeUnit.MINUTES);
	}

	/**
	 * Will send user a notification. Needs some work, but is functional
	 */

	void notification() {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("My notification")
				.setContentText("New commit!");
		// Creates an explicit intent for an Activity in your app
		// Intent resultIntent = new Intent(this, ResultActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(this);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(getIntent());
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());

	}

	/**
	 * Private inner class inherited from AsyncTask used for loading the commit
	 * list in a new thread.
	 * 
	 * This is made with a loop on the commit list (retrieved from the global
	 * Github Repository Object stored in the app class)
	 */
	private class CommitsLoadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... v) {
			StringBuilder finalText = new StringBuilder();

			try {
				// Set latestCom to the newest commit when program is started
				latestCom = githubRepository.listCommits().asList().get(0)
						.getSHA1();
				// Repositories Commit List iterator
				PagedIterator<GHCommit> repoCommits = githubRepository
						.listCommits().iterator();

				// Save the current time as the latest update time of the commit
				// view
				CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
				app.setUpdateTime(System.currentTimeMillis());
				Log.d("TIME NOW", "" + app.getUpdateTime());
				
				int i = 1;
				for (PagedIterator<GHCommit> commits =  repoCommits; commits.hasNext();){
    				// Get the next commit 
    				GHCommit thisCommit = commits.next();
    			
    				commitList.put(Integer.toString(i), thisCommit.getSHA1());
    				
    				// Add it to the UI text
    				String[] lines = thisCommit.getCommitShortInfo().getMessage().split("\n");
    				finalText.append(Integer.toString(i) + " => "+lines[0]+'\n');
    				i++;
            	}

				// Return the commit list flat text to be written in the UI
				return finalText.toString();

			} catch (Exception e) {
				Log.d("GithubProject", "Exception during project loading : + "
						+ e.toString());
				// Exception
				return "Unexpected exception...";

			}

		}

		@Override
		protected void onPostExecute(String result) {
			// Write the result to the UI.
			textViewCommits.setText(result);

		}
	}

	/**
	 * Private inner class implementing Runnable used for recurrently checking
	 * for new commits. This is made with a loop on the commit list, comparing
	 * each commit's creation date to the latest update date of the commit view
	 */
	private class RecurrentTask implements Runnable {
		int nrNewCommits = 0;
		int i = 0;

		@Override
		public void run() {
			try {

				Log.d("LAST TIME",
						githubRepository.listCommits().asList().get(0)
								.getCommitter().getName());

				Log.d("LAST TIME",
						githubRepository.listCommits().asList().get(0)
								.getSHA1());
				// Repositories Commit List iterator
				// PagedIterator<GHCommit> repoCommits =
				// githubRepository.listCommits().iterator();
				// Log.d("GET REPOS", repoCommits.toString());

				// Get the time of the latest update
				CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
				Long updateTime = app.getUpdateTime();
				Log.d("LAST TIME", "" + updateTime);

				// A lot is commented away. Wasnt able to make it work with that
				// code, but the current does the trick.
				// It sure aint pretty, but hey.
				// For now; Only master-branch
				// TODO: Cover all branches(?)

				// for (PagedIterator<GHCommit> commits = repoCommits;
				// commits.hasNext();){
				// PagedIterator<GHCommit> commits =
				// githubRepository.listCommits().iterator();
				/*
				 * while(commits.hasNext() ) { // Get the next commit GHCommit
				 * thisCommit = commits.next(); Log.d("TEST4",
				 * thisCommit.getCommitter().getName()); Log.d("TEST4",
				 * thisCommit.getSHA1()); //Current commit if(i == 0) {
				 * latestCommit = thisCommit.getSHA1(); i++; }
				 */

				/*
				 * List<GHCommitStatus> list =
				 * thisCommit.listStatuses().asList(); Log.d("COMM STATUS LIST",
				 * "" + list.size()); if(thisCommit.getLastStatus() == null) {
				 * Log.d("COMM STATUS", "NO STATUS"); continue; }
				 */
				// Log.d("COMM STATUS", thisCommit);
				// Count the new commits
				Log.d("latestcom", latestCom);
				Log.d("latest2", githubRepository.listCommits().asList().get(0)
						.getSHA1());
				// if newest commit doesnt have the same sha1, something new has
				// been pushed

				if (!(latestCom.equals(githubRepository.listCommits().asList()
						.get(0).getSHA1()))) {
					nrNewCommits = +1;
					Log.d("NEW COMMITS", "" + nrNewCommits);
					// set the new sha1 to current commit
					latestCom = githubRepository.listCommits().asList().get(0)
							.getSHA1();
				} else {
					Log.d("NEW COMMITS", "No new commit");
					// nrNewCommits++;
				}

				// }
				Log.d("NEW COMMITS", "" + nrNewCommits);
				// Updating the UI with a message showing the number of new
				// commits, if any
				if (nrNewCommits > 0) {
					notification();
					final int nr = nrNewCommits;
					runOnUiThread(new Runnable() {
						public void run() {
							TextView update = (TextView) findViewById(R.id.textViewUpdates);
							update.setText(getText(R.string.commits_update)
									+ " " + nr);
							update.setVisibility(View.VISIBLE);
						}
					});
					// Reset. Otherwise this will run every time
					nrNewCommits = 0;
				}
			} catch (Exception e) {
				Log.d("GithubNotificationUpdate",
						"Exception during commit notification loading : + "
								+ e.toString());
			}

		}

	}
	
	public void doSelectCommit(View v) throws Exception {
	
	// Retrieve the EditText in the UI
			EditText commitID_ed = (EditText)findViewById(R.id.selectedCommit);
			// Get the project ID value
			String projectID = commitID_ed.getText().toString();
			
			// Retrieve the project corresponding to this ID
			String sha1 = this.commitList.get(projectID);
			
			// Check if this given ID corresponds to a listed project
			if(sha1 == null) {
				// Doesn't exist error
				// TODO display an error message ???
				return;
			}
			
			// Start the ProjectCommit Activity
			
			Intent intent = new Intent(this, CommitDetailActivity.class);
			intent.putExtra("COMMIT_ID", sha1);
			startActivity(intent);
}

}

