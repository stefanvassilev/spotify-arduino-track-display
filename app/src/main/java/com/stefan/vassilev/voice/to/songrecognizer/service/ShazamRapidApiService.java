package com.stefan.vassilev.voice.to.songrecognizer.service;

import com.stefan.vassilev.voice.to.songrecognizer.dto.SongMetadataDto;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Service used to analyze sound data using Audd's REST API and retrieve potentially the song name
 */
public class ShazamRapidApiService implements SongAnalyzer {

    private final CloseableHttpClient httpClient;

    private String tokenKey = System.getenv("SHAZAM_RAPID_API_TOKEN_KEY");

    public ShazamRapidApiService() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    @Override
    public SongMetadataDto retrieveSongMetadata(byte[] raw) {
        return null;
    }


    //TODO
}
