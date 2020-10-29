package iDiamondhunter.morebows.entities;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class FrostArrow extends EntityArrow { //TODO re-implement rendering of snowball instead of arrow

    private boolean crit = false;
    private int groundTicks = 0;
    private boolean inGround = false;

    public FrostArrow(World world) {
        super(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public FrostArrow(World world, double var1, double var2, double var3) {
        super(world, var1, var2, var3);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public FrostArrow(World world, EntityLivingBase living1, EntityLivingBase living2, float var1, float var2) {
        super(world, living1, living2, var1, var2);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public FrostArrow(World world, EntityLivingBase living, float var) {
        super(world, living, var);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /** This doesn't actually return whether the arrow is crit, it will always return false! See the comments in the code for why this awful hack was made. */
    @Override
    public boolean getIsCritical() {
        return false;
        /* Obviously, you're just a bad shot :D
         *
         * This is an awful hack to prevent the vanilla crit particles from displaying.
         * The vanilla code to display the arrow particle trail is buried deep inside onUpdate,
         * and the only other options I have are to:
         * - intercept the particles with packets,
         * - intercept the particles with events (not feasible from what I can tell),
         * - ASM it out,
         * - or perform some ridiculous wrapping around the World to intercept the method to spawn particles.
         *
         * Instead of doing that, I just prevent anything from ever knowing that it's crited,
         * and instead I wrap around the event when the arrow attacks something. See onLivingAttackEvent() for the details,
         * but the TLDR is that I cancel the attack and start a new one with the crit taken into account.
         * This allows the entity to take the crit into account when deciding if it's damaged or not.
         *
         * This is probably the lesser of these evils.
         */
    }

    /** See {@code getIsCritical()} for why this kills the arrow after the attack. */
    @SubscribeEvent
    public void hitListen(LivingHurtEvent event) {
        if (this == event.source.getSourceOfDamage()) {
            event.entity.setInWeb(); //TODO Replace with slowness effect? This is the original behavior...
        }

        if (!(event.entity instanceof EntityEnderman)) { //TODO Verify that this is the right behavior
            setDead();
        }
    }

    /** See {@code getIsCritical()} for an explanation of why this method manually adds crit damage to the attack. */
    @SubscribeEvent
    public void onLivingAttackEvent(LivingAttackEvent event) {
        if (crit && (this == event.source.getSourceOfDamage())) {
            crit = false;
            event.setCanceled(true);
            event.entity.attackEntityFrom(event.source, event.ammount);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Hack to determine when the arrow has hit the ground. inGround is a private field.
        // Access transformers can be used for this, but they're are annoying to deal with and they aren't always safe.
        // However, instead we can take advantage of the fact that arrowShake is always set to 7 after an arrow has hit the ground.
        // inGround is used to store this information.
        if (arrowShake == 7) {
            inGround = true;
            canBePickedUp = 0;
        }

        if (inGround) {
            groundTicks++;

            if (groundTicks <= 2) {
                worldObj.spawnParticle("snowballpoof", posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            }

            /*
             * if (Block.isEqualTo(test, Blocks.water)) {
             * this.worldObj.setBlockMetadataWithNotify(tempThingX, tempThingY, tempThingZ,
             * Block.getIdFromBlock(Blocks.ice), 3); }
             */

            if (groundTicks <= 30) {
                worldObj.spawnParticle("splash", posX, posY - 0.3D, posZ, 0.0D, 0.0D, 0.0D);
            }

            //if (this.ticksInGround >= 64 && this.ticksInGround <= 65) //Why was this the original logic?
            /** Responsible for adding snow layers on top the block the arrow hits, or "freezing" the water it's in by setting the block to ice. */
            if (groundTicks == 64) {
                final int arrX = MathHelper.floor_double(posX);
                final int arrY = MathHelper.floor_double(posY);
                final int arrZ = MathHelper.floor_double(posZ);
                /* TODO Verify that this is the right block!
                 * Also, why does this sometimes set multiple blocks? It's the correct behavior of the original mod, but it's concerning... */
                final Block inBlock = worldObj.getBlock(arrX, arrY, arrZ);

                //if (Block.isEqualTo(this.field_145790_g, Blocks.snow)) {
                /* TODO Possibly implement incrementing snow layers. */
                if (Block.isEqualTo(inBlock, Blocks.air)) {
                    worldObj.setBlock(arrX, arrY, arrZ, Blocks.snow_layer);
                }

                if (Block.isEqualTo(inBlock, Blocks.water)) {
                    /* TODO Check if the earlier event or this one is the correct one.
                     * Also: bouncy arrow on ice, a bit like stone skimming? Could be cool. */
                    worldObj.setBlock(arrX, arrY, arrZ, Blocks.ice);
                }

                // It's actually still in the ground but we just don't care anymore.
                inGround = false;
                setDead();
            }
        } else if (crit && !worldObj.isRemote) { //TODO: Replace with sided proxy, make sure you're actually just supposed to spawn particles on server
            for (int i = 0; i < 4; ++i) {
                worldObj.spawnParticle("crit", posX + ((motionX * i) / 4.0D), posY + ((motionY * i) / 4.0D), posZ + ((motionZ * i) / 4.0D), -motionX, -motionY + 0.2D, -motionZ);
            }
        }
    }

    @Override
    public void setDead() {
        MinecraftForge.EVENT_BUS.unregister(this);
        super.setDead();
    }

    /** This is not what it looks like! See {@code getIsCritical()} for the explanation. */
    @Override
    public void setIsCritical(boolean crit) {
        /* This line of code brings a tear to my eye. It's glorious. */
        super.setIsCritical(this.crit = crit);
        //iDiamondhunter.morebows.MoreBows.modLog.info("setIsCritical " + crit);
    }
}