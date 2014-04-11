package se.chalmers.group4.codenavigator;


import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import android.app.Application;

public class CodeNavigatorApplication extends Application {
	

	private GHRepository githubRepository;
	private GitHub githubObject;
	private Long updateTime; 


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
    

    

}
