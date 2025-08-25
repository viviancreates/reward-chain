package com.example.reward_chain.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@Profile({"default","mock"}) // only active in dev/mock profiles
public class MnemonicGeneratorService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final String[] WORDS = {
            "river","orange","window","planet","gentle","silver",
            "garden","butter","canyon","velvet","rocket","puzzle",
            "quiet","maple","shadow","harbor","noble","ember",
            "voyage","lunar","cedar","crystal","pearl","meadow"
    };

    public String generateMnemonicPhrase() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            if (i > 0) sb.append(' ');
            sb.append(WORDS[RNG.nextInt(WORDS.length)]);
        }
        return sb.toString();
    }
}
