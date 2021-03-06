package se.chalmers.group4.codenavigator;


import java.util.ArrayList;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import android.app.Application;

public class CodeNavigatorApplication extends Application {
	

	private GHRepository githubRepository;
	private GitHub githubObject;
	private Long updateTime; 
	private GHIssue story;
    private ArrayList<String> favoriteFiles;

    public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public void setGithubRepository(GHRepository repo) {
    	this.githubRepository = repo;
    }

    public GHRepository getGithubRepository() {
    	return this.githubRepository;
    }
	public GitHub getGithubObject() {
		return githubObject;
	}
	public void setGithubObject(GitHub github) {
		// Remove the previously selected repository
		// (the user might have changed)
		this.githubRepository = null;
		this.githubObject = github;
	}
    
	public void setCurrentStory(GHIssue story) {
		this.story = story;
	}
	
	public GHIssue getCurrentStory() {
		return this.story;
	}

	public ArrayList<String> getFavoriteFiles() {
		if (favoriteFiles==null) favoriteFiles = new ArrayList<String>();
		return favoriteFiles;
	}

	public void setFavoriteFiles(ArrayList<String> favoriteFiles) {
		this.favoriteFiles = favoriteFiles;
	}

}
