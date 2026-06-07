package io.github.cciglesiasmartinez;

import io.github.cciglesiasmartinez.core.IrcServer;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        new IrcServer(6667).start();
    }
}