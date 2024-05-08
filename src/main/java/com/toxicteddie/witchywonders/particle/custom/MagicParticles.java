package com.toxicteddie.witchywonders.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagicParticles extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected MagicParticles(ClientLevel level, double xCoord, double yCoord, double zCoord,
                             SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd);

        this.sprites = spriteSet;
        this.friction = 0.8F;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        // Adjust quadSize to change the particle size, e.g., 1.0F for original size
        this.quadSize *= 5.0F;
        this.lifetime = 40; // Adjust as needed
        this.setSpriteFromAge(sprites);

        this.rCol = 10f;
        this.gCol = 10f;
        this.bCol = 10f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites); // Automatically select the correct frame
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)lifetime) * age + 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new MagicParticles(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
