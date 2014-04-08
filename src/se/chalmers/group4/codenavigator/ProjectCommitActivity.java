package se.chalmers.group4.codenavigator;

import java.util.Iterator;
import java.util.Map;

import org.kohsuke.github.GHCommitComment;
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
import android.widget.TextView;

public class ProjectCommitActivity extends Activity {

	private TextView textViewCommits;
	private GHRepository repo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_commit);
		textViewCommits = (TextView)findViewById(R.id.TextViewCommits);
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		this.repo = app.getGithubRepository();
		
		new CommitLoadTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project_commit, menu);
		return true;
	}
	
    private class CommitLoadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... v) {
            StringBuilder builder = new StringBuilder();
            
            try {
            	
            	
            	
            	for (PagedIterator<GHCommitComment> CcommentIter =  repo.listCommitComments().iterator(); CcommentIter.hasNext();){
            		GHCommitComment thisComment = CcommentIter.next();
            		builder.append(thisComment.getBody()+"\n\n");
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
        	textViewCommits.setText(result);
        	
       }
    }

}
