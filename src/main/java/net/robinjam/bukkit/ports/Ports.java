package net.robinjam.bukkit.ports;

import net.robinjam.bukkit.ports.commands.ArriveCommand;
import net.robinjam.bukkit.ports.commands.CreateCommand;
import net.robinjam.bukkit.ports.commands.DeleteCommand;
import net.robinjam.bukkit.ports.commands.DescribeCommand;
import net.robinjam.bukkit.ports.commands.DestinationCommand;
import net.robinjam.bukkit.ports.commands.LinkCommand;
import net.robinjam.bukkit.ports.commands.ListCommand;
import net.robinjam.bukkit.ports.commands.PermissionCommand;
import net.robinjam.bukkit.ports.commands.ReloadCommand;
import net.robinjam.bukkit.ports.commands.RenameCommand;
import net.robinjam.bukkit.ports.commands.ScheduleCommand;
import net.robinjam.bukkit.ports.commands.SelectCommand;
import net.robinjam.bukkit.ports.commands.TicketCommand;
import net.robinjam.bukkit.ports.commands.UnlinkCommand;
import net.robinjam.bukkit.ports.commands.UpdateCommand;
import net.robinjam.bukkit.ports.persistence.PersistentLocation;
import net.robinjam.bukkit.ports.persistence.PersistentCuboidRegion;
import net.robinjam.bukkit.ports.persistence.Port;
import net.robinjam.bukkit.util.CommandExecutor;
import net.robinjam.bukkit.util.CommandManager;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * 
 * @author robinjam
 */
public class Ports extends JavaPlugin {
	
	private static Ports instance;
	
	public static Ports getInstance() {
		return instance;
	}

	private CommandManager commandManager;
	private WorldEditPlugin worldEditPlugin;
	private PortTickTask portTickTask = new PortTickTask();
	
	public Ports() {
		instance = this;
	}

	@Override
	public void onEnable() {
		// Hook into WorldEdit
		this.hookWorldEdit();

		// Register events
		getServer().getPluginManager().registerEvents(portTickTask, this);

		// Register commands
		commandManager = new CommandManager();
		commandManager.registerCommands(new CommandExecutor[] {
				new ArriveCommand(), new CreateCommand(),
				new DeleteCommand(), new DescribeCommand(),
				new DestinationCommand(), new LinkCommand(), new ListCommand(),
				new ReloadCommand(), new RenameCommand(),
				new ScheduleCommand(), new SelectCommand(),
				new UnlinkCommand(), new UpdateCommand(),
				new PermissionCommand(), new TicketCommand() });
		this.getCommand("port").setExecutor(commandManager);
		
		// Load port data
		ConfigurationSerialization.registerClass(Port.class);
		ConfigurationSerialization.registerClass(PersistentLocation.class);
		ConfigurationSerialization.registerClass(PersistentCuboidRegion.class);
		Port.load();
		
		// Load config
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Schedule ticket manager
		getServer().getScheduler().scheduleSyncRepeatingTask(this, portTickTask, 0L, getConfig().getLong("port-tick-period"));
	}

	@Override
	public void onDisable() {
		getServer().getScheduler().cancelTasks(this);
	}

	private void hookWorldEdit() {
		Plugin plugin = this.getServer().getPluginManager()
				.getPlugin("WorldEdit");
		this.worldEditPlugin = (WorldEditPlugin) plugin;
	}

	public WorldEditPlugin getWorldEditPlugin() {
		return this.worldEditPlugin;
	}

	public WorldEdit getWorldEdit() {
		return this.worldEditPlugin.getWorldEdit();
	}

}
