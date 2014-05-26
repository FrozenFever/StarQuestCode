package us.higashiyama.george.SQSpace;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SuffocationTask extends BukkitRunnable {
	Player p;
	int OxygenLevel = 0;
	SQSpace plugin;
	boolean canceled = false;

	SuffocationTask(SQSpace plugin, Player p) {
		this.p = p;
		long delay = 20L;
		if (null != p.getInventory().getHelmet()) {
			this.OxygenLevel = p.getInventory().getHelmet()
					.getEnchantmentLevel(Enchantment.OXYGEN);
			switch (this.OxygenLevel) {
			case 3: // Should never trigger - qualifies as hasSpaceHelmet
				delay *= 27;
				// Intentionally use fall-through to create exponential time
				// growth in delay
			case 2:
				delay *= 9;
			case 1:
				delay *= 3;

			default:
				break;
			}
		}
		this.runTaskTimer(plugin, delay, delay);
		this.plugin = plugin;
	}

	public void cancel(Player p) {
		this.cancel();
		this.canceled = true;

		// Clear the player from the list of suffocating players
		SQSpace.Players.remove(p);
		// Check if we need to trigger a different speed suffocation task, otherwise tell them they are not suffocating
		if (!this.plugin.checkIfSuffocating(p))
		{
			p.sendMessage(ChatColor.AQUA + "[Space] " + ChatColor.GREEN
					+ "You are no longer suffocationg!");
		}
	}

	@Override
	public void run() {
		if (!this.plugin.isInSpace(this.p)) {
			this.cancel(this.p);
		}
		if (this.plugin.hasSpaceHelmet(this.p)) {
			this.cancel(this.p);
		}
		if (this.p.isDead()) {
			this.cancel(this.p);
		}
		if ((null != this.p.getInventory().getHelmet())
				&& (this.OxygenLevel != this.p.getInventory().getHelmet().getEnchantmentLevel(Enchantment.OXYGEN))) {
			this.cancel(this.p);
		}
		if (!this.canceled) {
			this.p.damage(1.0D);
		}
	}
}
