package com.ftec.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.ftec.configs.enums.TutorialSteps;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document(indexName = "users")
public class User {
	
    @Id
    private long id;

    @NotNull
    @Size(min = 3)
    private String username;

    @NotNull
    @Size(min = 4)
    private String password;

    @NotNull
    private String email;
    
    private TutorialSteps currentStep;

    @NotNull
    private boolean subscribeForNews;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSubscribeForNews() {
        return subscribeForNews;
    }

    public void setSubscribeForNews(boolean subscribeForNews) {
        this.subscribeForNews = subscribeForNews;
    }

	public TutorialSteps getStep() {
		return currentStep;
	}

	public void setCurrentStep(TutorialSteps step) {
		this.currentStep = step;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + ", currentStep=" + currentStep + ", subscribeForNews=" + subscribeForNews + "]";
	}
}
