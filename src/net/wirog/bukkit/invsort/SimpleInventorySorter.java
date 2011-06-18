package net.wirog.bukkit.invsort;

import java.util.logging.Logger;

import net.wirog.bukkit.invsort.event.PlayerListener;
import net.wirog.bukkit.invsort.event.PlayerListener.SortCommand;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 一定数以上のプレイヤーがベッドに入ったら、全員寝ていなくても朝にするプラグイン。
 * 
 * @author wiro
 */
public class SimpleInventorySorter extends JavaPlugin  {
	/**
	 * onEnable
	 */
	@Override
	public void onEnable() {
		// イベントリスナを登録
		PlayerListener playerListener = new PlayerListener();
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		
		// コマンド
		SortCommand cmd = playerListener.new SortCommand();
		getCommand("sort").setExecutor(cmd);
		getCommand("sortme").setExecutor(cmd);

		// ログ表示
		Logger logger = Logger.getLogger(getDescription().getName());
		logger.info(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled.");
	}
	
	/**
	 * onDisable
	 */
	@Override
	public void onDisable() {
	}
}
