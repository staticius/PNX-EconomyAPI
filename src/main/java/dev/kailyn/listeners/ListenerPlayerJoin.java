package dev.kailyn.listeners;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import dev.kailyn.database.DatabaseManage;

public class ListenerPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            // Oyuncunun veritabanında kayıtlı olup olmadığını kontrol et
            if (!DatabaseManage.isPlayerRegistered(player.getName())) {
                // Kayıt yoksa oyuncuyu ekle ve mesaj gönder
                DatabaseManage.createPlayerAccount(player.getName());
                player.sendMessage("§aHoşgeldiniz! Yeni oyuncu kaydınız oluşturuldu.");
            } else {
                // Kayıt varsa farklı bir mesaj gönder
                player.sendMessage("§eTekrar hoşgeldiniz, " + player.getName() + "!");
            }
        } catch (Exception e) {
            player.sendMessage("§cBir hata meydana geldi, lütfen daha sonra tekrar deneyin.");
            Server.getInstance().getLogger().error(e.getMessage());
        }
    }

}
