package prayerorrestore;

import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@PluginDescriptor(
		name = "Prayer or Super Restore",
		description = "Displays current wiki price per dose of prayer potions and super restores."
)
public class PrayerOrRestorePlugin extends Plugin
{
	@Inject
	private ClientToolbar pluginToolbar;
	private NavigationButton navButton;
	private PrayerOrRestorePanel prayerOrRestorePanel;

	@Override
	protected void startUp()
	{
		prayerOrRestorePanel = injector.getInstance(PrayerOrRestorePanel.class);

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "prayerOrRestoreIcon.png");

		navButton = NavigationButton.builder()
				.tooltip("Prayer or Restore")
				.priority(6)
				.icon(icon)
				.panel(prayerOrRestorePanel)
				.build();
		pluginToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown()
	{
		pluginToolbar.removeNavigation(navButton);
	}
}