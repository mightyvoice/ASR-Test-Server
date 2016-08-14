package com.example.lj.asrttstest;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

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

public class TTSCloudActivity extends AppCompatActivity {

    private static final String SAMANTHA = "samantha";
    private CloudServices _cloudServices;
    private CloudVocalizer _vocalizer;
    private ConverterPipe<AudioChunk, AudioChunk> _decoder;
    private PlayerSink _player;
    private AudioType _audioType = AudioType.OPUS_WB;
    private AudioType                             _audioTypePlayback = AudioType.PCM_16k;

    private boolean                               _speexEnabled;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); // So that the 'Media Volume' applies to this activity
        setContentView(R.layout.activity_tts_cloud);

        // UI initialization
        final EditText editText = (EditText)findViewById(R.id.editText);
        final Button speakButton = (Button) findViewById(R.id.speakButton);
        final Button cancelButton = (Button) findViewById(R.id.cancelTTSButton);
        final CheckBox speexCheckBox = (CheckBox) findViewById(R.id.speexCheckBox);

        findViewById(R.id.speedLayout).setVisibility(View.GONE);
        findViewById(R.id.volumeLayout).setVisibility(View.GONE);
        findViewById(R.id.languageSpinner).setVisibility(View.GONE);
        findViewById(R.id.voiceSpinner).setVisibility(View.GONE);
        findViewById(R.id.stopAudioButton).setVisibility(View.GONE);

        // by default using opus
        _speexEnabled = speexCheckBox.isChecked();
        _audioType = AudioType.OPUS_WB;

        speexCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                _speexEnabled = isChecked;
                if (!_speexEnabled)
                {
                    _audioType = AudioType.OPUS_WB;
                }
                else
                {
                    _audioType = AudioType.SPEEX_WB;
                }
                reCreateCloudVocalizer();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                if (arg2.getKeyCode()==KeyEvent.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_DOWN)
                {
                    TtsSpec ttsSpec = createTtsSpec(editText.getText().toString(), false, "enus", SAMANTHA, _audioType);
                    generateTts(ttsSpec);
                    return true;
                }
                return false;
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TtsSpec ttsSpec = createTtsSpec(editText.getText().toString(), false, "enus", SAMANTHA, _audioType);
                generateTts(ttsSpec);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _vocalizer.cancel();
            }
        });

        reCreateCloudVocalizer();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

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

    private void generateTts(TtsSpec ttsSpec)
    {
        AudioSource<AudioChunk> audioSource = _vocalizer.generateTts(ttsSpec, new CloudVocalizer.Listener()
        {
            @Override
            public void onProcessingStarted()
            {
                Logger.info(TTSCloudActivity.this, "TTS processing started.");
            }

            @Override
            public void onSuccess()
            {
                Logger.info(TTSCloudActivity.this, "TTS success.");
            }

            @Override
            public void onError(TransactionError error)
            {
                Logger.info(TTSCloudActivity.this, "TTS error [" + error.getErrorText() + "].");
            }
        });

        _player = new SpeakerPlayerSink(_audioTypePlayback);
        if (_decoder != null)
        {
            _decoder.disconnectAudioSource();
            _decoder.release();
            _decoder = null;
        }
        _decoder = _speexEnabled ? new SpeexDecoderPipe() : new OpusDecoderPipe();
        _decoder.connectAudioSource(audioSource);
        _player.connectAudioSource(_decoder);
        _player.startPlaying();
    }

    private void reCreateCloudVocalizer()
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
        _cloudServices = CloudServices.createCloudServices(TTSCloudActivity.this,
                new CloudConfig(AppInfo.Host, AppInfo.Port, AppInfo.AppId, AppInfo.AppKey, _audioType, _audioType));
        _vocalizer = new CloudVocalizer(_cloudServices);
    }
}
