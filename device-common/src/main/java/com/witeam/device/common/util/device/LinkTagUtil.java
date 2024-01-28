package com.witeam.device.common.util.device;

/**
 * @author gow 2024/01/23
 */

import org.apache.commons.lang3.RandomStringUtils;

public class LinkTagUtil {
    public LinkTagUtil() {
    }

    public static String createLinkTag(String clientIdentifier, String username) {
        return username + "##" + clientIdentifier;
    }

    public static String mirrorSnToSN(String sn) {
        return sn.startsWith("MIR:") && sn.length() > 8 ? sn.substring(8) : sn;
    }

    public static String snToMirrorSn(String sn) {
        String var10000 = RandomStringUtils.randomAlphanumeric(4);
        return "MIR:" + var10000 + sn;
    }

    public static LinkTagElements parseFromLinkTag(String linkTag) {
        String[] parts = linkTag.split("##");
        if (parts.length == 2) {
            LinkTagElements linkTagElements = new LinkTagElements();
            linkTagElements.setUsername(parts[0]);
            linkTagElements.setClientIdentifier(parts[1]);
            return linkTagElements;
        } else {
            return null;
        }
    }

    public static class LinkTagElements {
        private String clientIdentifier;
        private String username;

        public LinkTagElements() {
        }

        public String getClientIdentifier() {
            return this.clientIdentifier;
        }

        public void setClientIdentifier(String clientIdentifier) {
            this.clientIdentifier = clientIdentifier;
        }

        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}

