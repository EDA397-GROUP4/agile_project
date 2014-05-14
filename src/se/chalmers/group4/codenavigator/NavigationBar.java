package se.chalmers.group4.codenavigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.RadioButton;


public class NavigationBar {
	
	public static void load_navbar(final Activity act, int selectedButtonNumber) {
		act.setContentView(R.layout.navbar_layout);
		//RadioGroup radioGroup = (RadioGroup) act.findViewById(R.id.navbar_radiogroup);
		RadioButton rbHome = (RadioButton) act.findViewById(R.id.btnHome);
		RadioButton rbBranches = (RadioButton) act.findViewById(R.id.btnBranches);
		RadioButton rbStories = (RadioButton) act.findViewById(R.id.btnStories);
		RadioButton rbCommits = (RadioButton) act.findViewById(R.id.btnCommits);
		
		// Highlight the button corresponding to the current activities
		RadioButton rbToBeSelected;
		
		switch (selectedButtonNumber) {
		case 1:
			rbToBeSelected = rbHome;
			break;
		case 2:
			rbToBeSelected = rbBranches;
			break;
		case 3:
			rbToBeSelected = rbStories;
			break;
		case 4:
			rbToBeSelected = rbCommits;
			break;
		default:
			rbToBeSelected = null;
			break;
		}
		
		if (rbToBeSelected != null) {
			rbToBeSelected.setChecked(true);
		}
			
		// Set the click event on buttons
		rbHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				waiting_for_next_activity(act, "Loading Projects...");
				Intent intentHome = new Intent(act, ProjectListActivity.class);
	    		act.startActivity(intentHome);
			}
		});

		rbBranches.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showErrorMessage("Branches", act);
			}
		});
		
		rbStories.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				waiting_for_next_activity(act, "Loading Stories...");
				Intent intentHome = new Intent(act, ProjectStoriesActivity.class);
	    		act.startActivity(intentHome);
	    		
	    		
			}
		});
		
		rbCommits.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				waiting_for_next_activity(act, "Loading Commits...");
				Intent intentHome = new Intent(act, ProjectCommitActivity.class);
	    		act.startActivity(intentHome);
			}
		});
		
		
		
	}
	
	public static void insert_main_layout(Activity act, int mLayout) {
		ViewStub stub = (ViewStub) act.findViewById(R.id.layout_stub);
		stub.setLayoutResource(mLayout);
		stub.inflate();
	}
	
	public static void waiting_for_next_activity(Activity act, String text) {
		final ProgressDialog progressDialog = ProgressDialog.show(act, "Please wait ...", text, true, false);
		new Handler().postDelayed(new Runnable() {
            public void run() {
            	progressDialog.dismiss();
            }
        }, 4000);
		
		
	}
	
	 private static void showErrorMessage(String message, Activity act) {
         AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(act);

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

}
