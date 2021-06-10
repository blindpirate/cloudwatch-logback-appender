/*
 * Copyright 2018  Dieter Bogdoll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dibog;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;

public class AwsConfig {
    private ClientConfiguration clientConfig;
    private AwsCredentials credentials;
    private String profileName;
    private String region;

    public void setCredentials(AwsCredentials credentials) {
        this.credentials = credentials;
    }

    public void setClientConfig(ClientConfiguration clientConfig) {
        this.clientConfig = clientConfig;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    private AWSCredentialsProvider createCredentialProvider(final String accessKeyIdEnv, final String secretAccessKeyBeijingEnv) {
        return new AWSStaticCredentialsProvider(new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return System.getenv(accessKeyIdEnv);
            }

            @Override
            public String getAWSSecretKey() {
                return System.getenv(secretAccessKeyBeijingEnv);
            }
        });
    }
    public AWSLogs createAWSLogs() {
        AWSLogsClientBuilder builder = AWSLogsClientBuilder.standard();

        if (System.getenv("LOG_AWS_DEFAULT_REGION") != null) {
            builder.withRegion(Regions.fromName(System.getenv("LOG_AWS_DEFAULT_REGION")));
        } else if (System.getenv("AWS_DEFAULT_REGION") != null) {
            builder.withRegion(Regions.fromName(System.getenv("AWS_DEFAULT_REGION")));
        } else if (region != null) {
            builder.withRegion(region);
        }

        if (clientConfig != null) {
            builder.withClientConfiguration(clientConfig);
        }

        if (System.getenv("LOG_AWS_ACCESS_KEY_ID") != null) {
            builder.withCredentials(createCredentialProvider("LOG_AWS_ACCESS_KEY_ID", "LOG_AWS_SECRET_ACCESS_KEY"));
        } else if (System.getenv("AWS_ACCESS_KEY_ID") != null) {
            builder.withCredentials(createCredentialProvider("AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"));
        } else if (profileName != null) {
            builder.withCredentials(new ProfileCredentialsProvider(profileName));
        } else if (credentials != null) {
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }

        return builder.build();
    }
}
