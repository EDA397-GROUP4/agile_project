package se.chalmers.group4.codenavigator;


import android.app.Application;

public class CodeNavigatorApplication extends Application {
	
	private String githubUser;
	private String githubPassword;

    public void setGithubCredentials(String user, String password) {
		this.githubUser = user;
		this.githubPassword = password;
    }
    
    public String getGithubUser() {
    	return this.githubUser;
    }
    
    public String getGithubPassword() {
    	return this.githubPassword;
    }

}
