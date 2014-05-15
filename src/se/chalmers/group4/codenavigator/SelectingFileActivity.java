package se.chalmers.group4.codenavigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommit.File;
import org.kohsuke.github.GHRepository;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

public class SelectingFileActivity extends Activity {
    private static final String    TAG_NUMBER_OF_FILES = "numberOfFiles";
    private static final String    TAG_FILE_NAME       = "file_";
	private static final String    C_MASTER_NAME = "master";
	private ListView               theSelectedFilesListView;
	private Button                 saveSelectedFilesButton;
	private ArrayList<String>      allRepositoryFiles = new ArrayList<String>();
	private SelectableArrayAdapter selectableArrayAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("select","startingoncreate...");

		//setContentView(R.layout.activity_selecting_file);
		NavigationBar.load_navbar(this,2);
		NavigationBar.insert_main_layout(this, R.layout.activity_selecting_file);

		// Create and launch the SelectFileLoad AsyncTask
		// (network activities cannot be done in the main thread)
		new SelectFilesLoadTask().execute();
		
	}

	
	/**
	 * Help method to:
	 * 	 connect an adapter and listener to the list of filename+checkbox
	 *   connect a listener to the save button
	 */
    private void setupSelectableList() {
        // the adapter
    	Log.d("select","innan findViewById listOfFilenames...");
		theSelectedFilesListView = (ListView)findViewById(R.id.listOfFilenames);
		Log.d("select","innan new SelectablearrayAdapter...");
		selectableArrayAdapter   = new SelectableArrayAdapter( this,
				                                               R.layout.filerow,
				                                               R.id.checkListItem,
				                                       		   allRepositoryFiles );
		Log.d("select","innan setAdapter...");
		theSelectedFilesListView.setAdapter(selectableArrayAdapter);

		// the listener
		Log.d("select","innan setOnItemCLickListener...");
		theSelectedFilesListView.setOnItemClickListener(theOnItemClickListener);
		Log.d("select","innan find button...");
		saveSelectedFilesButton  = (Button)findViewById(R.id.saveSelectedFilesButton);
		Log.d("select","innan oncliclistne button");
		saveSelectedFilesButton.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> interestingFiles = new ArrayList<String>();
				String result = "";
				List<String> resultList = selectableArrayAdapter.getCheckedItems();
				for (int i=0; i<resultList.size(); i++) {
					String tmpFilename = String.valueOf(resultList.get(i));
//					result += String.valueOf(resultList.get(i)) + "\n";
					result += tmpFilename + "\n";
					interestingFiles.add(tmpFilename);
				}
				Log.d("select","onclick result: "+result)	;			
				selectableArrayAdapter.getCheckedItemPositions().toString();
				Toast.makeText( getApplicationContext(), result, Toast.LENGTH_LONG).show();
				
				// save selected files permanently
				saveSelectedFiles( interestingFiles );
				Log.d("selectSavedFiles","after saveSelectedfiles...");
				//Testing...
				ArrayList<String> retrievedFiles = getSavedFiles();
				for( String theFilename: retrievedFiles ) {
					Log.d("selectGETSAVEDFILEs","file: "+theFilename+"\n");
				}
			}
		});

    }

 
    private void saveSelectedFiles( List<String> filesToSave ) {
//        int numberOfSelectedFiles        = filesToSave.size();
        
		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
		ArrayList<String> interestingFiles = app.getFavoriteFiles();
//        SharedPreferences preferences = getPreferences( MODE_PRIVATE );
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt(TAG_NUMBER_OF_FILES, numberOfSelectedFiles);
//        editor.clear();  //delete all old filenames...
// // putStringSet(String key, Set<String> values) finns även en getStringSet
// // remove(String key)
//        int i = 0;
        for (String filename: filesToSave) {
        	interestingFiles.add(filename);
//     	   String tmpFileTag = TAG_FILE_NAME+i;
//     	   editor.putString(tmpFileTag, filename);
//     	   i++;
        }
//        editor.commit();
        app.setFavoriteFiles(interestingFiles);
     }
     
     public ArrayList<String> getSavedFiles() {
 		CodeNavigatorApplication app = (CodeNavigatorApplication) getApplication();
 		ArrayList<String> interestingFiles = app.getFavoriteFiles();
//    	ArrayList<String> theSavedFiles = new ArrayList<String>();
//     	
//         SharedPreferences preferences = getPreferences( MODE_PRIVATE );
////         SharedPreferences..Editor editor = preferences.edit();
//     	// Get number of saved files
//         int numberOfSelectedFiles = preferences.getInt(TAG_NUMBER_OF_FILES,0);
//         for ( int i = 0; i < numberOfSelectedFiles; i++  ) {
//            String tmpFileTag = TAG_FILE_NAME+i;
//            String tmpSavedFileName = preferences.getString(tmpFileTag, "");
//            theSavedFiles.add(tmpSavedFileName);
//         }
     	
//     	return theSavedFiles;
 		return interestingFiles;
     }

 /*   
    private void saveSelectedFiles( List<String> filesToSave ) {
       int numberOfSelectedFiles        = filesToSave.size();
       
       SharedPreferences preferences = getPreferences( MODE_PRIVATE );
       SharedPreferences.Editor editor = preferences.edit();
       editor.putInt(TAG_NUMBER_OF_FILES, numberOfSelectedFiles);
       editor.clear();  //delete all old filenames...
// putStringSet(String key, Set<String> values) finns även en getStringSet
// remove(String key)
       int i = 0;
       for (String filename: filesToSave) {
    	   String tmpFileTag = TAG_FILE_NAME+i;
    	   editor.putString(tmpFileTag, filename);
    	   i++;
       }
       editor.commit();
    }
    
    public ArrayList<String> getSavedFiles() {
    	ArrayList<String> theSavedFiles = new ArrayList<String>();
    	
        SharedPreferences preferences = getPreferences( MODE_PRIVATE );
//        SharedPreferences..Editor editor = preferences.edit();
    	// Get number of saved files
        int numberOfSelectedFiles = preferences.getInt(TAG_NUMBER_OF_FILES,0);
        for ( int i = 0; i < numberOfSelectedFiles; i++  ) {
           String tmpFileTag = TAG_FILE_NAME+i;
           String tmpSavedFileName = preferences.getString(tmpFileTag, "");
           theSavedFiles.add(tmpSavedFileName);
        }
    	
    	return theSavedFiles;
    }
*/    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	
	/**
	 * Listener for the row's checkbox
	 * @author Marianne
	 *
	 */
	OnItemClickListener theOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectableArrayAdapter.toggleChecked(position);			
		}
	};

	
	/**
	 * Private inner class to handle define the array adapter for the rows in the selectable list
	 * @author Marianne
	 */
	private class SelectableArrayAdapter extends ArrayAdapter<String> {
		private HashMap<Integer,Boolean> myChecked = new HashMap<Integer,Boolean>();
		
		public SelectableArrayAdapter( Context context, int resource, int textViewResourceId, List<String> objects) {
			super( context, resource, textViewResourceId, objects);
			for ( int i= 0; i < objects.size(); i++) {
				myChecked.put(i, false);
			}
		}
		
		public void toggleChecked( int position) {
			if (myChecked.get(position)) {
				myChecked.put(position,  false);
			} else {
				myChecked.put(position, true);			
			}
			notifyDataSetChanged();
		}
		
		public List<Integer> getCheckedItemPositions() {
			List<Integer> checkedItemPositions = new ArrayList<Integer>();
			
			for (int i=0; i<myChecked.size(); i++) {
				if (myChecked.get(i)) {
					(checkedItemPositions).add(i);
				}
			}
			return checkedItemPositions;
		}
		
		public List<String> getCheckedItems() {
			List<String> checkedItems = new ArrayList<String>();
			
			for (int i=0; i<myChecked.size(); i++) {
				if (myChecked.get(i)) {
					(checkedItems).add(allRepositoryFiles.get(i));
				}
			}
			return checkedItems;
		}
		
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {

			View row = convertView;
			if ( row == null ) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.filerow, parent,false);
			}
			
			CheckedTextView checkedTextView = (CheckedTextView) row.findViewById(R.id.checkListItem);
			Log.d("select","innan setText.. pos:"+position+" value: "+allRepositoryFiles.get(position));

			checkedTextView.setText(allRepositoryFiles.get(position));
			Log.d("select","efter settEXT...");

			Boolean checked = myChecked.get(position);
			if ( checked != null ) {
				checkedTextView.setChecked(checked);
			}
			return row;
		}
	}   // end of inner class: SelectableArrayAdapter
	

	
	/**
	 * Private inner class inherited from AsyncTask used for loading the list of
	 * files in a new thread.
	 * 
	 * This is made with a loop on the commit list (retrieved from the global
	 * Github Repository Object stored in the app class)
	 * @author Marianne
	 */
	private class SelectFilesLoadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... v) {
			StringBuilder     finalMasterBranchFiles = new StringBuilder();
			GHBranch          theMasterBranch;
//			ArrayList<String> tmpAllRepositoryFiles  = new ArrayList<String>();
			//finalMasterBranchFiles.append("Masterbranch files are: ");
			try {

				// Get the selected repository from the app class
				CodeNavigatorApplication app           = (CodeNavigatorApplication) getApplication();
				GHRepository             theRepository = app.getGithubRepository();
				Map<String,GHBranch>	 theBranches   = theRepository.getBranches();
				Log.d("selectFiles","####theBranches = :"+theBranches);
				Log.d("selectFiles","theBranches got :"+theBranches.size()+" items");
				Iterator<String>         iterator      = theBranches.keySet().iterator();
//				int i = 0;
				while (iterator.hasNext())    {
					
					Object key = iterator.next();
					Object value = theBranches.get(key);

					if ( key.equals(C_MASTER_NAME)) {
						theMasterBranch = (GHBranch)value;
						Log.d("selectFilesMasterFound","masterbranch found"+theMasterBranch.toString());

						String theMasterSHA          = theMasterBranch.getSHA1();
						GHCommit theMasterCommit     = theRepository.getCommit(theMasterSHA);
						List<File> allTheMasterFiles = theMasterCommit.getFiles();
						for ( File aFile: allTheMasterFiles ) {
							finalMasterBranchFiles.append(aFile.getFileName()+",");
//							tmpAllRepositoryFiles.add(aFile.getFileName());
						}
						Log.d("selectFilesRESULT",finalMasterBranchFiles.toString());
					}
				}
				return finalMasterBranchFiles.toString();
			} catch (Exception e) {
				Log.d("selectFiles","Exception during file loading :"+e.getMessage() );
			}

			return finalMasterBranchFiles.toString();
		}
		
		
		@Override
		protected void onPostExecute(String result) {

			StringTokenizer tmpAllRepositoryFiles = new StringTokenizer(result, ",");
			while (tmpAllRepositoryFiles.hasMoreTokens()) {
				allRepositoryFiles.add(tmpAllRepositoryFiles.nextToken());
			}
			
			setupSelectableList();
			Log.d("selectEndingOnPostExecute", "allRepositoryFiles has:"+allRepositoryFiles.size()+" filenames..");
		}
	}

}
