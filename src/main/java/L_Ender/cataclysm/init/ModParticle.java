package L_Ender.cataclysm.init;

import L_Ender.cataclysm.cataclysm;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class ModParticle {
    public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, cataclysm.MODID);


    public static final RegistryObject<BasicParticleType> SOUL_LAVA = PARTICLE.register("soul_lava", ()-> new BasicParticleType(false));

}
