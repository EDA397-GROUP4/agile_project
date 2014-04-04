/**
 * 
 * Nasty coding but for now, it works. It will take the credentials and get the name registered on that account and print
 * the name into logcat
 * 
 */

package se.chalmers.group4.codenavigator;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitUser;
import org.kohsuke.github.PagedIterable;

public class LoginActivity extends Activity {
	
	private static GitHub gitHub;
	private String user;
	private String pass;
	final Handler myHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
	}

	public void doLogin(View v) throws Exception {
		Log.d("Tag", "Kommer hit");
		EditText username, password;
		username = (EditText)findViewById(R.id.editText1);
		password = (EditText)findViewById(R.id.editText2);
		this.user = username.getText().toString();
		this.pass = password.getText().toString();
		
		//GHUser gh = new GHUser();
		//GitHub github;
		//GitHub github = GitHub.connectUsingPassword(user, pass);
		//GitHub github = GitHub.connectUsingPassword(user, pass);
		
		//GHUser guser = github.getMyself();
		//GHMyself guser = github.getMyself();
		//Log.d("Tag", guser.getEmail());
		//this.gh = startGit(user, pass);
		startGit(this.user, this.pass);
		Log.d("tag", "Gjort startGit");
		//gitHub.get
		//GHMyself myself = LoginActivity.gitHub.getMyself();
		//Log.d("tag", myself.getEmail());
	}
	

	
	public void doGit(String user, String pass) {
		try {
			LoginActivity.gitHub = GitHub.connectUsingPassword(user, pass);
		} catch (IOException e) {
			Log.d("error", "Fel");
			e.printStackTrace();
		}
	}
	
	public void showError() {
		TextView error = (TextView)findViewById(R.id.textView3);
		error.setVisibility(View.VISIBLE);		
	}
	
	
	public void startGit(final String user, final String pass) {
		this.pass = pass;
		this.user = user;
		
		AsyncTask<Void, Void, GHMyself> task = (new AsyncTask<Void, Void, GHMyself>() {

			@Override
			protected GHMyself doInBackground(Void... params) {
				try {
		        	GitHub github = GitHub.connectUsingPassword(user, pass);
		        	GHMyself myself = github.getMyself();
		        	Log.d("tagga", myself.getName());
		        	List<GHRepository> repositories = myself.listAllRepositories().asList();
		        	for(GHRepository r:repositories){
		        		Log.d("repo", r.getName());
		        	}
		        	return myself;
		        	
		        } catch (Exception e) {
		            e.printStackTrace();
		            return null;
		        }
			}
			protected void onPostExecute(GHMyself result) {
		        if (result!=null){
		        	Intent intent = new Intent(LoginActivity.this, DisplayReposActivity.class);
					try {
						intent.putExtra("github_name", result.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivity(intent);
		        }
		        else
		        	showError();
		    }
		});
		task.execute();
						            
	}
	
}
