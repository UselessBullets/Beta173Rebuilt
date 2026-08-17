// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.entity;

import org.lwjgl.opengl.GL11;
import net.minecraft.world.level.tile.Tile;
import util.Mth;
import java.util.Iterator;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.item.Boat;
import net.minecraft.world.entity.item.Minecart;
import net.minecraft.world.entity.item.FallingTile;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.Painting;
import net.minecraft.world.entity.Entity;
import net.minecraft.client.model.SquidModel;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.client.model.WolfModel;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.client.model.CowModel;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.client.model.PigModel;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.monster.Spider;
import java.util.HashMap;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.Textures;
import net.minecraft.client.gui.Font;
import java.util.Map;

public class EntityRenderDispatcher
{
    private Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers = new HashMap<>();
    public static EntityRenderDispatcher instance = new EntityRenderDispatcher();
    private Font font;
    public static double xOff;
    public static double yOff;
    public static double zOff;
    public Textures textures;
    public ItemInHandRenderer itemInHandRenderer;
    public Level level;
    public Mob player;
    public float playerRotY;
    public float playerRotX;
    public Options options;
    public double xPlayer;
    public double yPlayer;
    public double zPlayer;
    
    private EntityRenderDispatcher() {
        this.renderers.put(Spider.class, new SpiderRenderer());
        this.renderers.put(Pig.class, new PigRenderer(new PigModel(), new PigModel(0.5f), 0.7f));
        this.renderers.put(Sheep.class, new SheepRenderer(new SheepModel(), new SheepFurModel(), 0.7f));
        this.renderers.put(Cow.class, new CowRenderer(new CowModel(), 0.7f));
        this.renderers.put(Wolf.class, new WolfRenderer(new WolfModel(), 0.5f));
        this.renderers.put(Chicken.class, new ChickenRenderer(new ChickenModel(), 0.3f));
        this.renderers.put(Creeper.class, new CreeperRenderer());
        this.renderers.put(Skeleton.class, new HumanoidMobRenderer<>(new SkeletonModel(), 0.5f));
        this.renderers.put(Zombie.class, new HumanoidMobRenderer<>(new ZombieModel(), 0.5f));
        this.renderers.put(Slime.class, new SlimeRenderer(new SlimeModel(16), new SlimeModel(0), 0.25f));
        this.renderers.put(Player.class, new PlayerRenderer());
        this.renderers.put(Giant.class, new GiantMobRenderer(new ZombieModel(), 0.5f, 6.0f));
        this.renderers.put(Ghast.class, new GhastRenderer());
        this.renderers.put(Squid.class, new SquidRenderer(new SquidModel(), 0.7f));
        this.renderers.put(Mob.class, new MobRenderer<>(new HumanoidModel(), 0.5f));
        this.renderers.put(Entity.class, new DefaultRenderer());
        this.renderers.put(Painting.class, new PaintingRenderer());
        this.renderers.put(Arrow.class, new ArrowRenderer());
        this.renderers.put(Snowball.class, new ItemSpriteRenderer(Item.snowBall.getIcon(0)));
        this.renderers.put(ThrownEgg.class, new ItemSpriteRenderer(Item.egg.getIcon(0)));
        this.renderers.put(Fireball.class, new FireballRenderer());
        this.renderers.put(ItemEntity.class, new ItemRenderer());
        this.renderers.put(PrimedTnt.class, new TntRenderer());
        this.renderers.put(FallingTile.class, new FallingTileRenderer());
        this.renderers.put(Minecart.class, new MinecartRenderer());
        this.renderers.put(Boat.class, new BoatRenderer());
        this.renderers.put(FishingHook.class, new FishingHookRenderer());
        this.renderers.put(LightningBolt.class, new LightningBoltRenderer());

        for (EntityRenderer<? extends Entity> entityRenderer : this.renderers.values()) {
            entityRenderer.init(this);
        }
    }
    
    public <T extends Entity> EntityRenderer<T> getRenderer(final Class<? extends Entity> clazz) {
        EntityRenderer<? extends Entity> r = this.renderers.get(clazz);
        if (r == null && clazz != Entity.class) {
            r = this.getRenderer((Class<? extends T>) clazz.getSuperclass());
            this.renderers.put(clazz, r);
        }
        return (EntityRenderer<T>) r;
    }
    
    public <T extends Entity> EntityRenderer<T> getRenderer(final Entity entity) {
        return this.getRenderer(entity.getClass());
    }
    
    public void prepare(final Level level, final Textures textures, final Font font, final Mob player, final Options options, final float a) {
        this.level = level;
        this.textures = textures;
        this.options = options;
        this.player = player;
        this.font = font;

        if (player.isSleeping()) {
            int t = level.getTile(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));
            if (t == Tile.bed.id) {
                int data = level.getData(Mth.floor(player.x), Mth.floor(player.y), Mth.floor(player.z));

                int direction = data & 0x3;
                this.playerRotY = (float)(direction * 90 + 180);
                this.playerRotX = 0.0f;
            }
        }
        else {
            this.playerRotY = player.yRotO + (player.yRot - player.yRotO) * a;
            this.playerRotX = player.xRotO + (player.xRot - player.xRotO) * a;
        }

        this.xPlayer = player.xOld + (player.x - player.xOld) * a;
        this.yPlayer = player.yOld + (player.y - player.yOld) * a;
        this.zPlayer = player.zOld + (player.z - player.zOld) * a;
    }
    
    public void render(final Entity entity, final float a) {
        final double x = entity.xOld + (entity.x - entity.xOld) * a;
        final double y = entity.yOld + (entity.y - entity.yOld) * a;
        final double z = entity.zOld + (entity.z - entity.zOld) * a;

        final float r = entity.yRotO + (entity.yRot - entity.yRotO) * a;
        final float br = entity.getBrightness(a);
        GL11.glColor3f(br, br, br);

        this.render(entity, x - EntityRenderDispatcher.xOff, y - EntityRenderDispatcher.yOff, z - EntityRenderDispatcher.zOff, r, a);
    }
    
    public void render(final Entity entity, final double x, final double y, final double z, final float rot, final float a) {
        final EntityRenderer<Entity> renderer = this.getRenderer(entity);
        if (renderer != null) {
            renderer.render(entity, x, y, z, rot, a);
            renderer.postRender(entity, x, y, z, rot, a);
        }
    }
    
    public void setLevel(final Level level) {
        this.level = level;
    }
    
    public double distanceToSqr(final double x, final double y, final double z) {
        final double xd = x - this.xPlayer;
        final double yd = y - this.yPlayer;
        final double zd = z - this.zPlayer;
        return xd * xd + yd * yd + zd * zd;
    }
    
    public Font getFont() {
        return this.font;
    }

}
