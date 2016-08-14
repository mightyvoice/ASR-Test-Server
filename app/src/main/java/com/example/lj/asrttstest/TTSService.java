package com.example.lj.asrttstest;

/**
 * Created by lj on 16/5/23.
 */

import android.content.Context;

import com.example.lj.asrttstest.info.AppInfo;
import com.nuance.dragon.toolkit.audio.AudioChunk;
import com.nuance.dragon.toolkit.audio.AudioSource;
import com.nuance.dragon.toolkit.audio.AudioType;
import com.nuance.dragon.toolkit.audio.pipes.ConverterPipe;
import com.nuance.dragon.toolkit.audio.pipes.OpusDecoderPipe;
import com.nuance.dragon.toolkit.audio.pipes.SpeexDecoderPipe;
import com.nuance.dragon.toolkit.audio.sinks.PlayerSink;
import com.nuance.dragon.toolkit.audio.sinks.SpeakerPlayerSink;
import com.nuance.dragon.toolkit.cloudservices.CloudConfig;
import com.nuance.dragon.toolkit.cloudservices.CloudServices;
import com.nuance.dragon.toolkit.cloudservices.TransactionError;
import com.nuance.dragon.toolkit.cloudservices.vocalizer.CloudVocalizer;
import com.nuance.dragon.toolkit.cloudservices.vocalizer.TtsSpec;
import com.nuance.dragon.toolkit.data.Data;
import com.nuance.dragon.toolkit.util.Logger;

public class TTSService {

    private static final String SAMANTHA = "samantha";
    private CloudServices _cloudServices;
    private CloudVocalizer _vocalizer;
    private ConverterPipe<AudioChunk, AudioChunk> _decoder;
    private PlayerSink _player;
    private AudioType _audioType = AudioType.OPUS_WB;
    private AudioType _audioTypePlayback = AudioType.PCM_16k;

    public TTSService(Context appContext){
        reCreateCloudVocalizer(appContext);
    }

    public void performTTS(Context appContext, String input){
        TtsSpec ttsSpec = createTtsSpec(input, false, "enus", SAMANTHA, _audioType);
        generateTts(ttsSpec, appContext);
    }

    private void reCreateCloudVocalizer(Context appContext)
    {
        if (_vocalizer != null)
        {
            _vocalizer.cancel();
            _vocalizer = null;
        }

        if (_cloudServices != null)
        {
            _cloudServices.release();
            _cloudServices = null;
        }
        // Cloud services initialization
        _cloudServices = CloudServices.createCloudServices(appContext,
                new CloudConfig(AppInfo.Host, AppInfo.Port, AppInfo.AppId, AppInfo.AppKey, _audioType, _audioType));
        _vocalizer = new CloudVocalizer(_cloudServices);
    }

    private TtsSpec createTtsSpec(String text, boolean isSsml, String language, String voice, AudioType audioType)
    {
        Data.Dictionary ttsParamData = new Data.Dictionary();
        ttsParamData.put("tts_input", text);
        ttsParamData.put("tts_type", isSsml ? "ssml" : "text");

        Data.Dictionary settings = new Data.Dictionary();
        if (voice != null)
        {
            settings.put("tts_voice", voice);
        } else
        {
            settings.put("tts_language", language);
        }

        return new TtsSpec("NVC_TTS_CMD", settings, "TEXT_TO_READ", ttsParamData, audioType);
    }

    private void generateTts(TtsSpec ttsSpec, final Context appContext)
    {
        AudioSource<AudioChunk> audioSource = _vocalizer.generateTts(ttsSpec, new CloudVocalizer.Listener()
        {
            @Override
            public void onProcessingStarted()
            {
                Logger.info(appContext, "TTS processing started.");
            }

            @Override
            public void onSuccess()
            {
                Logger.info(appContext, "TTS success.");
            }

            @Override
            public void onError(TransactionError error)
            {
                Logger.info(appContext, "TTS error [" + error.getErrorText() + "].");
            }
        });

        _player = new SpeakerPlayerSink(_audioTypePlayback);
        if (_decoder != null)
        {
            _decoder.disconnectAudioSource();
            _decoder.release();
            _decoder = null;
        }
        _decoder =  new OpusDecoderPipe();
        _decoder.connectAudioSource(audioSource);
        _player.connectAudioSource(_decoder);
        _player.startPlaying();
    }

    public void close(){
        if (_vocalizer != null)
        {
            _vocalizer.cancel();
            _vocalizer = null;
        }

        if (_cloudServices != null)
        {
            _cloudServices.release();
            _cloudServices = null;
        }

        if (_player != null)
        {
            _player.stopPlaying();
            _player.disconnectAudioSource();
            _player = null;
        }

        if (_decoder != null)
        {
            _decoder.disconnectAudioSource();
            _decoder.release();
            _decoder = null;
        }
    }
}
