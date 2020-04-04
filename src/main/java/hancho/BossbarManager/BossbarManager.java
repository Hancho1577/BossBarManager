package hancho.BossbarManager;

import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.DummyBossBar.Builder;

public class BossbarManager extends PluginBase {
	public LinkedHashMap<String, Integer> timeStamps = new LinkedHashMap<String, Integer>();
	public LinkedHashMap<String, Integer> timeStampsCustom = new LinkedHashMap<String, Integer>();
	public LinkedHashMap<String, Long> bossBars = new LinkedHashMap<String, Long>();
	public LinkedHashMap<String, Long> customBossBars = new LinkedHashMap<String, Long>();

	@Override
	public void onEnable() {
		this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, new AsyncTask() {

			@Override
			public void onRun() {
				removeTask();
			}
		}, 20 * 20, 20 * 11, true);
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
	}
	
	/* 모든 플레이어에게 보스바 생성 */
	public void createBossbar(String content) {
		this.getServer().getOnlinePlayers().forEach((uuid, player) -> {
			createBossbarToPlayer(player, content);
		});
	}
	
	/*모든 플레이어에게 두번째 보스바 생성*/
	public void createCustomBossbar(String content) {
		this.getServer().getOnlinePlayers().forEach((uuid, player) -> {
			createSecondBossbar(player, content);
		});
	}
	
	//특정플레이어에게 두번째 보스바 생성
	public void createBossbarToPlayer(Player player, String content) {
		if (this.timeStamps.containsKey(player.getName())) {
			removeBossbar(player);
		}
		DummyBossBar bossBar = new Builder(player).text(content).build();

		player.createBossBar(bossBar);
		this.bossBars.put(player.getName(), bossBar.getBossBarId());
		this.timeStamps.put(player.getName(), (int) (System.currentTimeMillis() / 1000));
	}
	
	//특정 플레이어에게 두번째 보스바 생성
	public void createSecondBossbar(Player player, String content) {
		if (this.timeStampsCustom.containsKey(player.getName())) {
			removeSecondBossbar(player);
		}
		DummyBossBar bossBar = new Builder(player).text(content).build();

		player.createBossBar(bossBar);
		this.customBossBars.put(player.getName(), bossBar.getBossBarId());
		this.timeStampsCustom.put(player.getName(), (int) (System.currentTimeMillis() / 1000));
	}
	
	//기존 보스바 제거 후 생성
	public void editSecondBossBar(Player player, String content) {
		if (this.customBossBars.containsKey(player.getName())) {
			DummyBossBar bossBar = player.getDummyBossBar(this.customBossBars.get(player.getName()));
			if (bossBar != null) {
				bossBar.destroy();
				this.customBossBars.remove(player.getName());
			}
		}
		this.createSecondBossbar(player, content);
	}

	

	public void removeBossbar(Player player) {
		if (bossBars.containsKey(player.getName()) != true) {
			return;
		}
		player.removeBossBar(bossBars.get(player.getName()));
		this.bossBars.remove(player.getName());
		this.timeStamps.remove(player.getName());
	}

	public void removeSecondBossbar(Player player) {
		if (customBossBars.containsKey(player.getName()) != true) {
			return;
		}
		player.removeBossBar(customBossBars.get(player.getName()));
		this.bossBars.remove(player.getName());
		this.timeStampsCustom.remove(player.getName());
	}

	public void removeTask() {
		int millis = (int) (System.currentTimeMillis() / 1000);
		getServer().getOnlinePlayers().forEach((uuid, pl) -> {
			if (timeStamps.containsKey(pl.getName())) {
				if (millis - timeStamps.get(pl.getName()) > 10) {
					this.removeBossbar(pl);
				}
			}

			if (timeStampsCustom.containsKey(pl.getName())) {
				if (millis - timeStampsCustom.get(pl.getName()) > 10) {
					this.removeSecondBossbar(pl);
				}
			}
		});
	}

}
