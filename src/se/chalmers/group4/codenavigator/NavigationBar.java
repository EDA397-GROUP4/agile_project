package se.chalmers.group4.codenavigator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.RadioButton;


public class NavigationBar {
	
	public static void load_navbar(final Activity act, int selectedButtonNumber) {
		act.setContentView(R.layout.navbar_layout);

		RadioButton rbCommits = (RadioButton) act.findViewById(R.id.btnCommits);
		RadioButton rbStories = (RadioButton) act.findViewById(R.id.btnStories);
		RadioButton rbFiles = (RadioButton) act.findViewById(R.id.btnFiles);
		RadioButton rbProjects = (RadioButton) act.findViewById(R.id.btnProjects);

		
		// Highlight the button corresponding to the current activities
		RadioButton rbToBeSelected;
		
		switch (selectedButtonNumber) {
		case 1:

			rbToBeSelected = rbCommits;
			break;
		case 2:
			rbToBeSelected = rbStories;
			break;
		case 3:
			rbToBeSelected = rbFiles;
			break;
		case 4:
			rbToBeSelected = rbProjects;

			break;
		default:
			rbToBeSelected = null;
			break;
		}
		
		if (rbToBeSelected != null) {
			rbToBeSelected.setChecked(true);
		}
			
		// Set the click event on buttons
		rbProjects.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				waiting_for_next_activity(act, "Loading Projects...");
				Intent intentHome = new Intent(act, ProjectListActivity.class);
	    		act.startActivity(intentHome);
			}
		});

		rbFiles.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				waiting_for_next_activity(act, "Loading Files...");				
				Intent intentHome = new Intent(act, SelectingFileActivity.class);
	    		act.startActivity(intentHome);
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
	


}
