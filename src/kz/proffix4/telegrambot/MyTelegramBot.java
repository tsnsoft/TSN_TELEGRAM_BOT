package kz.proffix4.telegrambot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyTelegramBot extends TelegramLongPollingBot {
    
    // Метод получения команд бота, тут ничего не трогаем
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText(doCommand(update.getMessage().getText()));
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
            }
        }
    }

    // Тут задается нужное значение имени бота
    @Override
    public String getBotUsername() {
        return "Razetkabot";
    }

    // Тут задается нужное значение токена
    @Override
    public String getBotToken() {
        return "581557373:AAFR0TRCM-skKyUrD91uCSadtfiISN0yWOs";
    }
    
    // Метод обработки команд бота
    public String doCommand(String command) {
        // Получение значения биткоина через Http и библиотеку JSON
        if (command.equals("/btc")) {
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

                    String bitcoin_currency = "1 Bitcoin\nUSD: " + usd + ";\nGBP: " + gbp + ";\nEUR: " + eur;

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
        return "Используйте команду /btc";

    }
    
}