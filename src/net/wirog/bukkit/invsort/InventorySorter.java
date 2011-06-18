package net.wirog.bukkit.invsort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * インベントリをソートするためのクラス
 * 
 * @author wiro
 */
public class InventorySorter {
	
	// ダメージ値を持つブロック
	private static final List<Material> usesDamages = new ArrayList<Material>();
	{
		usesDamages.add(Material.WOOD);
		usesDamages.add(Material.COAL);
		usesDamages.add(Material.JUKEBOX);
		usesDamages.add(Material.SAPLING);
		usesDamages.add(Material.WOOL);
		usesDamages.add(Material.INK_SACK);
		usesDamages.add(Material.STEP);
	}

	/**
	 * 指定したチェストをソートします。
	 * 
	 * @param target チェスト
	 */
	public void sort(Chest target) {
		Chest pair = getPairChest(target, +1);
		if (pair != null)
			sortDoubleChest(target, pair);
		else {
			pair = getPairChest(target, -1);
			if (pair != null)
				sortDoubleChest(pair, target);
			else
				sortSingleChest(target);
		}
	}
	
	/**
	 * インベントリをソートします。
	 * 
	 * @param inv インベントリ
	 */
	public void sort(Inventory inv) {
		ItemStack[] sorted = sortContents(inv.getContents());
		inv.setContents(sorted);
	}
	
	/**
	 * 指定したプレイヤーのインベントリをソートします。
	 * このメソッドは、プレイヤーのショートカット内のアイテムはソートしません。
	 * 
	 * @param player プレイヤー
	 */
	public void sort(Player player) {
		Inventory inv = player.getInventory();
		ItemStack[] items = inv.getContents();
		ItemStack[] sorted = new ItemStack[items.length - 9];
		System.arraycopy(items, 9, sorted, 0, items.length - 9);
		sorted = sortContents(sorted);
		System.arraycopy(sorted, 0, items, 9, items.length - 9);
		inv.setContents(items);
	}
	
	/**
	 * シングルチェストをソート
	 */
	private void sortSingleChest(Chest c) {
		sort(c.getInventory());
	}
	
	/**
	 * ダブルチェストをソート
	 * 
	 * @param c1
	 * @param c2
	 */
	private void sortDoubleChest(Chest c1, Chest c2) {
		Inventory inv1 = c1.getInventory(), inv2 = c2.getInventory();
		ItemStack[] con1 = inv1.getContents(), con2 = inv2.getContents();
		ItemStack[] contents = new ItemStack[con1.length + con2.length];
		System.arraycopy(con1, 0, contents, 0, con1.length);
		System.arraycopy(con2, 0, contents, con1.length, con2.length);
		
		sortContents(contents);
		
		System.arraycopy(contents, 0, con1, 0, con1.length);
		System.arraycopy(contents, con1.length, con2, 0, con2.length);
		inv1.setContents(con1);
		inv2.setContents(con2);
		c1.update();
		c2.update();
	}

	/**
	 * 指定したチェストとペアになっているチェストを取得します。
	 * 
	 * @param chest チェスト
	 * @param off 調べる方向 +1 or -1
	 * @return ペアのチェストもしくはnull
	 */
	private Chest getPairChest(Chest chest, int off) {
		World world = chest.getWorld();
		int x = chest.getX(), y = chest.getY(), z = chest.getZ();
		
		Block b = world.getBlockAt(x + off, y, z);
		if (b.getType() == Material.CHEST)
			return (Chest)b.getState();
		
		b = world.getBlockAt(x, y, z + off);
		if (b.getType() == Material.CHEST)
			return (Chest)b.getState();
		
		return null;
	}
	
	/**
	 * ソートします。
	 * 
	 * @param items
	 * @return
	 */
	private ItemStack[] sortContents(ItemStack[] items) {
		Arrays.sort(stackItems(items), new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack o1, ItemStack o2) {
				if (o1 == null && o2 != null) return  1;
				if (o2 == null && o1 != null) return -1;
				if (o1 == null && o2 == null) return 0;
				
				int r = o1.getTypeId() - o2.getTypeId();
				if (r == 0) {
	                if (usesDamages.contains(o1))
	                	return o2.getDurability() - o1.getDurability();
	                else
	                	return o2.getAmount() - o1.getAmount();
				}
				return r;
			}
		});
		return items;
	}

	/**
	 * バラバラに配置されているアイテムを可能なかぎりスタックします。
	 * 
	 * @param items
	 * @return
	 */
	private ItemStack[] stackItems(ItemStack[] items) {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] == null) continue;
			
			int need = items[i].getMaxStackSize() - items[i].getAmount();
			if (need > 0) {
				// 探してスタック
				for (int j = i + 1; j < items.length; ++j) {
					if (items[j] == null) continue;
					
					// 同じの見つけた
					if (isSameType(items[i], items[j])) {
						int a2 = items[j].getAmount();
						if (need < a2) {
							// おすそ分け
							items[i].setAmount(items[i].getMaxStackSize());
							items[j].setAmount(a2 - need);
							break;
						} else {
							// くっつける
							items[i].setAmount(items[i].getAmount() + a2);
							items[j] = null;

							need = items[i].getMaxStackSize() - items[i].getAmount();
						}
					}
				}
			}
		}
		return items;
	}
	
	private boolean isSameType(ItemStack i1, ItemStack i2) {
		return i1.getType() == i2.getType() && (!usesDamages.contains(i1.getType()) || i1.getDurability() == i2.getDurability());
	}
}
