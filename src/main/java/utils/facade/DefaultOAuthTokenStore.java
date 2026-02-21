package utils.facade;

import org.springframework.stereotype.Component;

@Component
public class DefaultOAuthTokenStore implements GoogleOAuthTokenStore {

    @Override
    public String findAccessTokenByStaffProfileId(Long staffProfileId) {
        return "mock-token-provvisorio";
    }

}