package org.example.listeners.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

public class audioForwarder implements AudioSendHandler {

    public final AudioPlayer player;
    public final ByteBuffer buffer = ByteBuffer.allocate(1024);
    public final MutableAudioFrame frame = new MutableAudioFrame();

    public audioForwarder(AudioPlayer player) {
        this.player = player;
        frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        return player.provide(frame);
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return buffer.flip();
    }
    @Override
    public boolean isOpus() {
        return true;
    }
}
