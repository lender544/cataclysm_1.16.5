package L_Ender.cataclysm.client.sound;

import L_Ender.cataclysm.ClientProxy;
import L_Ender.cataclysm.entity.Ignis_Entity;
import L_Ender.cataclysm.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;

import java.util.Map;

public class SoundIgnisMusic extends TickableSound {
    private final Ignis_Entity Ignis;
    private int ticksExisted = 0;
    public SoundIgnisMusic(Ignis_Entity Ignis) {
        super(ModSounds.IGNIS_MUSIC.get(), SoundCategory.RECORDS);
        this.Ignis = Ignis;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
        this.priority = true;
        this.x = this.Ignis.getPosX();
        this.y = this.Ignis.getPosY();
        this.z = this.Ignis.getPosZ();
    }

    public boolean shouldPlaySound() {
        return !this.Ignis.isSilent() && ClientProxy.IGNIS_SOUND_MAP.get(this.Ignis.getEntityId()) == this;
    }

    public boolean isNearest() {
        float dist = 400;
        for(Map.Entry<Integer, SoundIgnisMusic> entry : ClientProxy.IGNIS_SOUND_MAP.entrySet()){
            SoundIgnisMusic MonstroMusic = entry.getValue();
            if(MonstroMusic != this && distanceSq(MonstroMusic.x, MonstroMusic.y, MonstroMusic.z) < dist * dist && MonstroMusic.shouldPlaySound()){
                return false;
            }
        }
        return true;
    }


    public double distanceSq(double p_218140_1_, double p_218140_3_, double p_218140_5_) {
        double lvt_10_1_ = (double)this.getX() - p_218140_1_;
        double lvt_12_1_ = (double)this.getY() - p_218140_3_;
        double lvt_14_1_ = (double)this.getZ() - p_218140_5_;
        return lvt_10_1_ * lvt_10_1_ + lvt_12_1_ * lvt_12_1_ + lvt_14_1_ * lvt_14_1_;
    }

    public void tick() {
        if(ticksExisted % 100 == 0){
            Minecraft.getInstance().getMusicTicker().stop();

        }
        if (!this.Ignis.removed && this.Ignis.isAlive()) {
            this.volume = 1;
            this.pitch = 1;
            this.x = this.Ignis.getPosX();
            this.y = this.Ignis.getPosY();
            this.z = this.Ignis.getPosZ();
        } else {
            this.finishPlaying();
            ClientProxy.IGNIS_SOUND_MAP.remove(Ignis.getEntityId());
        }
        ticksExisted++;
    }

}
