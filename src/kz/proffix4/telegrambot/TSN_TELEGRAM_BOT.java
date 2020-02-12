package kz.proffix4.telegrambot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

class MyTelegramBot extends TelegramLongPollingBot {

    // Метод получения команд бота, тут ничего не трогаем
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(doCommand(update.getMessage().getChatId(),
                    update.getMessage().getText()));
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
            }
        }
    }

    // Тут задается нужное значение имени бота
    @Override
    public String getBotUsername() {
        return "TSN_SUPPER_BOT";
    }

    // Тут задается нужное значение токена
    @Override
    public String getBotToken() {
        return "1061349748:AAFeINsq9jKBXLDXSatTn97RUWQ6NZ96EXU";
    }

    // Метод обработки команд бота
    public String doCommand(long chatId, String command) {
        if (command.startsWith("/btc")) {
            try {
                sendPhoto(new SendPhoto().setChatId(chatId).setNewPhoto(new File("btc.png")));
            } catch (TelegramApiException e) {
            }
            String[] param = command.split(" ");
            if (param.length > 1) {
                return "Привет, " + param[1] + "!\n" + getBTC();
            } else {
                return getBTC();
            }
        }
        return "Используйте команду /btc Ваше_Имя. Можно без имени";
    }

    private String getBTC() {
        // Получение значения биткоина через Http и библиотеку JSON
        HttpURLConnection connection = null;
        String coin_url = "https://api.coindesk.com/v1/bpi/currentprice.json";
        String query = coin_url;
        try {
            connection = (HttpURLConnection) new URL(query).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            int response = connection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                System.out.println(response);
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }
                } catch (IOException e) {
                }
                JSONObject obj = new JSONObject(data.toString());
                // USD
                JSONObject bpi = (JSONObject) obj.get("bpi");
                JSONObject USD = (JSONObject) bpi.get("USD");
                String usd = USD.get("rate").toString();
                // GBP
                JSONObject GBP = (JSONObject) bpi.get("GBP");
                String gbp = GBP.get("rate").toString();
                //EUR
                JSONObject EUR = (JSONObject) bpi.get("EUR");
                String eur = EUR.get("rate").toString();
                String bitcoin_currency = "1 Bitcoin=\nUSD: " + usd + ";\nGBP: " + gbp + ";\nEUR: " + eur;
                return bitcoin_currency;
            } else {
                return "Error";
            }
        } catch (Exception ignored) {
        } finally {
            connection.disconnect();
        }
        return "Error";
    }
}

public class TSN_TELEGRAM_BOT {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            // ЗАПУСКАЕМ КЛАСС НАШЕГО БОТА
            botsApi.registerBot(new MyTelegramBot());
        } catch (TelegramApiException e) {
        }
    }

}
