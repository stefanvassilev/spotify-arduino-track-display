package com.stefan.vassilev.voice.to.songrecognizer.service;

import com.stefan.vassilev.voice.to.songrecognizer.dto.SongMetadataDto;

/**
 * Service used to analyze sound data and retrieve potentially the song name
 */
public interface SongAnalyzer {


    SongMetadataDto retrieveSongMetadata(byte[] raw);

}
