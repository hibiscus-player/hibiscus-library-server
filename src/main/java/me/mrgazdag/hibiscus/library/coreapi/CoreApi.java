package me.mrgazdag.hibiscus.library.coreapi;

import java.io.IOException;

public interface CoreApi {
    ProfileData getProfile(String profileId, String serverKey) throws IOException;
}
