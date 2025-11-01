package com.example.trades.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
/**
 * Represents the outbound platform trade structure:
 * {
 *   "platform_id": "ACCT123",
 *   "trade": {
 *     "account": "***1234",
 *     "security": "ABC123",
 *     "type": "B",
 *     "amount": 100000,
 *     "timestamp": "2025-08-04T21:15:33Z"
 *   }
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlatformTrade {
    @JsonProperty("platform_id")
    private String platformId;

    @JsonProperty("trade")
    private Trade trade;

    public PlatformTrade() { }

    public PlatformTrade(String platformId, Trade trade) {
        this.platformId = platformId;
        this.trade = trade;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public static PlatformTrade of(String platformId,
                                   String accountNumber,
                                   String security,
                                   String type,
                                   long amount,
                                   String timestamp) {
        return new PlatformTrade(platformId,
                new Trade(maskAccount(accountNumber), security, type, amount, timestamp));
    }

    private static String maskAccount(String account) {
        if (account == null) return null;
        String trimmed = account.trim();
        int len = trimmed.length();
        if (len <= 4) {
            return "***" + trimmed;
        }
        String last4 = trimmed.substring(len - 4);
        return "***" + last4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlatformTrade that = (PlatformTrade) o;
        return Objects.equals(platformId, that.platformId) &&
                Objects.equals(trade, that.trade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platformId, trade);
    }

    @Override
    public String toString() {
        return "PlatformTrade{" +
                "platformId='" + platformId + '\'' +
                ", trade=" + trade +
                '}';
    }

    public static class Trade {
        private String account;
        private String security;
        private String type;
        private long amount;
        private String timestamp;

        public Trade() { }

        public Trade(String account, String security, String type, long amount, String timestamp) {
            this.account = account;
            this.security = security;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getSecurity() {
            return security;
        }

        public void setSecurity(String security) {
            this.security = security;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Trade)) return false;
            Trade trade = (Trade) o;
            return amount == trade.amount &&
                    Objects.equals(account, trade.account) &&
                    Objects.equals(security, trade.security) &&
                    Objects.equals(type, trade.type) &&
                    Objects.equals(timestamp, trade.timestamp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(account, security, type, amount, timestamp);
        }

        @Override
        public String toString() {
            return "Trade{" +
                    "account='" + account + '\'' +
                    ", security='" + security + '\'' +
                    ", type='" + type + '\'' +
                    ", amount=" + amount +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }
    }
}
