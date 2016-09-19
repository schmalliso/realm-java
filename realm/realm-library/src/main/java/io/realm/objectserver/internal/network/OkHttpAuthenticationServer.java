/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.objectserver.internal.network;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.realm.internal.Util;
import io.realm.objectserver.ErrorCode;
import io.realm.objectserver.User;
import io.realm.objectserver.internal.Token;
import io.realm.objectserver.Credentials;
import io.realm.objectserver.ObjectServerError;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpAuthenticationServer implements AuthenticationServer {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * Authenticate the given credentials on the specified Realm Authentication Server.
     */
    @Override
    public AuthenticateResponse authenticateUser(Credentials credentials, URL authenticationUrl) {
        try {
            String requestBody = AuthenticateRequest.fromCredentials(credentials).toJson();
            return authenticate(authenticationUrl, requestBody);
        } catch (Exception e) {
            return new AuthenticateResponse(new ObjectServerError(ErrorCode.OTHER_ERROR, Util.getStackTrace(e)));
        }
    }

    @Override
    public AuthenticateResponse authenticateRealm(Token refreshToken, URI path, URL authenticationUrl) {
        try {
            String requestBody = AuthenticateRequest.fromRefreshToken(refreshToken, path).toJson();
            return authenticate(authenticationUrl, requestBody);
        } catch (Exception e) {
            return new AuthenticateResponse(new ObjectServerError(ErrorCode.UNKNOWN, e));
        }
    }

    @Override
    public RefreshResponse refresh(String token, URL authenticationUrl) {
        throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public LogoutResponse logout(User user, URL authenticationUrl) {
        throw new UnsupportedOperationException("FIXME");
    }

    private AuthenticateResponse authenticate(URL authenticationUrl, String requestBody) throws Exception {
        Request request = new Request.Builder()
                .url(authenticationUrl)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Connection", "close") //  See https://github.com/square/okhttp/issues/2363
                .post(RequestBody.create(JSON, requestBody))
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return AuthenticateResponse.createFrom(response);
    }
}