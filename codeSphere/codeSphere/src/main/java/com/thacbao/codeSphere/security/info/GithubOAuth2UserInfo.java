package com.thacbao.codeSphere.security.info;

import com.thacbao.codeSphere.security.OAuth2UserInfo;

import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        String email = (String) attributes.get("login");
        email = email + "@codeSphere.com";
        return email;
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}