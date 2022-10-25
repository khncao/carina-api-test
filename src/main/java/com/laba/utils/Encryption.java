package com.laba.utils;

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qaprosoft.carina.core.foundation.commons.SpecialKeywords;
import com.qaprosoft.carina.core.foundation.crypto.CryptoTool;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.R;

public class Encryption {
    private final static Logger logger = LogManager.getLogger(Encryption.class);
    public final static String CRYPTO_KEY_PATH = "crypto_key_path";
    public final static Pattern CRYPTO_PATTERN = Pattern.compile(SpecialKeywords.CRYPT);
    public final static String CRYPTO_WRAPPER = "{crypt:%s}";
    private static CryptoTool cryptoTool;
    private static Encryption instance;

    private Encryption() {
        cryptoTool =  new CryptoTool(R.CONFIG.get(CRYPTO_KEY_PATH));
    }

    public static Encryption getInstance() {
        if(instance == null) {
            instance = new Encryption();
        }
        return instance;
    }

    public String encryptAndPrintConsole(String strToEncrypt) {
        String result = cryptoTool.encrypt(strToEncrypt);
        System.out.println(result); // feels more secure than logger
        return result;
    }

    public String decrypt(String strToDecrypt) {
        String result = cryptoTool.decryptByPattern(strToDecrypt, CRYPTO_PATTERN);
        return result;
    }

    public String decryptEnvProperty(String key) {
        return decrypt(Configuration.getEnvArg(key));
    }
}
