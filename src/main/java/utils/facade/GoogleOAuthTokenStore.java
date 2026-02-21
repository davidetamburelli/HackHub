package utils.facade;

public interface GoogleOAuthTokenStore {

    String findAccessTokenByStaffProfileId(Long staffProfileId);

}