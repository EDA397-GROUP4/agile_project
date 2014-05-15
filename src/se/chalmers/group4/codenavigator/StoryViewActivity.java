package se.chalmers.group4.codenavigator;

import org.kohsuke.github.GHIssue;

//import se.chalmers.group4.codenavigator.R.id;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StoryViewActivity extends Activity {

	private GHIssue story;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NavigationBar.load_navbar(this, 2);
		NavigationBar.insert_main_layout(this, R.layout.story_layout);
		ActionBar bar = getActionBar();
		bar.setTitle("Stories");
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
		story = app.getCurrentStory();
		// Load the issue-text
		setTitle(story.getTitle());
		setStory(story.getBody());

	}

	protected void setStory(String result) {
		// Write the result to the UI.
		TextView textView;
		textView = (TextView) findViewById(R.id.TextViewStory);
		textView.setText(result);

	}

	public void assignPair(View v) throws Exception {

		Intent intent = new Intent(this, AssigningpairActivity.class);

		startActivity(intent);
	}
}
