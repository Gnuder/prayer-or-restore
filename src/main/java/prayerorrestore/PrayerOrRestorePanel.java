package prayerorrestore;

import lombok.SneakyThrows;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Singleton
class PrayerOrRestorePanel extends PluginPanel
{
    private final JPanel mainPanel = new JPanel(new GridBagLayout());
    private final JPanel header = new JPanel();
    private final GridBagConstraints constraints = new GridBagConstraints();
    public JLabel nameLabel;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;

    public PrayerOrRestorePanel()
    {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        header.setLayout(new BorderLayout());
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(58, 58, 58)),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)));

        nameLabel = new JLabel("Price Per Dose");
        nameLabel.setForeground(Color.WHITE);

        header.add(nameLabel, BorderLayout.CENTER);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(mainPanel)
                .addComponent(header)
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(header)
                .addGap(10)
                .addComponent(mainPanel)
        );
    }

    @Override
    public void onActivate() {
        Map<Pair<String, Integer>, Integer> nameAndIdToPricePerDose = new HashMap<>();

        SwingUtilities.invokeLater(() ->
                {
                    mainPanel.removeAll();

                    //Prayer Pots 1 through 4
                    nameAndIdToPricePerDose.put(Pair.of("Prayer potion(1)", 143), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(143, true)), 1));
                    nameAndIdToPricePerDose.put(Pair.of("Prayer potion(2)", 141), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(141, true)), 2));
                    nameAndIdToPricePerDose.put(Pair.of("Prayer potion(3)", 139), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(139, true)), 3));
                    nameAndIdToPricePerDose.put(Pair.of("Prayer potion(4)", 2434), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(2434, true)), 4));
                    //Super Restores 1 through 4
                    nameAndIdToPricePerDose.put(Pair.of("Super restore(1)", 3030), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(3030, true)), 1));
                    nameAndIdToPricePerDose.put(Pair.of("Super restore(2)", 3028), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(3028, true)), 2));
                    nameAndIdToPricePerDose.put(Pair.of("Super restore(3)", 3026), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(3026, true)), 3));
                    nameAndIdToPricePerDose.put(Pair.of("Super restore(4)", 3024), calculatePricePerDose(runOnClientThread(() -> itemManager.getItemPriceWithSource(3024, true)), 4));

                    nameAndIdToPricePerDose.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(ent ->  {
                        mainPanel.add(generatePanel(ent.getKey().getLeft(), ent.getValue(), itemManager.getImage(ent.getKey().getRight())), constraints);
                        constraints.gridy++;
                    }
                    );
                    header.revalidate();
                    header.repaint();
                }
        );
    }

    private int calculatePricePerDose(int price, int doseCount) {
        return Math.round((float) price /doseCount);
    }

    @SneakyThrows
    private <T> T runOnClientThread(Callable<T> method) {
        final FutureTask<?> task = new FutureTask<Object>(method::call);
        clientThread.invoke(task);
        return (T) task.get();
    }

    private JPanel generatePanel(String itemName, Integer itemPrice, AsyncBufferedImage itemImage) {
        JPanel newPanel = new JPanel();
        newPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        newPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        GroupLayout layout = new GroupLayout(newPanel);
        newPanel.setLayout(layout);

        JLabel itemNameComponent = new JLabel(itemName);
        itemNameComponent.setFont(FontManager.getRunescapeFont());

        String itemPriceString = QuantityFormatter.quantityToStackSize(itemPrice);
        JLabel itemPriceComponent = new JLabel(StringUtils.capitalize(itemPriceString));
        itemPriceComponent.setForeground(Color.YELLOW);
        itemPriceComponent.setFont(FontManager.getRunescapeFont());
        itemPriceComponent.setToolTipText("Price per dose: " + NumberFormat.getNumberInstance(Locale.US).format(itemPrice));

        JLabel itemImageComponent = new JLabel();
        itemImage.addTo(itemImageComponent);

        layout.setVerticalGroup(layout.createParallelGroup()
                .addComponent(itemImageComponent)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(itemNameComponent)
                        .addComponent(itemPriceComponent)
                )

        );

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(itemImageComponent)
                .addGap(8)
                .addGroup(layout.createParallelGroup()
                        .addComponent(itemNameComponent)
                        .addComponent(itemPriceComponent)
                )
        );

        return newPanel;
    }
}