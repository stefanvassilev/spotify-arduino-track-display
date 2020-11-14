package com.stefan.vassilev.voice.to.songrecognizer.service;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.vassilev.voice.to.songrecognizer.dto.SongMetadataDto;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Service used to analyze sound data using Audd's REST API and retrieve potentially the song name
 */
public class ShazamRapidApiService implements SongDetector {


    private static final String TAG = "shazam rapid api service";

    private final String TOKEN_HEADER_KEY = "x-rapidapi-key";
    private final String TOKEN_HEADER_VALUE = "96abb5201cmsh766e9743e063b5fp16f154jsn119b0641f199";
    private final String HOST_HEADER_KEY = "x-rapidapi-host";
    private final String HOST_HEADER_VALUE = "shazam.p.rapidapi.com";

    private final CloseableHttpClient httpClient;
    private final ObjectMapper om = new ObjectMapper();

    private String tokenKey = System.getenv("SHAZAM_RAPID_API_TOKEN_KEY");

    public ShazamRapidApiService() {
        this.httpClient = HttpClientBuilder.create().build();
    }

    //TODO: rename to detect song

    /**
     * Detect songs from raw sound data.
     * The raw sound data must be 44100Hz, 1 channel (Mono), signed 16 bit PCM little endian.
     * Other types of media are NOT supported, such as : mp3, wav, etc… or need to be converted to uncompressed raw data.
     * Encoded base64 string of byte[] that generated from raw data less than 500KB (3-5 seconds sample are good enough for detection) will be sent via body as plain text
     *
     * @param raw
     * @return
     */
    @Override
    public SongMetadataDto detectSong(byte[] raw) {
        //TODO: add validations of raw data and test API

        try {
            HttpPost detectSongReq = new HttpPost(new URIBuilder("https://shazam.p.rapidapi.com/songs/detect").build());
            detectSongReq.setHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
            detectSongReq.setHeader(HOST_HEADER_KEY, HOST_HEADER_VALUE);
            detectSongReq.setHeader(TOKEN_HEADER_KEY, TOKEN_HEADER_KEY);

            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(new ByteArrayInputStream(raw));
            detectSongReq.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(detectSongReq)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() > 299) {
                    throw new SongDetectorException("failed to detect song: " + responseBody);
                }

                Log.i(TAG, "Response  is: " + responseBody);

                return om.readValue(responseBody, SongMetadataDto.class);

            } catch (IOException e) {
                throw new SongDetectorException("failed to detect song", e);
            }


        } catch (URISyntaxException e) {
            throw new SongDetectorException("failed to build uri ", e);
        }

    }


}
