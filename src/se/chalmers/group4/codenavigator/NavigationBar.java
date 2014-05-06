package se.chalmers.group4.codenavigator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class NavigationBar {
	
	public static void load_navbar(final Activity act, int selectedButtonNumber) {
		act.setContentView(R.layout.navbar_layout);
		RadioGroup radioGroup = (RadioGroup) act.findViewById(R.id.navbar_radiogroup);
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
				Intent intentHome = new Intent(act, ProjectStoriesActivity.class);
	    		act.startActivity(intentHome);
			}
		});
		
		rbCommits.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentHome = new Intent(act, ProjectCommitActivity.class);
	    		act.startActivity(intentHome);
			}
		});
		
		/*
	    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {
	        	
	            // checkedId is the RadioButton selected
	        	switch (checkedId) {
				case R.id.btnHome:
					Intent intentHome = new Intent(act, ProjectListActivity.class);
		    		act.startActivity(intentHome);
					break;
				case R.id.btnBranches:
					showErrorMessage("Branches");
					break;
				case R.id.btnStories:
					showErrorMessage("Stories");
					break;
				case R.id.btnCommits:
					Intent intentCommits = new Intent(act, ProjectCommitActivity.class);
		    		act.startActivity(intentCommits);
					break;

				default:
					break;
				}
	        	
	        	
	        	
	        }
	        private void showErrorMessage(String message) {
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
	    });
	    */
		
	}
	
	public static void insert_main_layout(Activity act, int mLayout) {
		//RelativeLayout root = (RelativeLayout) act.findViewById(R.id.navbar_layout_root);
		
		ViewStub stub = (ViewStub) act.findViewById(R.id.layout_stub);
		stub.setLayoutResource(mLayout);
		View inflated = stub.inflate();
		
		//View layout = (View) act.getLayoutInflater().inflate(mLayout, null);
		//root.addView(layout);
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
