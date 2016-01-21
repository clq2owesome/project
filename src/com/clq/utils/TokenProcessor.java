package com.clq.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.clq.common.Constants;


/**
 * Token（令牌管理类）
 * @author chenliqiang
 * @update date 2015-07-16
 */
public class TokenProcessor {

    private static TokenProcessor instance = new TokenProcessor();

    public static TokenProcessor getInstance() {
        return instance;
    }

    protected TokenProcessor() {
        super();
    }

    public synchronized boolean isTokenValid(HttpServletRequest request, String token) {
        return this.isTokenValid(request, false, token);
    }
    
    public synchronized boolean isTokenValid(
        HttpServletRequest request,
        boolean reset, String token) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String saved = (String) session.getAttribute(Constants.TOKEN_KEY);
        if (saved == null) {
            return false;
        }

        if (reset) {
            this.resetToken(request);
        }

        return saved.equals(token);
    }

    /**
     * Reset the saved transaction token in the user's session.  This
     * indicates that transactional token checking will not be needed
     * on the next request that is submitted.
     *
     * @param request The servlet request we are processing
     */
    public synchronized void resetToken(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(Constants.TOKEN_KEY);
    }

    public synchronized boolean saveToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = generateToken(request);
        if (token != null) {
            session.setAttribute(Constants.TOKEN_KEY, token);
            return true;
        }
        return false;
    }

    public String generateToken(HttpServletRequest request) {

        HttpSession session = request.getSession();
        try {
            byte id[] = session.getId().getBytes();
            byte now[] = new Long(System.currentTimeMillis()).toString().getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(id);
            md.update(now);
            return this.toHex(md.digest());

        } catch (IllegalStateException e) {
            return null;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }

    public String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer();
        String s = null;
        for (int i = 0; i < buffer.length; i++) {
            s = Integer.toHexString((int) buffer[i] & 0xff);
            if (s.length() < 2) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
    }

}

