package com.team100.kite_master.messages;

public interface OutputHandler {

    void output(final String username, final String text);

    void setErrorText(String errorText);
}
