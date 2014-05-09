package se.chalmers.group4.codenavigator;


import java.io.IOException;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueComment;

import se.chalmers.group4.codenavigator.R.id;
import android.app.Activity;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StoryViewActivity extends Activity {
	
	private GHIssue story;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NavigationBar.load_navbar(this,4);
		NavigationBar.insert_main_layout(this, R.layout.story_layout);
		
		CodeNavigatorApplication app = (CodeNavigatorApplication)getApplication();
		story = app.getCurrentStory();
		//Load the issue-text
		setTitle(story.getTitle());
		setStory(story.getBody());
		
				
	}

    protected void setStory(String result) {
    	// Write the result to the UI.
    	TextView textView;
    	textView = (TextView)findViewById(R.id.TextViewStory);
    	textView.setText(result);

    	}
}
