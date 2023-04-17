package me.neznamy.tab.platforms.sponge8;

import lombok.Getter;
import lombok.NonNull;
import me.neznamy.tab.shared.hook.ViaVersionHook;
import me.neznamy.tab.shared.platform.bossbar.AdventureBossBar;
import me.neznamy.tab.shared.chat.IChatBaseComponent;
import me.neznamy.tab.shared.platform.TabPlayer;
import me.neznamy.tab.shared.platform.tablist.TabList;
import me.neznamy.tab.shared.platform.PlatformScoreboard;
import me.neznamy.tab.shared.TAB;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.profile.property.ProfileProperty;

import java.util.Collections;
import java.util.List;

public final class SpongeTabPlayer extends TabPlayer {

    @Getter private final PlatformScoreboard<SpongeTabPlayer> scoreboard = new SpongeScoreboard(this);
    @Getter private final TabList tabList = new SpongeTabList(this);
    @Getter private final AdventureBossBar bossBar = new AdventureBossBar(getPlayer());

    public SpongeTabPlayer(ServerPlayer player) {
        super(player, player.uniqueId(), player.name(), TAB.getInstance().getConfiguration().getServerName(),
                player.world().key().value(), ViaVersionHook.getInstance().getPlayerVersion(player.uniqueId(), player.name()), true);
    }

    @Override
    public boolean hasPermission(String permission) {
        return getPlayer().hasPermission(permission);
    }

    @Override
    public int getPing() {
        return getPlayer().connection().latency();
    }

    @Override
    public void sendMessage(IChatBaseComponent message) {
        getPlayer().sendMessage(message.toAdventureComponent());
    }

    @Override
    public boolean hasInvisibilityPotion() {
        for (PotionEffect effect : getPlayer().get(Keys.POTION_EFFECTS).orElse(Collections.emptyList())) {
            if (effect.type() == PotionEffectTypes.INVISIBILITY.get()) return true;
        }
        return false;
    }

    @Override
    public boolean isDisguised() {
        return false;
    }

    @Override
    public TabList.Skin getSkin() {
        List<ProfileProperty> list = getPlayer().profile().properties();
        if (list.isEmpty()) return null;
        return new TabList.Skin(list.get(0).value(), list.get(0).signature().orElse(null));
    }

    @Override
    public ServerPlayer getPlayer() {
        return (ServerPlayer) player;
    }

    @Override
    public boolean isOnline() {
        return getPlayer().isOnline();
    }

    @Override
    public boolean isVanished() {
        return getPlayer().vanishState().get().invisible();
    }

    @Override
    public int getGamemode() {
        if (getPlayer().gameMode().get() == GameModes.CREATIVE.get()) return 1;
        if (getPlayer().gameMode().get() == GameModes.ADVENTURE.get()) return 2;
        if (getPlayer().gameMode().get() == GameModes.SPECTATOR.get()) return 3;
        return 0;
    }

    @Override
    public void setPlayerListHeaderFooter(@NonNull IChatBaseComponent header, @NonNull IChatBaseComponent footer) {
        getPlayer().tabList().setHeaderAndFooter(header.toAdventureComponent(), footer.toAdventureComponent());
    }

    public void setPlayer(final ServerPlayer player) {
        this.player = player;
        bossBar.setAudience(player);
    }
}
