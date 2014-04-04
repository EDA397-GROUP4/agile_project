package se.chalmers.group4.codenavigator;


import org.kohsuke.github.GHRepository;

import android.app.Application;

public class CodeNavigatorApplication extends Application {
	
	private String githubUser;
	private String githubPassword;
	private GHRepository githubRepository;

    public void setGithubCredentials(String user, String password) {
		this.githubUser = user;
		this.githubPassword = password;
    }
    
    public void setGithubRepository(GHRepository repo) {
    	this.githubRepository = repo;
    }
    
    public String getGithubUser() {
    	return this.githubUser;
    }
    
    public String getGithubPassword() {
    	return this.githubPassword;
    }
    
    public GHRepository getGithubRepository() {
    	return this.githubRepository;
    }
    

}
