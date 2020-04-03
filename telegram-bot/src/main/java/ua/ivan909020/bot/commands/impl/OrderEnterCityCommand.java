package ua.ivan909020.bot.commands.impl;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.ivan909020.bot.commands.Command;
import ua.ivan909020.bot.commands.Commands;
import ua.ivan909020.bot.domain.entities.Order;
import ua.ivan909020.bot.domain.models.MessageSend;
import ua.ivan909020.bot.services.ClientService;
import ua.ivan909020.bot.services.OrderStepService;
import ua.ivan909020.bot.services.TelegramService;
import ua.ivan909020.bot.services.impl.ClientServiceDefault;
import ua.ivan909020.bot.services.impl.OrderStepServiceDefault;
import ua.ivan909020.bot.services.impl.TelegramServiceDefault;
import ua.ivan909020.bot.utils.KeyboardUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderEnterCityCommand implements Command {

    private static final OrderEnterCityCommand INSTANCE = new OrderEnterCityCommand();

    private final TelegramService telegramService = TelegramServiceDefault.getInstance();
    private final ClientService clientService = ClientServiceDefault.getInstance();
    private final OrderStepService orderStepService = OrderStepServiceDefault.getInstance();

    private static final Pattern CITY_PATTERN = Pattern.compile("[a-zA-Z]");

    private OrderEnterCityCommand() {
    }

    public static OrderEnterCityCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(Long chatId) {
        clientService.setActionForChatId(chatId, "order=enter-client-city");
        telegramService.sendMessage(new MessageSend(chatId, "Enter your city", createKeyboard(false)));
        sendCurrentCity(chatId);
    }

    private void sendCurrentCity(Long chatId) {
        Order order = orderStepService.findCachedOrderByChatId(chatId);
        if (order != null && order.getClient() != null && order.getClient().getCity() != null) {
            telegramService.sendMessage(new MessageSend(chatId,
                    "Current city: " + order.getClient().getCity(), createKeyboard(true)));
        }
    }

    private ReplyKeyboardMarkup createKeyboard(boolean skipStep) {
        return KeyboardUtils.create(new ArrayList<KeyboardRow>() {{
            if (skipStep) {
                add(new KeyboardRow() {{
                    add(new KeyboardButton(Commands.ORDER_NEXT_STEP_COMMAND));
                }});
            }
            add(new KeyboardRow() {{
                add(new KeyboardButton(Commands.ORDER_CANCEL_COMMAND));
                add(new KeyboardButton(Commands.ORDER_PREVIOUS_STEP_COMMAND));
            }});
        }});
    }

    public void doEnterCity(Long chatId, String city) {
        Matcher matcher = CITY_PATTERN.matcher(city);
        if (!matcher.find()) {
            telegramService.sendMessage(new MessageSend(chatId, "You entered the incorrect city, try again."));
            return;
        }
        Order order = orderStepService.findCachedOrderByChatId(chatId);
        if (order != null && order.getClient() != null) {
            order.getClient().setCity(city);
            orderStepService.updateCachedOrder(chatId, order);
        }
        orderStepService.nextOrderStep(chatId);
    }

}
