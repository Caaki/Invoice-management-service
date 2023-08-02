package com.app.ares.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;
public class SmsUtils {

    public static final String FROM_NUMBER ="whatsapp:+14155238886";
    public static final String SID_KEY = "ACe44894d9c985920dad2b5390f9893313";
    public static final String TOKEN_KEY= "a13726efe205f2f7aae7a561caf985ad";

    public static void sendSMS(String to, String messageBody){
        Twilio.init(SID_KEY,TOKEN_KEY);
        Message message = creator(
                new PhoneNumber("whatsapp:+381" + to),
                new PhoneNumber(FROM_NUMBER),
                messageBody)
                .create();
    }

}
