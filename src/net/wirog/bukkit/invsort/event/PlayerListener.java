package net.wirog.bukkit.invsort.event;

import java.util.Set;
import java.util.TreeSet;

import net.wirog.bukkit.invsort.InventorySorter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 * プレイヤーリスナ
 * 
 * @author wiro
 */
public class PlayerListener extends org.bukkit.event.player.PlayerListener {

	private Set<String> cmdProcessed = new TreeSet<String>();
	private InventorySorter sorter = new InventorySorter();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		Block block = event.getClickedBlock();
		
		if (cmdProcessed.contains(name)) {
			if (block.getType() == Material.CHEST) {
				sorter.sort((Chest)block.getState());
				player.sendMessage(ChatColor.GREEN + "Sortings was complated.");
			} else {
				player.sendMessage(ChatColor.RED + "Sorting was aborted.");
			}
			cmdProcessed.remove(name);
		}
	}
	
	/**
	 * ソートコマンド
	 * 
	 * @author wiro
	 */
	public class SortCommand implements CommandExecutor {
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				if (label.equals("sort")) {
					cmdProcessed.add(player.getName());
					player.sendMessage(ChatColor.GOLD + "Click the chest to be sorted.");
				} else if (label.equals("sortme")) {
					sorter.sort(player);
					player.sendMessage(ChatColor.GREEN + "Your inventory was sorted.");
				}
			}
			return true;
		}
	}
}
