package se.chalmers.group4.codenavigator;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

public class NavigationBar {
	
	public static void load_navbar(Activity act) {
		act.setContentView(R.layout.navbar_layout);
	}
	
	public static void insert_main_layout(Activity act, int mLayout) {
		RelativeLayout root = (RelativeLayout) act.findViewById(R.id.navbar_layout_root);
		View layout = (View) act.getLayoutInflater().inflate(mLayout, null);
		root.addView(layout);
	}

}
