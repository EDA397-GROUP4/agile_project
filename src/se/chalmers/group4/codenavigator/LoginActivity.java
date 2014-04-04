/**
 * 
 * Nasty coding but for now, it works. It will take the credentials and get the name registered on that account and print
 * the name into logcat
 * 
 */

package se.chalmers.group4.codenavigator;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitUser;



public class LoginActivity extends Activity {
	
	//private static GitHub gitHub;
	//private String user;
	//private String pass;
	
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
		String user = username.getText().toString();
		String pass = password.getText().toString();
		
		//GHUser gh = new GHUser();
		//GitHub github;
		//GitHub github = GitHub.connectUsingPassword(user, pass);
		//GitHub github = GitHub.connectUsingPassword(user, pass);
		
		//GHUser guser = github.getMyself();
		//GHMyself guser = github.getMyself();
		//Log.d("Tag", guser.getEmail());
		//this.gh = startGit(user, pass);
		startGit(user,pass);
		Log.d("tag", "Gjort startGit");
		//gitHub.get
		//GHMyself myself = LoginActivity.gitHub.getMyself();
		//Log.d("tag", myself.getEmail());
	}
	

	/*
	public void doGit(String user, String pass) {
		try {
			LoginActivity.gitHub = GitHub.connectUsingPassword(user, pass);
		} catch (IOException e) {
			Log.d("error", "Fel");
			e.printStackTrace();
		}
	}
	*/
	
	public void startGit(final String user, final String pass) {
		//this.pass = pass;
		//this.user = user;
		
		//String credentials[] = {user,pass};
		new ConnectionTask().execute(user,pass);
	}


	
	private void showErrorMessage(String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Error Message...");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

        dlgAlert.setPositiveButton("Ok",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

            }
        });
	}
	
    private class ConnectionTask extends AsyncTask<String, Void, boolean[]> {
        @Override
        protected boolean[] doInBackground(String... credentials) {
        	// 0 : Exception; 1: Wrong
            boolean result[] = {false,false};
            String user = credentials[0];
            String pass = credentials[1];
            try {
    			GitHub gh = GitHub.connectUsingPassword(user, pass);
    			if(gh.isCredentialValid()) {
    				CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
    				app.setGithubCredentials(user, pass);
    			}
    			else {
    				// Wrong user/password
    				result[1] = true;
    			} 
    		}
    		catch (Exception e) {
    			Log.d("GithubConnection", "Exception during connection : + " + e.toString());
    			// Exception
    			result[0] = true;
    			
    		}
            
            return result;
        }
        
        @Override
        protected void onPostExecute(boolean[] result) {
        	boolean isException = result[0];
        	boolean isWrong = result[1];
        	
        	if(isException) {
        		showErrorMessage("Unexpected error during connection");
        	}
        	else if (isWrong) {
        		showErrorMessage("Wrong user or password");
        	}
        	else {
        		goToProjectList();
        	}
       }
    }
    
    private void goToProjectList() {
    	Intent intent = new Intent(this, ProjectListActivity.class);
		startActivity(intent);
    }
	
}
