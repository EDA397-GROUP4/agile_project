package se.chalmers.group4.codenavigator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.kohsuke.github.GitHub;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		ActionBar bar = getActionBar();
		bar.setTitle("Login");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
	}
	
	

	/**
	 * Event triggered by the login button.
	 * Retrieves the user and the password fields,
	 * and runs a Connection AsyncTask.
	 */
	public void doLogin(View v) throws Exception {
		Log.d("Tag", "Click on Login button");
		
		// Retrieve the EditText in the UI
		EditText userET = (EditText)findViewById(R.id.editText1);
		EditText passET = (EditText)findViewById(R.id.editText2);
		
		// Get their values
		String user = userET.getText().toString();
		String pass = passET.getText().toString();
		
		// Create and launch the Connection AsyncTask
		// (network activities cannot be done in the main thread)
		new ConnectionTask(user,pass).execute();
	}
	


    /**
     * Start the ProjectList Activity
     * (assumes that the GitHub Object has been correctly set
     * with the Connection AsyncTask)
     */
    private void goToProjectList() {
    	Intent intent = new Intent(this, ProjectListActivity.class);
		startActivity(intent);
    }
	
    
    /**
     * Display a AlertDialog with the given message
     * @param message The message to display
     */
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
	
	
	/**
	 * Private inner class inherited from AsyncTask
	 * used for performing the connection in a new thread.
	 * 
	 * In case of success, the new GitHub Object in set in the Application class
	 * in order to share it with the following activities.
	 * 
	 * In case of failure, an AlertDialog is shown.
	 */
    private class ConnectionTask extends AsyncTask<Void, Void, Integer> {
    	public static final int LOGIN_OK = 0;
    	public static final int ERROR_CREDENTIALS = 1;
    	public static final int ERROR_EXCEPTION = 2;
    	private String user;
    	private String pass;
    	
    	// Constructor
    	public ConnectionTask(String user, String pass) {
    		super();
    		this.user = user;
    		this.pass = pass;
    	}
    	
    	/**
    	 * Definition of the Connection AsyncTask (to be run with execute())
    	 * @return Connection status (passed automatically to the postExcecute method)
    	 */
        @Override
        protected Integer doInBackground(Void... v) {
            try {
            	// Create GitHub Object with given user/password            	
    			GitHub github = GitHub.connectUsingPassword(this.user, this.pass);

    			// Check validity
    			if(github.isCredentialValid()) {
    				
    				// Save this new GitHub Object in the Application class 
    				CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
    				app.setGithubObject(github);
    				
    				return LOGIN_OK;
    			}
    			else {
    				// Wrong user or password
    				return ERROR_CREDENTIALS;
    			} 
    		}
    		catch (Exception e) {
    			// Something else went wrong...
    			Log.d("error", "GithubConnection, Exception during connection : + " + e.toString());
    			return ERROR_EXCEPTION;
    		}
        }
        
        /**
         * Take an action according to the result of the Connection AsyncTask
         */
        @Override
        protected void onPostExecute(Integer result) {
        	switch (result) {
        		case LOGIN_OK : 
        			goToProjectList();
        			break;
        			
        		case ERROR_CREDENTIALS : 
        			showErrorMessage("Wrong user or password");
        			break;
        			
        		case ERROR_EXCEPTION :
        			showErrorMessage("Unexpected error during connection");
        			break;
        	}
       }
    }
    

}
