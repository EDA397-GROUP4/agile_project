package se.chalmers.group4.codenavigator;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterator;




import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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

public class ProjectCommitActivity extends Activity {


	private GHRepository githubRepository;
	private String latestCom;	
	private CommitAdapter commitAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NavigationBar.load_navbar(this,4);
		NavigationBar.insert_main_layout(this, R.layout.activity_project_commit);
		ActionBar bar = getActionBar();
		bar.setTitle("Commits");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

		
		// Get the selected repository from the app class
		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
		this.githubRepository = app.getGithubRepository();

		// Construct the data source
	    ArrayList<CommitListItem> commitListArray = new ArrayList<CommitListItem>();
	    // Create the adapter to convert the array to views
	    this.commitAdapter = new CommitAdapter(this, commitListArray);
	    
	    // Attach the adapter to a ListView
	    ListView listView = (ListView) findViewById(R.id.listview_commit);
	    listView.setAdapter(this.commitAdapter);
		
	    // Create the click event on the ListView
	    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final CommitListItem item = (CommitListItem) parent.getItemAtPosition(position);
				Log.d("Click",item.getSHA1());
				selectCommit(item.getSHA1());
			}	
	    });
		

		// Create and launch the CommitsLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new CommitsLoadTask().execute();

		// Scheduling recurrent task for once per 1 minute
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new RecurrentTask(), 0, 1,TimeUnit.MINUTES);
	}
	
	
	
    private void showInformation(String message, boolean loading) {
    	TextView message_view = (TextView)findViewById(R.id.TextViewCommitsMessage);
    	ProgressBar loading_view = (ProgressBar)findViewById(R.id.ProgressBarCommit);
    	LinearLayout layout_view = (LinearLayout)findViewById(R.id.LayoutCommitInfo);
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
    	LinearLayout layout_view = (LinearLayout)findViewById(R.id.LayoutCommitInfo);
    	layout_view.setVisibility(View.GONE);
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
	private class CommitsLoadTask extends AsyncTask<Void, Void, ArrayList<CommitListItem>> {
		@Override
		protected ArrayList<CommitListItem> doInBackground(Void... v) {
			ArrayList<CommitListItem> commitListArray = new ArrayList<CommitListItem>();

			try {
				
				// Set latestCom to the newest commit when program is started
				latestCom = githubRepository.listCommits().asList().get(0).getSHA1();
				
				// Save the current time as the latest update time of the commit view
				CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
				app.setUpdateTime(System.currentTimeMillis());
				Log.d("TIME NOW", "" + app.getUpdateTime());
				
				// Repositories Commit List iterator
				PagedIterator<GHCommit> repoCommits = githubRepository.listCommits().iterator();
				
				// Loop !
				for (PagedIterator<GHCommit> commits =  repoCommits; commits.hasNext();){
    				// Get the next commit 
    				GHCommit thisCommit = commits.next();
    			
    				// Get information
    				String title = thisCommit.getCommitShortInfo().getMessage();
    				String date = DateFormat.getDateTimeInstance().format(thisCommit.getCommitShortInfo().getCommitter().getDate());
    				String sha1 = thisCommit.getSHA1();
    				
    				// Add to the commit list
    				commitListArray.add(new CommitListItem(title, date, sha1));
            	}

				// Return the commit list Array
				return commitListArray;

			} catch (Exception e) {
				Log.d("GithubProject", "Exception during project loading : + " + e.toString());
				
				// Exception
				ArrayList<CommitListItem> errorArray = new ArrayList<CommitListItem>();
				errorArray.add(new CommitListItem("Unexpected exception...", null, null).setError());
				return errorArray;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<CommitListItem> result) {
			// Clear the previous results in the Listview  
			commitAdapter.clear();
			
			// Check if the first element is Error type
			if(result.get(0).isError()) {
				// Write the error in the information box and don't display results
        		showInformation(result.get(0).getTitle(), false);
			}
			else {
				// Hide the infomation box
        		hideInformation();
        		// Display all the results
				commitAdapter.addAll(result);
			}

		}
	}

	/**
	 * Private inner class implementing Runnable used for recurrently checking
	 * for new commits. This is made with a loop on the commit list, comparing
	 * each commit's creation date to the latest update date of the commit view
	 */
	private class RecurrentTask implements Runnable {
		int nrNewCommits = 0;
		//int i = 0;

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

	
	private void selectCommit(String sha1) {
		final ListView listview = (ListView)findViewById(R.id.listview_commit);
    	listview.setVisibility(View.GONE);
    	showInformation("Loading selected commit...", true);
    	
		Intent intent = new Intent(this, CommitDetailActivity.class);
		intent.putExtra("COMMIT_ID", sha1);
		startActivity(intent);
		
		new Handler().postDelayed(new Runnable() {
            public void run() {
            	hideInformation();
            	listview.setVisibility(View.VISIBLE);
            }
        }, 4000);
	}
	
	private class CommitListItem {

		private String title;
		private String date;
		private String sha1;
		private boolean error;

		public CommitListItem(String title, String date, String sha1) {
			this.title = title;
			this.date = date;
			this.sha1 = sha1;
			this.error = false;
		}

		public String getTitle() {
			return this.title;
		}
		public String getDate() {
			return this.date;
		}
		public String getSHA1() {
			return this.sha1;
		}
		public CommitListItem setError() {
			this.error = true;
			return this;
		}
		public boolean isError() {
			return this.error;
		}

	}
	
	private class CommitAdapter extends ArrayAdapter<CommitListItem> {
		private final Context context;
		public CommitAdapter(Context context, ArrayList<CommitListItem> users) {
			super(context, android.R.layout.simple_list_item_1, users);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Get the data item for this position
			CommitListItem item = getItem(position);    

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_commit, parent, false);

			// Lookup view for data population
			TextView titleview = (TextView) rowView.findViewById(R.id.CommitTitle);
			TextView dateview = (TextView) rowView.findViewById(R.id.CommitDate);
			TextView numberview = (TextView) rowView.findViewById(R.id.CommitNumber);

			// Populate the data into the template view using the data object
			titleview.setText(item.getTitle());
			dateview.setText(item.getDate());
			numberview.setText(Integer.toString(getCount()-position));

			// Return the completed view to render on screen
			return rowView;
		}
	}

}

