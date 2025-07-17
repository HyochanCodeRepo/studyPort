/***********************************************
 * 클래스명 : CustomOAuth2UserService
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-07-16
 * 수정 : 2025-07-16
 * ***********************************************/
package com.example.studyport.service;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.dto.OAuthAttributes;
import com.example.studyport.dto.UserDTO;
import com.example.studyport.entity.Members;
import com.example.studyport.entity.User;
import com.example.studyport.repository.MemberRepository;
import com.example.studyport.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스를 구분하는 코드 (네이버 로그인인지 구글 로그인인지 구분)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드 값 (Primary Key와 같은 의미)을 의미
        // 구글의 기본 코드는 "sub", 후에 네이버 로그인과 구글 로그인을 동시 지원할 때 사용
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuthUser의 attribute를 담을 클래스 ( 네이버 등 다른 소셜 로그인도 이 클래스 사용)
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Members members = saveOrUpdate(attributes);

        // UserEntity 클래스를 사용하지 않고 SessionUser클래스를 사용하는 이유는 오류 방지.
        httpSession.setAttribute("user", new MembersDTO(members)); // UserDTO : 세션에 사용자 정보를 저장하기 위한 Dto 클래스

        log.info("로그인된거? {}" , members);
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_"+members.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    // 구글 사용자 정보 업데이트 시 Members 엔티티에 반영
    private Members saveOrUpdate(OAuthAttributes attributes) {
        // 이메일을 기준으로 사용자를 찾아 업데이트하거나, 사용자를 새로 생성합니다.
        Members members = memberRepository.findByEmail(attributes.toEntity().getEmail());
        if(members!=null){
            members.setName(attributes.toEntity().getName());
        }else {
            members = attributes.toEntity();
        }
        log.info("저장된거? {}", members);
        return memberRepository.save(members);
    }
}
