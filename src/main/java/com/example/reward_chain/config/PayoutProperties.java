package com.example.reward_chain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payout")
public class PayoutProperties {
    private boolean enabled;
    private String network;
    private String address;
    private String privateKey;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getNetwork() { return network; }
    public void setNetwork(String network) { this.network = network; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
}
