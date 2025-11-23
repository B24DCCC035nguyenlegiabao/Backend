package com.trungtam.LearningCenterApi.security.oauth2;

import com.trungtam.LearningCenterApi.entity.User;
import com.trungtam.LearningCenterApi.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google" or "facebook"
        Map<String, Object> attributes = oauth2User.getAttributes();

        String providerId = null;
        String email = null;
        String name = null;

        if ("google".equalsIgnoreCase(registrationId)) {
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else if ("facebook".equalsIgnoreCase(registrationId)) {
            providerId = (String) attributes.get("id");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        } else {
            // other providers
            providerId = (String) attributes.get("id");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        User.Provider provider = User.Provider.LOCAL;
        if ("google".equalsIgnoreCase(registrationId)) provider = User.Provider.GOOGLE;
        if ("facebook".equalsIgnoreCase(registrationId)) provider = User.Provider.FACEBOOK;

        // find existing user by provider + providerId
        Optional<User> userOpt = userRepository.findByProviderAndProviderId(provider, providerId);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            // update info if needed
            if (email != null) user.setUsername(email);
        } else {
            // if not found, try find by email to link accounts
            if (email != null && userRepository.findByUsername(email).isPresent()) {
                user = userRepository.findByUsername(email).get();
                user.setProvider(provider);
                user.setProviderId(providerId);
            } else {
                // create new user
                user = new User();
                user.setUsername(email != null ? email : registrationId + "_" + providerId);
                user.setPassword(""); // no local password
                user.setProvider(provider);
                user.setProviderId(providerId);
                user.setRole("ROLE_STAFF");
                userRepository.save(user);
            }
        }

        // Return a DefaultOAuth2User so Spring can continue; map role to authority
        return new DefaultOAuth2User(user.getAuthorities(), attributes, "id");
    }
}

