/***********************************************
 * 클래스명 : OAuthAttributes
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-07-16
 * 수정 : 2025-07-16
 * ***********************************************/
package com.example.studyport.dto;

import com.example.studyport.constant.Role;
import com.example.studyport.entity.Members;
import com.example.studyport.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String provider;
    private String providerId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String provider, String providerId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver(registrationId, userNameAttributeName, attributes);
        }

        return ofGoogle(registrationId, userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        //todo 일단 이름 이메일만 받아오는데 나중에 전화번호 끌어오는것도.... 한번.

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(attributes)
                .provider(registrationId)
                .providerId((String) response.get("id"))
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .provider(registrationId)
                .providerId((String) attributes.get("sub"))
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Members toEntity() {
        return Members.builder()
                .name(name)
                .email(email)
                .role(Role.USER)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
